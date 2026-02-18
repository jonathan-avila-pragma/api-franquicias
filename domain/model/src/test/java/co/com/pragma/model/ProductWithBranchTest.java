package co.com.pragma.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductWithBranchTest {

    @Test
    void testDefaultConstructor() {
        ProductWithBranch productWithBranch = new ProductWithBranch();
        assertNull(productWithBranch.getProduct());
        assertNull(productWithBranch.getBranch());
    }

    @Test
    void testConstructorWithParameters() {
        Product product = new Product("1", "Product 1", 10);
        Branch branch = Branch.builder()
                .id("1")
                .name("Branch 1")
                .address("Address")
                .city("City")
                .build();
        
        ProductWithBranch productWithBranch = new ProductWithBranch(product, branch);
        
        assertNotNull(productWithBranch.getProduct());
        assertNotNull(productWithBranch.getBranch());
        assertEquals("1", productWithBranch.getProduct().getId());
        assertEquals("1", productWithBranch.getBranch().getId());
    }

    @Test
    void testSettersAndGetters() {
        ProductWithBranch productWithBranch = new ProductWithBranch();
        Product product = new Product("1", "Product 1", 10);
        Branch branch = Branch.builder()
                .id("1")
                .name("Branch 1")
                .address("Address")
                .city("City")
                .build();
        
        productWithBranch.setProduct(product);
        productWithBranch.setBranch(branch);
        
        assertEquals(product, productWithBranch.getProduct());
        assertEquals(branch, productWithBranch.getBranch());
    }
}
