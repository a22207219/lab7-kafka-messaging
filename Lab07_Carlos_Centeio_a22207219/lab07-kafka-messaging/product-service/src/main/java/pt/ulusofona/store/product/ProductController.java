package pt.ulusofona.store.product;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
 private final ProductService products;
 public ProductController(ProductService products) { this.products=products; }
 public record CreateProduct(@NotBlank String name,@NotNull UUID supplierId) { }
 @PostMapping public Product create(@Valid @RequestBody CreateProduct input) { return products.create(input.name(),input.supplierId()); }
 @GetMapping public List<Product> all() { return products.all(); }
 // Lab 6 discontinue-by-supplier HTTP endpoint is deliberately ABSENT.
}
