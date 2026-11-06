package pt.ulusofona.store.supplier;
import java.util.UUID;
import jakarta.persistence.*;
@Entity
@Table(name="supplier")
public class Supplier {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @Column(nullable=false) private String name;
 @Column(name="is_active", nullable=false) private boolean active=true;
 protected Supplier() { }
 public Supplier(String name) { this.name=name; }
 public UUID getId() { return id; }
 public String getName() { return name; }
 public boolean isActive() { return active; }
 public void deactivate() { active=false; }
}
