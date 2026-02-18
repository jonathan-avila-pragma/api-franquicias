package co.com.pragma.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BranchTest {

    @Test
    void testDefaultConstructor() {
        Branch branch = new Branch();
        
        assertNull(branch.getId());
        assertNull(branch.getName());
        assertNull(branch.getAddress());
        assertNull(branch.getCity());
        assertNotNull(branch.getProducts());
        assertTrue(branch.getProducts().isEmpty());
    }

    @Test
    void testBuilderWithIdAndName() {
        Branch branch = Branch.builder()
                .id("1")
                .name("Branch Name")
                .build();
        
        assertEquals("1", branch.getId());
        assertEquals("Branch Name", branch.getName());
        assertNull(branch.getAddress());
        assertNull(branch.getCity());
        assertNotNull(branch.getProducts());
        assertTrue(branch.getProducts().isEmpty());
    }

    @Test
    void testBuilderWithIdNameAddressCity() {
        Branch branch = Branch.builder()
                .id("1")
                .name("Branch Name")
                .address("Address 123")
                .city("City")
                .build();
        
        assertEquals("1", branch.getId());
        assertEquals("Branch Name", branch.getName());
        assertEquals("Address 123", branch.getAddress());
        assertEquals("City", branch.getCity());
        assertNotNull(branch.getProducts());
        assertTrue(branch.getProducts().isEmpty());
    }

    @Test
    void testBuilderWithIdNameAndProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("1", "Product 1", 10));
        
        Branch branch = Branch.builder()
                .id("1")
                .name("Branch Name")
                .products(products)
                .build();
        
        assertEquals("1", branch.getId());
        assertEquals("Branch Name", branch.getName());
        assertNull(branch.getAddress());
        assertNull(branch.getCity());
        assertNotNull(branch.getProducts());
        assertEquals(1, branch.getProducts().size());
        assertEquals("Product 1", branch.getProducts().get(0).getName());
    }

    @Test
    void testBuilderWithIdNameAndNullProducts() {
        Branch branch = Branch.builder()
                .id("1")
                .name("Branch Name")
                .build();
        
        assertEquals("1", branch.getId());
        assertEquals("Branch Name", branch.getName());
        assertNotNull(branch.getProducts());
        assertTrue(branch.getProducts().isEmpty());
    }

    @Test
    void testBuilderWithAllFields() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("1", "Product 1", 10));
        
        Branch branch = Branch.builder()
                .id("1")
                .name("Branch Name")
                .address("Address 123")
                .city("City")
                .products(products)
                .build();
        
        assertEquals("1", branch.getId());
        assertEquals("Branch Name", branch.getName());
        assertEquals("Address 123", branch.getAddress());
        assertEquals("City", branch.getCity());
        assertNotNull(branch.getProducts());
        assertEquals(1, branch.getProducts().size());
    }

    @Test
    void testBuilderWithAllFieldsAndNullProducts() {
        Branch branch = Branch.builder()
                .id("1")
                .name("Branch Name")
                .address("Address 123")
                .city("City")
                .build();
        
        assertEquals("1", branch.getId());
        assertEquals("Branch Name", branch.getName());
        assertEquals("Address 123", branch.getAddress());
        assertEquals("City", branch.getCity());
        assertNotNull(branch.getProducts());
        assertTrue(branch.getProducts().isEmpty());
        
        branch.setProducts(null);
        assertNotNull(branch.getProducts());
        assertTrue(branch.getProducts().isEmpty());
    }

    @Test
    void testSettersAndGetters() {
        Branch branch = new Branch();
        List<Product> products = new ArrayList<>();
        products.add(new Product("1", "Product 1", 10));
        
        branch.setId("2");
        branch.setName("New Branch");
        branch.setAddress("New Address");
        branch.setCity("New City");
        branch.setProducts(products);
        
        assertEquals("2", branch.getId());
        assertEquals("New Branch", branch.getName());
        assertEquals("New Address", branch.getAddress());
        assertEquals("New City", branch.getCity());
        assertEquals(1, branch.getProducts().size());
    }
}
