$ErrorActionPreference = "Stop"
function Wait-Ready([string]$url) {
  for ($n=0; $n -lt 50; $n++) {
    try { $r=Invoke-RestMethod -Uri $url -TimeoutSec 5; if ($r.status -eq 'UP') { return } } catch { }
    Start-Sleep -Seconds 3
  }
  throw "Service not healthy: $url"
}
Wait-Ready 'http://localhost:8081/actuator/health'
Wait-Ready 'http://localhost:8082/actuator/health'
$suffix = [guid]::NewGuid().ToString('N').Substring(0,8)
$s = Invoke-RestMethod -Method Post -Uri 'http://localhost:8082/api/v1/suppliers' -ContentType 'application/json' -Body (@{name="supplier-$suffix"} | ConvertTo-Json)
$p = Invoke-RestMethod -Method Post -Uri 'http://localhost:8081/api/v1/products' -ContentType 'application/json' -Body (@{name="product-$suffix";supplierId=$s.id} | ConvertTo-Json)
if ($p.discontinued) { throw 'Product unexpectedly discontinued initially' }
Invoke-RestMethod -Method Patch -Uri "http://localhost:8082/api/v1/suppliers/$($s.id)/deactivate" | Out-Null
$ok=$false
for ($n=0; $n -lt 30; $n++) {
  $p2 = @(Invoke-RestMethod -Method Get -Uri 'http://localhost:8081/api/v1/products') | Where-Object { $_.id -eq $p.id }
  if ($p2 -and $p2.discontinued) { $ok=$true; break }
  Start-Sleep -Seconds 2
}
if (-not $ok) { throw 'FAIL: Product was not discontinued by Kafka event' }
Write-Host "PASS: Supplier $($s.id) deactivated; product $($p.id) discontinued via Kafka." -ForegroundColor Green
# The HTTP endpoint removed by Lab 7 must not exist:
try {
  Invoke-WebRequest -Method Patch -Uri 'http://localhost:8081/api/v1/products/discontinue-by-supplier' -ErrorAction Stop | Out-Null
  throw 'FAIL: Deprecated synchronous endpoint is still available'
} catch {
  if ($_.Exception.Response -and [int]$_.Exception.Response.StatusCode -in @(404,405)) {
    Write-Host 'PASS: deprecated endpoint is absent.' -ForegroundColor Green
  } else { throw }
}
