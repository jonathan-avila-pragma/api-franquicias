package co.com.pragma.usecase.getproductbyname;

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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetProductByNameUseCaseTest {

    @Mock
    private ProductGateway productGateway;
    @Mock
    private BranchGateway branchGateway;
    @Mock
    private FranchiseGateway franchiseGateway;

    private GetProductByNameUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetProductByNameUseCase(productGateway, branchGateway, franchiseGateway);
    }

    @Test
    void execute_Success() {
        Franchise franchise = Franchise.builder().id("1").name("Test Franchise").build();
        Branch branch = new Branch();
        branch.setId("1");
        Product product = new Product("1", "Test Product", 100);

        when(franchiseGateway.findById("1")).thenReturn(Mono.just(franchise));
        when(branchGateway.findById("1", "1")).thenReturn(Mono.just(branch));
        when(productGateway.findByName("1", "1", "Test Product")).thenReturn(Mono.just(product));

        StepVerifier.create(useCase.execute("1", "1", "Test Product"))
                .expectNext(product)
                .verifyComplete();

        verify(franchiseGateway).findById("1");
        verify(branchGateway).findById("1", "1");
        verify(productGateway).findByName("1", "1", "Test Product");
    }

    @Test
    void execute_FranchiseIdNull() {
        StepVerifier.create(useCase.execute(null, "1", "Test Product"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_FRANCHISE_NOT_FOUND))
                .verify();

        verifyNoInteractions(franchiseGateway, branchGateway, productGateway);
    }

    @Test
    void execute_FranchiseIdEmpty() {
        StepVerifier.create(useCase.execute("", "1", "Test Product"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_FRANCHISE_NOT_FOUND))
                .verify();

        verifyNoInteractions(franchiseGateway, branchGateway, productGateway);
    }

    @Test
    void execute_BranchIdNull() {
        StepVerifier.create(useCase.execute("1", null, "Test Product"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_BRANCH_NOT_FOUND))
                .verify();

        verifyNoInteractions(franchiseGateway, branchGateway, productGateway);
    }

    @Test
    void execute_BranchIdEmpty() {
        StepVerifier.create(useCase.execute("1", "", "Test Product"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_BRANCH_NOT_FOUND))
                .verify();

        verifyNoInteractions(franchiseGateway, branchGateway, productGateway);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void execute_ProductNameInvalid(String productName) {
        StepVerifier.create(useCase.execute("1", "1", productName))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_INVALID_NAME))
                .verify();

        verifyNoInteractions(franchiseGateway, branchGateway, productGateway);
    }

    @Test
    void execute_FranchiseNotFound() {
        when(franchiseGateway.findById("999")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute("999", "1", "Test Product"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_FRANCHISE_NOT_FOUND))
                .verify();

        verify(franchiseGateway).findById("999");
        verifyNoInteractions(branchGateway, productGateway);
    }

    @Test
    void execute_BranchNotFound() {
        Franchise franchise = Franchise.builder().id("1").name("Test Franchise").build();

        when(franchiseGateway.findById("1")).thenReturn(Mono.just(franchise));
        when(branchGateway.findById("1", "999")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute("1", "999", "Test Product"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_BRANCH_NOT_FOUND))
                .verify();

        verify(franchiseGateway).findById("1");
        verify(branchGateway).findById("1", "999");
        verifyNoInteractions(productGateway);
    }

    @Test
    void execute_ProductNotFound() {
        Franchise franchise = Franchise.builder().id("1").name("Test Franchise").build();
        Branch branch = new Branch();
        branch.setId("1");

        when(franchiseGateway.findById("1")).thenReturn(Mono.just(franchise));
        when(branchGateway.findById("1", "1")).thenReturn(Mono.just(branch));
        when(productGateway.findByName("1", "1", "NonExistent")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute("1", "1", "NonExistent"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_PRODUCT_NOT_FOUND))
                .verify();

        verify(franchiseGateway).findById("1");
        verify(branchGateway).findById("1", "1");
        verify(productGateway).findByName("1", "1", "NonExistent");
    }
}