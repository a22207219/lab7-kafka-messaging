package pt.ulusofona.store.supplier;
import java.util.UUID;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;
@Service
public class SupplierService {
 private final SupplierRepository repository;
 private final KafkaTemplate<String,String> kafka;
 private final ObjectMapper mapper;
 private final TransactionTemplate transactions;
 public SupplierService(SupplierRepository repository,KafkaTemplate<String,String> kafka,ObjectMapper mapper,org.springframework.transaction.PlatformTransactionManager tx) {
   this.repository=repository; this.kafka=kafka; this.mapper=mapper; this.transactions=new TransactionTemplate(tx);
 }
 public Supplier create(String name) { return repository.save(new Supplier(name)); }
 public java.util.List<Supplier> all() { return repository.findAll(); }
 public Supplier deactivate(UUID id) {
   // Local DB transaction completes BEFORE the Kafka publish. This intentional Lab 7 dual-write
   // risk is addressed by the outbox in Lab 9, not by a fictitious distributed transaction.
   Result result=transactions.execute(status -> {
     Supplier supplier=repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Supplier not found"));
     if (!supplier.isActive()) return new Result(supplier,false);
     supplier.deactivate(); repository.saveAndFlush(supplier);
     return new Result(supplier,true);
   });
   if (result.publishedNeeded()) {
     try {
       String payload=mapper.writeValueAsString(new SupplierDeactivatedEvent(id));
       // Wait for acknowledgment so HTTP success proves the broker accepted the event.
       kafka.send("supplier-deactivated-events",id.toString(),payload).get();
     } catch (Exception e) {
       if (e instanceof InterruptedException) Thread.currentThread().interrupt();
       throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
         "Supplier deactivated in database, but Kafka publish was not confirmed; requires recovery",e);
     }
   }
   return result.supplier();
 }
 private record Result(Supplier supplier,boolean publishedNeeded) { }
}
