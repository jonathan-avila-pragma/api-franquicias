package co.com.pragma.usecase.updateproductstock;

import co.com.pragma.model.Constants;
import co.com.pragma.model.Product;
import co.com.pragma.model.gateways.ProductGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProductStockUseCaseTest {

    @Mock
    private ProductGateway productGateway;

    private UpdateProductStockUseCase updateProductStockUseCase;

    @BeforeEach
    void setUp() {
        updateProductStockUseCase = new UpdateProductStockUseCase(productGateway);
    }

    @Test
    void testExecuteWithNullStock() {
        StepVerifier.create(updateProductStockUseCase.execute("1", "1", "1", null))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_INVALID_STOCK))
                .verify();

        verify(productGateway, never()).findById(anyString(), anyString(), anyString());
    }

    @Test
    void testExecuteWithNegativeStock() {
        StepVerifier.create(updateProductStockUseCase.execute("1", "1", "1", -1))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_INVALID_STOCK))
                .verify();

        verify(productGateway, never()).findById(anyString(), anyString(), anyString());
    }

    @Test
    void testExecuteProductNotFound() {
        when(productGateway.findById("1", "1", "999")).thenReturn(Mono.empty());

        StepVerifier.create(updateProductStockUseCase.execute("1", "1", "999", 100))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_PRODUCT_NOT_FOUND))
                .verify();

        verify(productGateway).findById("1", "1", "999");
        verify(productGateway, never()).update(anyString(), anyString(), any());
    }

    @Test
    void testExecuteSuccess() {
        Product product = new Product("1", "Product 1", 10);
        Product updatedProduct = new Product("1", "Product 1", 100);

        when(productGateway.findById("1", "1", "1")).thenReturn(Mono.just(product));
        when(productGateway.update(eq("1"), eq("1"), any(Product.class))).thenReturn(Mono.just(updatedProduct));

        StepVerifier.create(updateProductStockUseCase.execute("1", "1", "1", 100))
                .expectNextMatches(p -> p.getStock().equals(100))
                .verifyComplete();

        verify(productGateway).findById("1", "1", "1");
        verify(productGateway).update(eq("1"), eq("1"), any(Product.class));
    }

    @Test
    void testExecuteWithZeroStock() {
        Product product = new Product("1", "Product 1", 10);
        Product updatedProduct = new Product("1", "Product 1", 0);

        when(productGateway.findById("1", "1", "1")).thenReturn(Mono.just(product));
        when(productGateway.update(eq("1"), eq("1"), any(Product.class))).thenReturn(Mono.just(updatedProduct));

        StepVerifier.create(updateProductStockUseCase.execute("1", "1", "1", 0))
                .expectNextMatches(p -> p.getStock().equals(0))
                .verifyComplete();

        verify(productGateway).findById("1", "1", "1");
        verify(productGateway).update(eq("1"), eq("1"), any(Product.class));
    }
}
