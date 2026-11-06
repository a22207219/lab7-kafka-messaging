package pt.ulusofona.store.product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
@Service
public class ProductService {
 private final ProductRepository products;
 public ProductService(ProductRepository products) { this.products=products; }
 public Product create(String name,UUID supplierId) { return products.save(new Product(name,supplierId)); }
 public List<Product> all() { return products.findAll(); }
 @Transactional public int discontinueSupplier(UUID supplierId) { return products.discontinueActiveBySupplier(supplierId); }
}
