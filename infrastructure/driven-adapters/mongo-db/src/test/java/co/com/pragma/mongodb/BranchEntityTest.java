package co.com.pragma.mongodb;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BranchEntityTest {

    @Test
    void testDefaultConstructor() {
        BranchEntity entity = new BranchEntity();
        assertNull(entity.getId());
        assertNull(entity.getFranchiseId());
        assertNull(entity.getName());
        assertNull(entity.getAddress());
        assertNull(entity.getCity());
    }

    @Test
    void testConstructorWithThreeParameters() {
        BranchEntity entity = new BranchEntity("franchise1", "1", "Branch 1");
        
        assertEquals("1", entity.getId());
        assertEquals("franchise1", entity.getFranchiseId());
        assertEquals("Branch 1", entity.getName());
        assertNull(entity.getAddress());
        assertNull(entity.getCity());
    }

    @Test
    void testAllArgsConstructor() {
        BranchEntity entity = new BranchEntity("1", "franchise1", "Branch 1", "Address", "City");
        
        assertEquals("1", entity.getId());
        assertEquals("franchise1", entity.getFranchiseId());
        assertEquals("Branch 1", entity.getName());
        assertEquals("Address", entity.getAddress());
        assertEquals("City", entity.getCity());
    }

    @Test
    void testSettersAndGetters() {
        BranchEntity entity = new BranchEntity();
        
        entity.setId("1");
        entity.setFranchiseId("franchise1");
        entity.setName("Branch 1");
        entity.setAddress("Address");
        entity.setCity("City");
        
        assertEquals("1", entity.getId());
        assertEquals("franchise1", entity.getFranchiseId());
        assertEquals("Branch 1", entity.getName());
        assertEquals("Address", entity.getAddress());
        assertEquals("City", entity.getCity());
    }
}
