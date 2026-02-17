package co.com.pragma.usecase.getbranchbyid;

import co.com.pragma.model.Branch;
import co.com.pragma.model.Constants;
import co.com.pragma.model.Franchise;
import co.com.pragma.model.gateways.BranchGateway;
import co.com.pragma.model.gateways.FranchiseGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetBranchByIdUseCaseTest {

    @Mock
    private BranchGateway branchGateway;
    @Mock
    private FranchiseGateway franchiseGateway;

    private GetBranchByIdUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetBranchByIdUseCase(branchGateway, franchiseGateway);
    }

    @Test
    void execute_Success() {
        Franchise franchise = new Franchise();
        franchise.setId("1");
        Branch branch = new Branch();
        branch.setId("1");

        when(franchiseGateway.findById("1")).thenReturn(Mono.just(franchise));
        when(branchGateway.findById("1", "1")).thenReturn(Mono.just(branch));

        StepVerifier.create(useCase.execute("1", "1"))
                .expectNext(branch)
                .verifyComplete();

        verify(franchiseGateway).findById("1");
        verify(branchGateway).findById("1", "1");
    }

    @Test
    void execute_FranchiseIdNull() {
        StepVerifier.create(useCase.execute(null, "1"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_FRANCHISE_NOT_FOUND))
                .verify();

        verifyNoInteractions(franchiseGateway, branchGateway);
    }

    @Test
    void execute_FranchiseIdEmpty() {
        StepVerifier.create(useCase.execute("", "1"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_FRANCHISE_NOT_FOUND))
                .verify();

        verifyNoInteractions(franchiseGateway, branchGateway);
    }

    @Test
    void execute_BranchIdNull() {
        StepVerifier.create(useCase.execute("1", null))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_BRANCH_NOT_FOUND))
                .verify();

        verifyNoInteractions(franchiseGateway, branchGateway);
    }

    @Test
    void execute_BranchIdEmpty() {
        StepVerifier.create(useCase.execute("1", ""))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_BRANCH_NOT_FOUND))
                .verify();

        verifyNoInteractions(franchiseGateway, branchGateway);
    }

    @Test
    void execute_FranchiseNotFound() {
        when(franchiseGateway.findById("999")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute("999", "1"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_FRANCHISE_NOT_FOUND))
                .verify();

        verify(franchiseGateway).findById("999");
        verifyNoInteractions(branchGateway);
    }

    @Test
    void execute_BranchNotFound() {
        Franchise franchise = new Franchise();
        franchise.setId("1");

        when(franchiseGateway.findById("1")).thenReturn(Mono.just(franchise));
        when(branchGateway.findById("1", "999")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute("1", "999"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_BRANCH_NOT_FOUND))
                .verify();

        verify(franchiseGateway).findById("1");
        verify(branchGateway).findById("1", "999");
    }
}
