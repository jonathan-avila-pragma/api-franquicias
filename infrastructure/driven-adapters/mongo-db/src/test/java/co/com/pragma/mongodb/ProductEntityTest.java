package co.com.pragma.mongodb;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductEntityTest {

    @Test
    void testDefaultConstructor() {
        ProductEntity entity = new ProductEntity();
        assertNull(entity.getId());
        assertNull(entity.getFranchiseId());
        assertNull(entity.getBranchId());
        assertNull(entity.getName());
        assertNull(entity.getStock());
    }

    @Test
    void testConstructorWithParameters() {
        ProductEntity entity = new ProductEntity("1", "franchise1", "branch1", "Product 1", 10);
        
        assertEquals("1", entity.getId());
        assertEquals("franchise1", entity.getFranchiseId());
        assertEquals("branch1", entity.getBranchId());
        assertEquals("Product 1", entity.getName());
        assertEquals(10, entity.getStock());
    }

    @Test
    void testSettersAndGetters() {
        ProductEntity entity = new ProductEntity();
        
        entity.setId("1");
        entity.setFranchiseId("franchise1");
        entity.setBranchId("branch1");
        entity.setName("Product 1");
        entity.setStock(10);
        
        assertEquals("1", entity.getId());
        assertEquals("franchise1", entity.getFranchiseId());
        assertEquals("branch1", entity.getBranchId());
        assertEquals("Product 1", entity.getName());
        assertEquals(10, entity.getStock());
    }
}
