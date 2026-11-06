package pt.ulusofona.store.supplier;
import java.util.UUID;
/** Lab 7 one-field event; keeps UUID identifiers compatible with Lab 8. */
public record SupplierDeactivatedEvent(UUID supplierId) { }
