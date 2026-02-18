package co.com.pragma.usecase.updateproductname;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProductNameUseCaseTest {

    @Mock
    private ProductGateway productGateway;

    @Mock
    private BranchGateway branchGateway;

    @Mock
    private FranchiseGateway franchiseGateway;

    private UpdateProductNameUseCase updateProductNameUseCase;

    @BeforeEach
    void setUp() {
        updateProductNameUseCase = new UpdateProductNameUseCase(productGateway, branchGateway, franchiseGateway);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void testExecuteWithInvalidName(String name) {
        StepVerifier.create(updateProductNameUseCase.execute("1", "1", "1", name))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_INVALID_NAME))
                .verify();

        verify(franchiseGateway, never()).findById(anyString());
    }

    @Test
    void testExecuteFranchiseNotFound() {
        when(franchiseGateway.findById("999")).thenReturn(Mono.empty());

        StepVerifier.create(updateProductNameUseCase.execute("999", "1", "1", "New Name"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_FRANCHISE_NOT_FOUND))
                .verify();

        verify(franchiseGateway).findById("999");
        verify(branchGateway, never()).findById(anyString(), anyString());
    }

    @Test
    void testExecuteBranchNotFound() {
        Franchise franchise = Franchise.builder().id("1").name("Franchise 1").description("Description").build();
        when(franchiseGateway.findById("1")).thenReturn(Mono.just(franchise));
        when(branchGateway.findById("1", "999")).thenReturn(Mono.empty());

        StepVerifier.create(updateProductNameUseCase.execute("1", "999", "1", "New Name"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_BRANCH_NOT_FOUND))
                .verify();

        verify(franchiseGateway).findById("1");
        verify(branchGateway).findById("1", "999");
        verify(productGateway, never()).findById(anyString(), anyString(), anyString());
    }

    @Test
    void testExecuteProductNotFound() {
        Franchise franchise = Franchise.builder().id("1").name("Franchise 1").description("Description").build();
        Branch branch = Branch.builder().id("1").name("Branch 1").address("Address").city("City").build();
        when(franchiseGateway.findById("1")).thenReturn(Mono.just(franchise));
        when(branchGateway.findById("1", "1")).thenReturn(Mono.just(branch));
        when(productGateway.findById("1", "1", "999")).thenReturn(Mono.empty());

        StepVerifier.create(updateProductNameUseCase.execute("1", "1", "999", "New Name"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_PRODUCT_NOT_FOUND))
                .verify();

        verify(franchiseGateway).findById("1");
        verify(branchGateway).findById("1", "1");
        verify(productGateway).findById("1", "1", "999");
        verify(productGateway, never()).update(anyString(), anyString(), any());
    }

    @Test
    void testExecuteSuccess() {
        Franchise franchise = Franchise.builder().id("1").name("Franchise 1").description("Description").build();
        Branch branch = Branch.builder().id("1").name("Branch 1").address("Address").city("City").build();
        Product product = new Product("1", "Old Name", 10);
        Product updatedProduct = new Product("1", "New Name", 10);

        when(franchiseGateway.findById("1")).thenReturn(Mono.just(franchise));
        when(branchGateway.findById("1", "1")).thenReturn(Mono.just(branch));
        when(productGateway.findById("1", "1", "1")).thenReturn(Mono.just(product));
        when(productGateway.update(eq("1"), eq("1"), any(Product.class))).thenReturn(Mono.just(updatedProduct));

        StepVerifier.create(updateProductNameUseCase.execute("1", "1", "1", "New Name"))
                .expectNextMatches(p -> p.getName().equals("New Name"))
                .verifyComplete();

        verify(franchiseGateway).findById("1");
        verify(branchGateway).findById("1", "1");
        verify(productGateway).findById("1", "1", "1");
        verify(productGateway).update(eq("1"), eq("1"), any(Product.class));
    }
}
