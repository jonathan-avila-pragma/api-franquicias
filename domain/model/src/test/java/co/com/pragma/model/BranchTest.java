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
    void testConstructorWithIdAndName() {
        Branch branch = new Branch("1", "Branch Name");
        
        assertEquals("1", branch.getId());
        assertEquals("Branch Name", branch.getName());
        assertNull(branch.getAddress());
        assertNull(branch.getCity());
        assertNotNull(branch.getProducts());
        assertTrue(branch.getProducts().isEmpty());
    }

    @Test
    void testConstructorWithIdNameAddressCity() {
        Branch branch = new Branch("1", "Branch Name", "Address 123", "City");
        
        assertEquals("1", branch.getId());
        assertEquals("Branch Name", branch.getName());
        assertEquals("Address 123", branch.getAddress());
        assertEquals("City", branch.getCity());
        assertNotNull(branch.getProducts());
        assertTrue(branch.getProducts().isEmpty());
    }

    @Test
    void testConstructorWithIdNameAndProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("1", "Product 1", 10));
        
        Branch branch = new Branch("1", "Branch Name", products);
        
        assertEquals("1", branch.getId());
        assertEquals("Branch Name", branch.getName());
        assertNull(branch.getAddress());
        assertNull(branch.getCity());
        assertNotNull(branch.getProducts());
        assertEquals(1, branch.getProducts().size());
        assertEquals("Product 1", branch.getProducts().get(0).getName());
    }

    @Test
    void testConstructorWithIdNameAndNullProducts() {
        Branch branch = new Branch("1", "Branch Name", null);
        
        assertEquals("1", branch.getId());
        assertEquals("Branch Name", branch.getName());
        assertNotNull(branch.getProducts());
        assertTrue(branch.getProducts().isEmpty());
    }

    @Test
    void testConstructorWithAllFields() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("1", "Product 1", 10));
        
        Branch branch = new Branch("1", "Branch Name", "Address 123", "City", products);
        
        assertEquals("1", branch.getId());
        assertEquals("Branch Name", branch.getName());
        assertEquals("Address 123", branch.getAddress());
        assertEquals("City", branch.getCity());
        assertNotNull(branch.getProducts());
        assertEquals(1, branch.getProducts().size());
    }

    @Test
    void testConstructorWithAllFieldsAndNullProducts() {
        Branch branch = new Branch("1", "Branch Name", "Address 123", "City", null);
        
        assertEquals("1", branch.getId());
        assertEquals("Branch Name", branch.getName());
        assertEquals("Address 123", branch.getAddress());
        assertEquals("City", branch.getCity());
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
