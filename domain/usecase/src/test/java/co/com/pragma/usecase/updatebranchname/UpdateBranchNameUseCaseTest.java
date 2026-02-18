package co.com.pragma.usecase.updatebranchname;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateBranchNameUseCaseTest {

    @Mock
    private BranchGateway branchGateway;

    @Mock
    private FranchiseGateway franchiseGateway;

    private UpdateBranchNameUseCase updateBranchNameUseCase;

    @BeforeEach
    void setUp() {
        updateBranchNameUseCase = new UpdateBranchNameUseCase(branchGateway, franchiseGateway);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void testExecuteWithInvalidName(String name) {
        StepVerifier.create(updateBranchNameUseCase.execute("1", "1", name))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_INVALID_NAME))
                .verify();

        verify(franchiseGateway, never()).findById(anyString());
    }

    @Test
    void testExecuteFranchiseNotFound() {
        when(franchiseGateway.findById("999")).thenReturn(Mono.empty());

        StepVerifier.create(updateBranchNameUseCase.execute("999", "1", "New Name"))
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

        StepVerifier.create(updateBranchNameUseCase.execute("1", "999", "New Name"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_BRANCH_NOT_FOUND))
                .verify();

        verify(franchiseGateway).findById("1");
        verify(branchGateway).findById("1", "999");
        verify(branchGateway, never()).update(anyString(), any());
    }

    @Test
    void testExecuteSuccess() {
        Franchise franchise = Franchise.builder().id("1").name("Franchise 1").description("Description").build();
        Branch branch = Branch.builder().id("1").name("Old Name").address("Address").city("City").build();
        Branch updatedBranch = Branch.builder().id("1").name("New Name").address("Address").city("City").build();

        when(franchiseGateway.findById("1")).thenReturn(Mono.just(franchise));
        when(branchGateway.findById("1", "1")).thenReturn(Mono.just(branch));
        when(branchGateway.update(eq("1"), any(Branch.class))).thenReturn(Mono.just(updatedBranch));

        StepVerifier.create(updateBranchNameUseCase.execute("1", "1", "New Name"))
                .expectNextMatches(b -> b.getName().equals("New Name"))
                .verifyComplete();

        verify(franchiseGateway).findById("1");
        verify(branchGateway).findById("1", "1");
        verify(branchGateway).update(eq("1"), any(Branch.class));
    }
}
