package co.com.pragma.usecase.deleteproduct;

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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteProductUseCaseTest {

    @Mock
    private ProductGateway productGateway;

    private DeleteProductUseCase deleteProductUseCase;

    @BeforeEach
    void setUp() {
        deleteProductUseCase = new DeleteProductUseCase(productGateway);
    }

    @Test
    void testExecuteSuccess() {
        Product product = new Product();
        product.setId("1");
        product.setName("Product 1");
        product.setStock(10);

        when(productGateway.findById("franchise1", "branch1", "1")).thenReturn(Mono.just(product));
        when(productGateway.deleteById("franchise1", "branch1", "1")).thenReturn(Mono.empty());

        StepVerifier.create(deleteProductUseCase.execute("franchise1", "branch1", "1"))
                .verifyComplete();

        verify(productGateway).findById("franchise1", "branch1", "1");
        verify(productGateway).deleteById("franchise1", "branch1", "1");
    }

    @Test
    void testExecuteProductNotFound() {
        when(productGateway.findById("franchise1", "branch1", "999")).thenReturn(Mono.empty());

        StepVerifier.create(deleteProductUseCase.execute("franchise1", "branch1", "999"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_PRODUCT_NOT_FOUND))
                .verify();

        verify(productGateway).findById("franchise1", "branch1", "999");
        verify(productGateway, never()).deleteById(anyString(), anyString(), anyString());
    }
}
