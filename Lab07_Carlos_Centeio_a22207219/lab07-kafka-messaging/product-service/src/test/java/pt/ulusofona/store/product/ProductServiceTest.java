package pt.ulusofona.store.product;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
class ProductServiceTest {
 @Test void repeatsAreHarmlessAtRepositoryBoundary() {
   ProductRepository repository=mock(ProductRepository.class);
   when(repository.discontinueActiveBySupplier(java.util.UUID.fromString("00000000-0000-0000-0000-000000000017"))).thenReturn(2,0);
   ProductService service=new ProductService(repository);
   assertEquals(2,service.discontinueSupplier(java.util.UUID.fromString("00000000-0000-0000-0000-000000000017")));
   assertEquals(0,service.discontinueSupplier(java.util.UUID.fromString("00000000-0000-0000-0000-000000000017")));
   verify(repository,times(2)).discontinueActiveBySupplier(java.util.UUID.fromString("00000000-0000-0000-0000-000000000017"));
 }
}
