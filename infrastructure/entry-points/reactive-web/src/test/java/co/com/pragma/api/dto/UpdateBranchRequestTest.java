package co.com.pragma.api.dto;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UpdateBranchRequestTest {

    @ParameterizedTest
    @MethodSource("blankFieldProvider")
    void hasAtLeastOneField_WithBlankFields_ReturnsFalse(String name, String address, String city) {
        UpdateBranchRequest request = new UpdateBranchRequest();
        request.setName(name);
        request.setAddress(address);
        request.setCity(city);
        
        assertFalse(request.hasAtLeastOneField());
    }

    static Stream<Arguments> blankFieldProvider() {
        return Stream.of(
                Arguments.of(null, null, null),
                Arguments.of("", "", ""),
                Arguments.of("   ", "   ", "   "),
                Arguments.of("   ", null, null),
                Arguments.of(null, "   ", null),
                Arguments.of(null, null, "   "),
                Arguments.of("   ", "   ", null),
                Arguments.of("   ", null, "   "),
                Arguments.of(null, "   ", "   ")
        );
    }

    @ParameterizedTest
    @MethodSource("validFieldProvider")
    void hasAtLeastOneField_WithValidFields_ReturnsTrue(String name, String address, String city) {
        UpdateBranchRequest request = new UpdateBranchRequest();
        request.setName(name);
        request.setAddress(address);
        request.setCity(city);
        
        assertTrue(request.hasAtLeastOneField());
    }

    static Stream<Arguments> validFieldProvider() {
        return Stream.of(
                Arguments.of("Test Branch", null, null),
                Arguments.of(null, "123 Main St", null),
                Arguments.of(null, null, "Bogotá"),
                Arguments.of("Test Branch", "123 Main St", null),
                Arguments.of("Test Branch", null, "Bogotá"),
                Arguments.of(null, "123 Main St", "Bogotá"),
                Arguments.of("Test Branch", "123 Main St", "Bogotá")
        );
    }
}
