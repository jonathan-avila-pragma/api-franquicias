package co.com.pragma.api.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BaseBranchFieldsTest {

    private Validator validator;
    private TestBranchDto dto;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        dto = new TestBranchDto();
    }

    @Test
    void shouldPassValidationWithValidName() {
        dto.setName("Valid Branch Name");
        Set<ConstraintViolation<TestBranchDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNameExceedsMaxLength() {
        dto.setName("a".repeat(101));
        Set<ConstraintViolation<TestBranchDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Name must be between 1 and 100 characters")));
    }

    @Test
    void shouldFailWhenNameContainsInvalidCharacters() {
        dto.setName("Invalid@Name#");
        Set<ConstraintViolation<TestBranchDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Name contains invalid characters")));
    }

    @Test
    void shouldPassValidationWithValidAddress() {
        dto.setName("Branch");
        dto.setAddress("123 Main St., #456");
        Set<ConstraintViolation<TestBranchDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenAddressExceedsMaxLength() {
        dto.setName("Branch");
        dto.setAddress("a".repeat(201));
        Set<ConstraintViolation<TestBranchDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Address must not exceed 200 characters")));
    }

    @Test
    void shouldFailWhenAddressContainsInvalidCharacters() {
        dto.setName("Branch");
        dto.setAddress("Invalid@Address");
        Set<ConstraintViolation<TestBranchDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Address contains invalid characters")));
    }

    @Test
    void shouldPassValidationWithValidCity() {
        dto.setName("Branch");
        dto.setCity("New York");
        Set<ConstraintViolation<TestBranchDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenCityExceedsMaxLength() {
        dto.setName("Branch");
        dto.setCity("a".repeat(101));
        Set<ConstraintViolation<TestBranchDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("City must not exceed 100 characters")));
    }

    @Test
    void shouldFailWhenCityContainsInvalidCharacters() {
        dto.setName("Branch");
        dto.setCity("City123");
        Set<ConstraintViolation<TestBranchDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("City contains invalid characters")));
    }

    @Test
    void shouldPassValidationWithNullOptionalFields() {
        dto.setName("Branch");
        dto.setAddress(null);
        dto.setCity(null);
        Set<ConstraintViolation<TestBranchDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldSetAndGetNameCorrectly() {
        dto.setName("Test Name");
        assertEquals("Test Name", dto.getName());
    }

    @Test
    void shouldSetAndGetAddressCorrectly() {
        dto.setAddress("Test Address");
        assertEquals("Test Address", dto.getAddress());
    }

    @Test
    void shouldSetAndGetCityCorrectly() {
        dto.setCity("Test City");
        assertEquals("Test City", dto.getCity());
    }

    static class TestBranchDto extends BaseBranchFields {
    }
}
