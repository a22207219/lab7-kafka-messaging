package pt.ulusofona.store.product;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface ProductRepository extends JpaRepository<Product,UUID> {
 @Modifying(clearAutomatically=true,flushAutomatically=true)
 @Query("update Product p set p.discontinued=true where p.supplierId=:supplierId and p.discontinued=false")
 int discontinueActiveBySupplier(@Param("supplierId") UUID supplierId);
}
