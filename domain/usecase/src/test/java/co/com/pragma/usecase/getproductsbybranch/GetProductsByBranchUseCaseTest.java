package co.com.pragma.usecase.getproductsbybranch;

import co.com.pragma.model.Branch;
import co.com.pragma.model.Constants;
import co.com.pragma.model.Product;
import co.com.pragma.model.gateways.BranchGateway;
import co.com.pragma.model.gateways.ProductGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetProductsByBranchUseCaseTest {

    @Mock
    private ProductGateway productGateway;

    @Mock
    private BranchGateway branchGateway;

    private GetProductsByBranchUseCase getProductsByBranchUseCase;

    @BeforeEach
    void setUp() {
        getProductsByBranchUseCase = new GetProductsByBranchUseCase(productGateway, branchGateway);
    }

    @Test
    void execute_Success() {
        Branch branch = Branch.builder().id("1").name("Branch 1").build();
        Product product1 = new Product("1", "Product 1", 10);
        Product product2 = new Product("2", "Product 2", 20);

        when(branchGateway.findById("franchise1", "branch1")).thenReturn(Mono.just(branch));
        when(productGateway.findAllByBranch("franchise1", "branch1"))
                .thenReturn(Flux.just(product1, product2));

        StepVerifier.create(getProductsByBranchUseCase.execute("franchise1", "branch1"))
                .expectNext(product1)
                .expectNext(product2)
                .verifyComplete();

        verify(branchGateway).findById("franchise1", "branch1");
        verify(productGateway).findAllByBranch("franchise1", "branch1");
    }

    @Test
    void execute_BranchNotFound() {
        when(branchGateway.findById("franchise1", "branch1")).thenReturn(Mono.empty());

        StepVerifier.create(getProductsByBranchUseCase.execute("franchise1", "branch1"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_BRANCH_NOT_FOUND))
                .verify();

        verify(branchGateway).findById("franchise1", "branch1");
        verify(productGateway, never()).findAllByBranch(anyString(), anyString());
    }

    @Test
    void execute_EmptyProducts() {
        Branch branch = Branch.builder().id("1").name("Branch 1").build();

        when(branchGateway.findById("franchise1", "branch1")).thenReturn(Mono.just(branch));
        when(productGateway.findAllByBranch("franchise1", "branch1"))
                .thenReturn(Flux.empty());

        StepVerifier.create(getProductsByBranchUseCase.execute("franchise1", "branch1"))
                .verifyComplete();

        verify(branchGateway).findById("franchise1", "branch1");
        verify(productGateway).findAllByBranch("franchise1", "branch1");
    }
}
