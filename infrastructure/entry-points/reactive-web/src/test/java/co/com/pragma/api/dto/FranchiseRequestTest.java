package co.com.pragma.api.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FranchiseRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest
    @MethodSource("validDescriptionProvider")
    void validate_ValidRequestWithDescription(String name, String description) {
        FranchiseRequest request = new FranchiseRequest(name, description);
        Set<ConstraintViolation<FranchiseRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    static Stream<Arguments> validDescriptionProvider() {
        return Stream.of(
                Arguments.of("Test Franchise", "Test Description"),
                Arguments.of("Test Franchise", null),
                Arguments.of("Test", ""),
                Arguments.of("Test", "Description with spaces, dots. semicolons; colons: dashes- underscores_")
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void validate_NameInvalid(String name) {
        FranchiseRequest request = new FranchiseRequest();
        request.setName(name);
        Set<ConstraintViolation<FranchiseRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Name is required")));
    }

    @Test
    void validate_NameTooLong() {
        String longName = "a".repeat(101);
        FranchiseRequest request = new FranchiseRequest(longName);
        Set<ConstraintViolation<FranchiseRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Name must be between 1 and 100 characters")));
    }

    @Test
    void validate_NameInvalidCharacters() {
        FranchiseRequest request = new FranchiseRequest("Test@Franchise#");
        Set<ConstraintViolation<FranchiseRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Name contains invalid characters")));
    }

    @Test
    void validate_NameValidCharacters() {
        FranchiseRequest request = new FranchiseRequest("Test-Franchise_123.Name");
        Set<ConstraintViolation<FranchiseRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_DescriptionTooLong() {
        String longDescription = "a".repeat(501);
        FranchiseRequest request = new FranchiseRequest("Test", longDescription);
        Set<ConstraintViolation<FranchiseRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Description must not exceed 500 characters")));
    }

    @Test
    void validate_DescriptionInvalidCharacters() {
        FranchiseRequest request = new FranchiseRequest("Test", "Description@#$%");
        Set<ConstraintViolation<FranchiseRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Description contains invalid characters")));
    }

}