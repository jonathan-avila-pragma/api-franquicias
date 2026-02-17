package co.com.pragma.usecase.addbranch;

import co.com.pragma.model.Branch;
import co.com.pragma.model.Constants;
import co.com.pragma.model.Franchise;
import co.com.pragma.model.gateways.BranchGateway;
import co.com.pragma.model.gateways.FranchiseGateway;
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
class AddBranchUseCaseTest {

    @Mock
    private BranchGateway branchGateway;

    @Mock
    private FranchiseGateway franchiseGateway;

    private AddBranchUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new AddBranchUseCase(branchGateway, franchiseGateway);
    }

    @Test
    void shouldAddBranchSuccessfully() {
        String franchiseId = "1";
        String branchId = "10";
        Branch branch = new Branch();
        branch.setName("Test Branch");

        Franchise franchise = new Franchise();
        franchise.setId(franchiseId);

        Branch savedBranch = new Branch();
        savedBranch.setId(branchId);
        savedBranch.setName("Test Branch");

        when(franchiseGateway.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(branchGateway.getNextId()).thenReturn(Mono.just(branchId));
        when(branchGateway.save(eq(franchiseId), any(Branch.class))).thenReturn(Mono.just(savedBranch));

        StepVerifier.create(useCase.execute(franchiseId, branch))
                .expectNextMatches(result -> result.getId().equals(branchId) && result.getName().equals("Test Branch"))
                .verifyComplete();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldFailWhenNameIsInvalid(String name) {
        Branch branch = new Branch();
        branch.setName(name);

        StepVerifier.create(useCase.execute("1", branch))
                .expectErrorMatches(error -> error instanceof IllegalArgumentException 
                        && error.getMessage().equals(Constants.ERROR_INVALID_NAME))
                .verify();
    }

    @Test
    void shouldFailWhenFranchiseNotFound() {
        String franchiseId = "999";
        Branch branch = new Branch();
        branch.setName("Test Branch");

        when(franchiseGateway.findById(franchiseId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(franchiseId, branch))
                .expectErrorMatches(error -> error instanceof IllegalArgumentException 
                        && error.getMessage().equals(Constants.ERROR_FRANCHISE_NOT_FOUND))
                .verify();
    }
}
