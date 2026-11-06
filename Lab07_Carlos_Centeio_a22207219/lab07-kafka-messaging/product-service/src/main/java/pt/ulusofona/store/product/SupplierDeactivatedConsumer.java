package pt.ulusofona.store.product;
import tools.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Component
public class SupplierDeactivatedConsumer {
 private final ObjectMapper mapper;
 private final ProductService products;
 public SupplierDeactivatedConsumer(ObjectMapper mapper,ProductService products) { this.mapper=mapper; this.products=products; }
 @KafkaListener(topics="supplier-deactivated-events",groupId="product-service-group")
 public void consume(String json) throws Exception {
   // Repeated delivery is harmless: UPDATE only rows with is_discontinued=false.
   SupplierDeactivatedEvent event=mapper.readValue(json,SupplierDeactivatedEvent.class);
   if (event.supplierId()==null || event.supplierId().equals(new java.util.UUID(0L, 0L))) throw new IllegalArgumentException("Invalid supplierId");
   products.discontinueSupplier(event.supplierId());
 }
}
