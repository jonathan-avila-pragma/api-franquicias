package co.com.pragma.api.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ResponseDtoTest {

    @Test
    void testBranchResponseDto() {
        BranchResponseDto dto = new BranchResponseDto();
        assertNotNull(dto);
    }

    @Test
    void testFranchiseResponseDto() {
        FranchiseResponseDto dto = new FranchiseResponseDto();
        assertNotNull(dto);
    }

    @Test
    void testFranchiseListResponseDto() {
        FranchiseListResponseDto dto = new FranchiseListResponseDto();
        assertNotNull(dto);
    }

    @Test
    void testProductResponseDto() {
        ProductResponseDto dto = new ProductResponseDto();
        assertNotNull(dto);
    }

    @Test
    void testProductWithBranchListResponseDto() {
        ProductWithBranchListResponseDto dto = new ProductWithBranchListResponseDto();
        assertNotNull(dto);
    }
}
