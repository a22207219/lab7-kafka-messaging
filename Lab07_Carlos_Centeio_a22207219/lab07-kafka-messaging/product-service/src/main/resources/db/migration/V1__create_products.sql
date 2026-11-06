CREATE TABLE product (
 id UUID PRIMARY KEY,
 name VARCHAR(255) NOT NULL,
 supplier_id UUID NOT NULL
);
CREATE INDEX idx_product_supplier_id ON product(supplier_id);
