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
    void testBuilderWithIdAndName() {
        Franchise franchise = Franchise.builder()
                .id("1")
                .name("Franchise Name")
                .build();
        
        assertEquals("1", franchise.getId());
        assertEquals("Franchise Name", franchise.getName());
        assertNull(franchise.getDescription());
        assertNotNull(franchise.getBranches());
        assertTrue(franchise.getBranches().isEmpty());
    }

    @Test
    void testBuilderWithIdNameAndDescription() {
        Franchise franchise = Franchise.builder()
                .id("1")
                .name("Franchise Name")
                .description("Description")
                .build();
        
        assertEquals("1", franchise.getId());
        assertEquals("Franchise Name", franchise.getName());
        assertEquals("Description", franchise.getDescription());
        assertNotNull(franchise.getBranches());
        assertTrue(franchise.getBranches().isEmpty());
    }

    @Test
    void testBuilderWithIdNameAndBranches() {
        List<Branch> branches = new ArrayList<>();
        branches.add(Branch.builder().id("1").name("Branch 1").build());
        
        Franchise franchise = Franchise.builder()
                .id("1")
                .name("Franchise Name")
                .branches(branches)
                .build();
        
        assertEquals("1", franchise.getId());
        assertEquals("Franchise Name", franchise.getName());
        assertNull(franchise.getDescription());
        assertNotNull(franchise.getBranches());
        assertEquals(1, franchise.getBranches().size());
    }

    @Test
    void testBuilderWithIdNameAndNullBranches() {
        Franchise franchise = Franchise.builder()
                .id("1")
                .name("Franchise Name")
                .build();
        
        assertEquals("1", franchise.getId());
        assertEquals("Franchise Name", franchise.getName());
        assertNotNull(franchise.getBranches());
        assertTrue(franchise.getBranches().isEmpty());
    }

    @Test
    void testBuilderWithAllFields() {
        List<Branch> branches = new ArrayList<>();
        branches.add(Branch.builder().id("1").name("Branch 1").build());
        
        Franchise franchise = Franchise.builder()
                .id("1")
                .name("Franchise Name")
                .description("Description")
                .branches(branches)
                .build();
        
        assertEquals("1", franchise.getId());
        assertEquals("Franchise Name", franchise.getName());
        assertEquals("Description", franchise.getDescription());
        assertNotNull(franchise.getBranches());
        assertEquals(1, franchise.getBranches().size());
    }

    @Test
    void testBuilderWithAllFieldsAndNullBranches() {
        Franchise franchise = Franchise.builder()
                .id("1")
                .name("Franchise Name")
                .description("Description")
                .build();
        
        assertEquals("1", franchise.getId());
        assertEquals("Franchise Name", franchise.getName());
        assertEquals("Description", franchise.getDescription());
        assertNotNull(franchise.getBranches());
        assertTrue(franchise.getBranches().isEmpty());
        
        franchise.setBranches(null);
        assertNotNull(franchise.getBranches());
        assertTrue(franchise.getBranches().isEmpty());
    }

    @Test
    void testSettersAndGetters() {
        Franchise franchise = new Franchise();
        List<Branch> branches = new ArrayList<>();
        branches.add(Branch.builder().id("1").name("Branch 1").build());
        
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
