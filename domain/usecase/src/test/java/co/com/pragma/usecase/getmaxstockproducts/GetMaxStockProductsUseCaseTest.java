package co.com.pragma.usecase.getmaxstockproducts;

import co.com.pragma.model.Branch;
import co.com.pragma.model.Product;
import co.com.pragma.model.ProductWithBranch;
import co.com.pragma.model.gateways.ProductGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetMaxStockProductsUseCaseTest {

    @Mock
    private ProductGateway productGateway;

    private GetMaxStockProductsUseCase getMaxStockProductsUseCase;

    @BeforeEach
    void setUp() {
        getMaxStockProductsUseCase = new GetMaxStockProductsUseCase(productGateway);
    }

    @Test
    void testExecute() {
        Product product = new Product();
        product.setId("1");
        product.setName("Product 1");
        product.setStock(100);

        Branch branch = new Branch("1", "Branch 1", "Address", "City");

        ProductWithBranch productWithBranch = new ProductWithBranch(product, branch);

        when(productGateway.findMaxStockProductsByFranchise("franchise1"))
                .thenReturn(Flux.just(productWithBranch));

        StepVerifier.create(getMaxStockProductsUseCase.execute("franchise1"))
                .expectNextMatches(pwb -> pwb.getProduct().getId().equals("1") 
                        && pwb.getBranch().getId().equals("1"))
                .verifyComplete();

        verify(productGateway).findMaxStockProductsByFranchise("franchise1");
    }
}
