package co.com.pragma.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FranchiseTest {

    @Test
    void testDefaultConstructor() {
        Franchise franchise = new Franchise();
        
        assertNull(franchise.getId());
        assertNull(franchise.getName());
        assertNull(franchise.getDescription());
        assertNotNull(franchise.getBranches());
        assertTrue(franchise.getBranches().isEmpty());
    }

    @Test
    void testConstructorWithIdAndName() {
        Franchise franchise = new Franchise("1", "Franchise Name");
        
        assertEquals("1", franchise.getId());
        assertEquals("Franchise Name", franchise.getName());
        assertNull(franchise.getDescription());
        assertNotNull(franchise.getBranches());
        assertTrue(franchise.getBranches().isEmpty());
    }

    @Test
    void testConstructorWithIdNameAndDescription() {
        Franchise franchise = new Franchise("1", "Franchise Name", "Description");
        
        assertEquals("1", franchise.getId());
        assertEquals("Franchise Name", franchise.getName());
        assertEquals("Description", franchise.getDescription());
        assertNotNull(franchise.getBranches());
        assertTrue(franchise.getBranches().isEmpty());
    }

    @Test
    void testConstructorWithIdNameAndBranches() {
        List<Branch> branches = new ArrayList<>();
        branches.add(new Branch("1", "Branch 1"));
        
        Franchise franchise = new Franchise("1", "Franchise Name", branches);
        
        assertEquals("1", franchise.getId());
        assertEquals("Franchise Name", franchise.getName());
        assertNull(franchise.getDescription());
        assertNotNull(franchise.getBranches());
        assertEquals(1, franchise.getBranches().size());
    }

    @Test
    void testConstructorWithIdNameAndNullBranches() {
        Franchise franchise = new Franchise("1", "Franchise Name", (List<Branch>) null);
        
        assertEquals("1", franchise.getId());
        assertEquals("Franchise Name", franchise.getName());
        assertNotNull(franchise.getBranches());
        assertTrue(franchise.getBranches().isEmpty());
    }

    @Test
    void testConstructorWithAllFields() {
        List<Branch> branches = new ArrayList<>();
        branches.add(new Branch("1", "Branch 1"));
        
        Franchise franchise = new Franchise("1", "Franchise Name", "Description", branches);
        
        assertEquals("1", franchise.getId());
        assertEquals("Franchise Name", franchise.getName());
        assertEquals("Description", franchise.getDescription());
        assertNotNull(franchise.getBranches());
        assertEquals(1, franchise.getBranches().size());
    }

    @Test
    void testConstructorWithAllFieldsAndNullBranches() {
        Franchise franchise = new Franchise("1", "Franchise Name", "Description", null);
        
        assertEquals("1", franchise.getId());
        assertEquals("Franchise Name", franchise.getName());
        assertEquals("Description", franchise.getDescription());
        assertNotNull(franchise.getBranches());
        assertTrue(franchise.getBranches().isEmpty());
    }

    @Test
    void testSettersAndGetters() {
        Franchise franchise = new Franchise();
        List<Branch> branches = new ArrayList<>();
        branches.add(new Branch("1", "Branch 1"));
        
        franchise.setId("2");
        franchise.setName("New Franchise");
        franchise.setDescription("New Description");
        franchise.setBranches(branches);
        
        assertEquals("2", franchise.getId());
        assertEquals("New Franchise", franchise.getName());
        assertEquals("New Description", franchise.getDescription());
        assertEquals(1, franchise.getBranches().size());
    }

    @Test
    void testSetBranchesWithNull() {
        Franchise franchise = new Franchise();
        
        franchise.setBranches(null);
        
        assertNotNull(franchise.getBranches());
        assertTrue(franchise.getBranches().isEmpty());
    }
}
