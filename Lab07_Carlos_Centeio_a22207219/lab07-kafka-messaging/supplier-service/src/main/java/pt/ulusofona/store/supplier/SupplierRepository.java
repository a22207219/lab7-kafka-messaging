package pt.ulusofona.store.supplier;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SupplierRepository extends JpaRepository<Supplier,UUID> { }
