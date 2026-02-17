package co.com.pragma.usecase.addproduct;

import co.com.pragma.model.Branch;
import co.com.pragma.model.Constants;
import co.com.pragma.model.Franchise;
import co.com.pragma.model.Product;
import co.com.pragma.model.gateways.BranchGateway;
import co.com.pragma.model.gateways.FranchiseGateway;
import co.com.pragma.model.gateways.ProductGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddProductUseCaseTest {

    @Mock
    private ProductGateway productGateway;

    @Mock
    private BranchGateway branchGateway;

    @Mock
    private FranchiseGateway franchiseGateway;

    private AddProductUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new AddProductUseCase(productGateway, branchGateway, franchiseGateway);
    }

    @Test
    void shouldAddProductSuccessfully() {
        String franchiseId = "1";
        String branchId = "10";
        String productId = "100";
        
        Product product = new Product();
        product.setName("Test Product");
        product.setStock(50);

        Franchise franchise = new Franchise();
        franchise.setId(franchiseId);

        Branch branch = new Branch();
        branch.setId(branchId);

        Product savedProduct = new Product();
        savedProduct.setId(productId);
        savedProduct.setName("Test Product");
        savedProduct.setStock(50);

        when(franchiseGateway.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(branchGateway.findById(franchiseId, branchId)).thenReturn(Mono.just(branch));
        when(productGateway.getNextId()).thenReturn(Mono.just(productId));
        when(productGateway.save(eq(franchiseId), eq(branchId), any(Product.class))).thenReturn(Mono.just(savedProduct));

        StepVerifier.create(useCase.execute(franchiseId, branchId, product))
                .expectNextMatches(result -> result.getId().equals(productId) 
                        && result.getName().equals("Test Product") 
                        && result.getStock() == 50)
                .verifyComplete();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldFailWhenNameIsInvalid(String name) {
        Product product = new Product();
        product.setName(name);
        product.setStock(10);

        StepVerifier.create(useCase.execute("1", "10", product))
                .expectErrorMatches(error -> error instanceof IllegalArgumentException 
                        && error.getMessage().equals(Constants.ERROR_INVALID_NAME))
                .verify();
    }

    @Test
    void shouldFailWhenStockIsNull() {
        Product product = new Product();
        product.setName("Test Product");
        product.setStock(null);

        StepVerifier.create(useCase.execute("1", "10", product))
                .expectErrorMatches(error -> error instanceof IllegalArgumentException 
                        && error.getMessage().equals(Constants.ERROR_INVALID_STOCK))
                .verify();
    }

    @Test
    void shouldFailWhenStockIsNegative() {
        Product product = new Product();
        product.setName("Test Product");
        product.setStock(-1);

        StepVerifier.create(useCase.execute("1", "10", product))
                .expectErrorMatches(error -> error instanceof IllegalArgumentException 
                        && error.getMessage().equals(Constants.ERROR_INVALID_STOCK))
                .verify();
    }

    @Test
    void shouldFailWhenFranchiseNotFound() {
        Product product = new Product();
        product.setName("Test Product");
        product.setStock(10);

        when(franchiseGateway.findById("999")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute("999", "10", product))
                .expectErrorMatches(error -> error instanceof IllegalArgumentException 
                        && error.getMessage().equals(Constants.ERROR_FRANCHISE_NOT_FOUND))
                .verify();
    }

    @Test
    void shouldFailWhenBranchNotFound() {
        String franchiseId = "1";
        String branchId = "999";
        
        Product product = new Product();
        product.setName("Test Product");
        product.setStock(10);

        Franchise franchise = new Franchise();
        franchise.setId(franchiseId);

        when(franchiseGateway.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(branchGateway.findById(franchiseId, branchId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(franchiseId, branchId, product))
                .expectErrorMatches(error -> error instanceof IllegalArgumentException 
                        && error.getMessage().equals(Constants.ERROR_BRANCH_NOT_FOUND))
                .verify();
    }

    @Test
    void shouldAddProductWithZeroStock() {
        String franchiseId = "1";
        String branchId = "10";
        String productId = "100";
        
        Product product = new Product();
        product.setName("Test Product");
        product.setStock(0);

        Franchise franchise = new Franchise();
        franchise.setId(franchiseId);

        Branch branch = new Branch();
        branch.setId(branchId);

        Product savedProduct = new Product();
        savedProduct.setId(productId);
        savedProduct.setName("Test Product");
        savedProduct.setStock(0);

        when(franchiseGateway.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(branchGateway.findById(franchiseId, branchId)).thenReturn(Mono.just(branch));
        when(productGateway.getNextId()).thenReturn(Mono.just(productId));
        when(productGateway.save(eq(franchiseId), eq(branchId), any(Product.class))).thenReturn(Mono.just(savedProduct));

        StepVerifier.create(useCase.execute(franchiseId, branchId, product))
                .expectNextMatches(result -> result.getStock() == 0)
                .verifyComplete();
    }
}
