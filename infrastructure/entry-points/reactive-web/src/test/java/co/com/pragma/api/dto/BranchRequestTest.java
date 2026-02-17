package co.com.pragma.api.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BranchRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testDefaultConstructor() {
        BranchRequest request = new BranchRequest();
        
        assertNull(request.getName());
        assertNull(request.getAddress());
        assertNull(request.getCity());
    }

    @Test
    void testConstructorWithName() {
        BranchRequest request = new BranchRequest("Branch Name");
        
        assertEquals("Branch Name", request.getName());
        assertNull(request.getAddress());
        assertNull(request.getCity());
    }

    @Test
    void testConstructorWithAllFields() {
        BranchRequest request = new BranchRequest("Branch Name", "Address 123", "City");
        
        assertEquals("Branch Name", request.getName());
        assertEquals("Address 123", request.getAddress());
        assertEquals("City", request.getCity());
    }

    @Test
    void testValidBranchRequest() {
        BranchRequest request = new BranchRequest("Valid Branch", "Address", "City");
        
        Set<ConstraintViolation<BranchRequest>> violations = validator.validate(request);
        
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNameIsRequired() {
        BranchRequest request = new BranchRequest();
        
        Set<ConstraintViolation<BranchRequest>> violations = validator.validate(request);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Name is required")));
    }

    @Test
    void testNameMaxLength() {
        BranchRequest request = new BranchRequest("a".repeat(101));
        
        Set<ConstraintViolation<BranchRequest>> violations = validator.validate(request);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be between 1 and 100 characters")));
    }

    @Test
    void testNameInvalidCharacters() {
        BranchRequest request = new BranchRequest("Invalid<>Name");
        
        Set<ConstraintViolation<BranchRequest>> violations = validator.validate(request);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Name contains invalid characters")));
    }

    @Test
    void testSettersAndGetters() {
        BranchRequest request = new BranchRequest();
        
        request.setName("New Branch");
        request.setAddress("New Address");
        request.setCity("New City");
        
        assertEquals("New Branch", request.getName());
        assertEquals("New Address", request.getAddress());
        assertEquals("New City", request.getCity());
    }

    @Test
    void testGetNameOverride() {
        BranchRequest request = new BranchRequest("Test Branch");
        
        assertEquals("Test Branch", request.getName());
        
        request.setName("Updated Branch");
        assertEquals("Updated Branch", request.getName());
    }

    @Test
    void testGetNameWithNull() {
        BranchRequest request = new BranchRequest();
        
        assertNull(request.getName());
        
        request.setName("New Name");
        assertEquals("New Name", request.getName());
    }
}
