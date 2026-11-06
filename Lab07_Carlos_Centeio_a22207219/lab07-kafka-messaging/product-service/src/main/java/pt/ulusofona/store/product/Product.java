package pt.ulusofona.store.product;
import java.util.UUID;
import jakarta.persistence.*;
@Entity
@Table(name="product")
public class Product {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @Column(nullable=false) private String name;
 @Column(name="supplier_id",nullable=false) private UUID supplierId;
 @Column(name="is_discontinued",nullable=false) private boolean discontinued=false;
 protected Product() { }
 public Product(String name,UUID supplierId) { this.name=name;this.supplierId=supplierId; }
 public UUID getId() { return id; }
 public String getName() { return name; }
 public UUID getSupplierId() { return supplierId; }
 public boolean isDiscontinued() { return discontinued; }
}
