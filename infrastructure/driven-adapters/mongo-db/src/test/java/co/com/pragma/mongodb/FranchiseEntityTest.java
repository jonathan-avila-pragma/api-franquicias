package co.com.pragma.mongodb;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FranchiseEntityTest {

    @Test
    void testDefaultConstructor() {
        FranchiseEntity entity = new FranchiseEntity();
        
        assertNull(entity.getId());
        assertNull(entity.getName());
        assertNull(entity.getDescription());
    }

    @Test
    void testConstructorWithIdAndName() {
        FranchiseEntity entity = new FranchiseEntity("1", "Franchise Name");
        
        assertEquals("1", entity.getId());
        assertEquals("Franchise Name", entity.getName());
        assertNull(entity.getDescription());
    }

    @Test
    void testConstructorWithAllFields() {
        FranchiseEntity entity = new FranchiseEntity("1", "Franchise Name", "Description");
        
        assertEquals("1", entity.getId());
        assertEquals("Franchise Name", entity.getName());
        assertEquals("Description", entity.getDescription());
    }

    @Test
    void testSettersAndGetters() {
        FranchiseEntity entity = new FranchiseEntity();
        
        entity.setId("2");
        entity.setName("New Franchise");
        entity.setDescription("New Description");
        
        assertEquals("2", entity.getId());
        assertEquals("New Franchise", entity.getName());
        assertEquals("New Description", entity.getDescription());
    }
}
