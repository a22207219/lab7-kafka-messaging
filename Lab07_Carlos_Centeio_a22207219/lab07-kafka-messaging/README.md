# Lab 07 — Kafka and Messaging

**Aluno:** Carlos Centeio  
**Número de aluno:** a22207219


**Autonomous implementation based on the publicly accessible Lab 7 overview.** This is a standalone teaching reconstruction, not a verified patch of your actual Labs 4–6 repositories. The linked subpages (including Delivery/rubric) were not accessible to the assistant. Do not mistake this repository for a verified exact submission of those pages.

## Included

- Kafka `apache/kafka:4.3.1`, single broker KRaft (no ZooKeeper), persistent volume.
- Kafka UI `kafbat/kafka-ui:v1.5.0` at <http://localhost:8090>.
- Two services on 8082 (supplier) and 8081 (product), each with its own PostgreSQL 18 database.
- `PATCH /api/v1/suppliers/{id}/deactivate` commits the supplier state change and publishes JSON `{ "supplierId": "<uuid>" }` to `supplier-deactivated-events`, key = supplier ID.
- Product Service listener in `product-service-group` sets `is_discontinued=true` for matching products using an idempotent UPDATE. No HTTP call or URL dependency between the two services.
- Java 25, Spring Boot 4.1.0; `spring-kafka` deliberately has no dependency version in Maven.

## Requirements

Docker Desktop + Compose v2. On Windows run in **PowerShell**. Maven and Java are only needed locally if building *outside* Docker.

## Start

```powershell
docker network inspect store-network 2>$null
if ($LASTEXITCODE -ne 0) { docker network create store-network }
docker compose up -d --build
docker compose ps
```

Startup may require image pulls and Maven dependency downloads. Kafbat UI: http://localhost:8090. Kafka is on `kafka:9092` *inside Docker* and `localhost:29092` *on the host*. Product 8081, Supplier 8082.

## Smoke test

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\smoke-test.ps1
powershell -ExecutionPolicy Bypass -File .\scripts\test-without-ui.ps1
```

The smoke script creates a supplier and a product, deactivates the supplier, polls for discontinuation, and asserts the removed HTTP endpoint is unavailable (404/405).

### Manual sample

```powershell
$s = Invoke-RestMethod -Method Post -Uri http://localhost:8082/api/v1/suppliers -ContentType 'application/json' -Body '{"name":"Acme"}'
$p = Invoke-RestMethod -Method Post -Uri http://localhost:8081/api/v1/products -ContentType 'application/json' -Body (@{name='Widget'; supplierId=$s.id} | ConvertTo-Json)
Invoke-RestMethod -Method Patch -Uri "http://localhost:8082/api/v1/suppliers/$($s.id)/deactivate"
Invoke-RestMethod http://localhost:8081/api/v1/products
```

## Failure experiments (perform and record observations)

1. **Product listener offline:** `docker compose stop product-service`; deactivate a supplier; `docker compose start product-service`; inspect products and consumer lag. Kafka retains the event and the service catches up.
2. **Duplicate records:** in Kafbat UI, publish the same JSON payload with the same supplier ID into `supplier-deactivated-events`; the SQL UPDATE leaves already discontinued rows unchanged.
3. **Broker failure:** `docker compose stop kafka`; attempt to deactivate a new supplier; the API may report 503 after broker timeout **even though the DB was updated**. Restart with `docker compose start kafka`. This intentionally demonstrates the dual-write gap, not exactly-once delivery. Do not run this experiment against data you care about.

## Kafka concepts

- A topic is an append-only log of records; offsets identify positions *within a partition*.
- A consumer group distributes partitions among its members; a rebalance reassigns those partitions after membership changes.
- A key makes a supplier's events map consistently to a partition, giving ordering *inside* that partition, not a global ordering guarantee.
- Retention is independent of whether a message has been read. The broker does not delete it on acknowledgment.
- At-least-once means duplicate delivery is possible. This listener is idempotent because `true` remains `true`.
- The DB change and Kafka publish are still two separate writes. A transactional outbox is a future improvement, not part of this Lab 7 implementation.
- JSON is sent as a string and explicitly parsed; there is no need for unsafe `spring.json.trusted.packages: "*"`.

## Differences from the existing course project

This standalone sample creates its own Lab 4-style entities and database schema. If your Labs 4–6 already have Supplier/Product entities and Flyway migrations, **merge only the Kafka changes**. Do not copy these V1/V2/V3 SQL scripts over an established migration history. Preserve previous API contracts and table definitions. The course overview specifically expects `V3__add_is_discontinued_to_product.sql` to already exist from Lab 6.

## Test/build limitations

This environment lacked Docker, Maven, and Java 25; therefore actual container startup and Maven compilation were **not performed** here. The included tests and PowerShell smoke test are designed to be run in your environment; a passing run is required before submission.

## Cleanup

```powershell
docker compose down              # preserves named volumes
docker compose down -v           # destructive: removes Kafka and database data
```

## Lab 07 → Lab 08 upgrade (same Kafka records and database IDs)

Both ZIPs use UUID identifiers, external Docker network `store-network` and the **same named data volumes** for Kafka and the two PostgreSQL databases. Do not run the stacks at the same time (they share published ports). In the Lab 07 folder, run `docker compose down` (without `-v`). Then in the Lab 08 folder, run `docker network create store-network` if needed, followed by `docker compose up -d --build`. Keep the named volumes to preserve old one-field Kafka events and supplier/product records. **Never** use `docker compose down -v` during an upgrade. Existing data made with the original earlier Long-ID ZIP must not be reused without an explicit migration.

Run `powershell -ExecutionPolicy Bypass -File .\scripts\smoke-test.ps1` in the current lab folder. JSON responses expose `discontinued` (Java getter `isDiscontinued()`).

**Verification scope:** This deliverable has undergone static checks, but Docker/Maven integration tests must be run on a host with Docker Desktop and Java 25. Instructor delivery rubric pages were unavailable for direct verification.
