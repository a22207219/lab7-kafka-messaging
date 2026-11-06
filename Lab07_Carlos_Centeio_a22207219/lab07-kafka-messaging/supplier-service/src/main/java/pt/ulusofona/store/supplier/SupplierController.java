package pt.ulusofona.store.supplier;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/suppliers")
public class SupplierController {
 private final SupplierService service;
 public SupplierController(SupplierService service) { this.service=service; }
 public record CreateSupplier(@NotBlank String name) { }
 @PostMapping public Supplier create(@Valid @RequestBody CreateSupplier input) { return service.create(input.name()); }
 @GetMapping public List<Supplier> all() { return service.all(); }
 @PatchMapping("/{id}/deactivate") public Supplier deactivate(@PathVariable UUID id) { return service.deactivate(id); }
}
