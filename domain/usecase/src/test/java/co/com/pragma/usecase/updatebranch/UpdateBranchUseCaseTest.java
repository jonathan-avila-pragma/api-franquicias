package co.com.pragma.usecase.updatebranch;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateBranchUseCaseTest {

    @Mock
    private BranchGateway branchGateway;
    @Mock
    private FranchiseGateway franchiseGateway;

    private UpdateBranchUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateBranchUseCase(branchGateway, franchiseGateway);
    }

    @Test
    void execute_UpdateAllFields() {
        Franchise franchise = Franchise.builder().id("1").name("Test Franchise").build();
        Branch existingBranch = Branch.builder().id("1").name("Old Name").address("Old Address").city("Old City").build();
        Branch branchUpdate = new Branch();
        branchUpdate.setName("New Name");
        branchUpdate.setAddress("New Address");
        branchUpdate.setCity("New City");
        Branch updatedBranch = Branch.builder().id("1").name("New Name").address("New Address").city("New City").build();

        when(franchiseGateway.findById("1")).thenReturn(Mono.just(franchise));
        when(branchGateway.findById("1", "1")).thenReturn(Mono.just(existingBranch));
        when(branchGateway.update("1", existingBranch)).thenReturn(Mono.just(updatedBranch));

        StepVerifier.create(useCase.execute("1", "1", branchUpdate))
                .expectNext(updatedBranch)
                .verifyComplete();

        verify(franchiseGateway).findById("1");
        verify(branchGateway).findById("1", "1");
        verify(branchGateway).update("1", existingBranch);
    }

    @Test
    void execute_UpdateOnlyName() {
        Franchise franchise = Franchise.builder().id("1").name("Test Franchise").build();
        Branch existingBranch = Branch.builder().id("1").name("Old Name").address("Old Address").city("Old City").build();
        Branch branchUpdate = new Branch();
        branchUpdate.setName("New Name");

        when(franchiseGateway.findById("1")).thenReturn(Mono.just(franchise));
        when(branchGateway.findById("1", "1")).thenReturn(Mono.just(existingBranch));
        when(branchGateway.update("1", existingBranch)).thenReturn(Mono.just(existingBranch));

        StepVerifier.create(useCase.execute("1", "1", branchUpdate))
                .assertNext(branch -> {
                    assertEquals("New Name", branch.getName());
                    assertEquals("Old Address", branch.getAddress());
                    assertEquals("Old City", branch.getCity());
                })
                .verifyComplete();

        verify(franchiseGateway).findById("1");
        verify(branchGateway).findById("1", "1");
        verify(branchGateway).update("1", existingBranch);
    }

    @Test
    void execute_UpdateOnlyAddress() {
        Franchise franchise = Franchise.builder().id("1").name("Test Franchise").build();
        Branch existingBranch = Branch.builder().id("1").name("Old Name").address("Old Address").city("Old City").build();
        Branch branchUpdate = new Branch();
        branchUpdate.setAddress("New Address");

        when(franchiseGateway.findById("1")).thenReturn(Mono.just(franchise));
        when(branchGateway.findById("1", "1")).thenReturn(Mono.just(existingBranch));
        when(branchGateway.update("1", existingBranch)).thenReturn(Mono.just(existingBranch));

        StepVerifier.create(useCase.execute("1", "1", branchUpdate))
                .assertNext(branch -> {
                    assertEquals("Old Name", branch.getName());
                    assertEquals("New Address", branch.getAddress());
                    assertEquals("Old City", branch.getCity());
                })
                .verifyComplete();

        verify(franchiseGateway).findById("1");
        verify(branchGateway).findById("1", "1");
        verify(branchGateway).update("1", existingBranch);
    }

    @Test
    void execute_UpdateOnlyCity() {
        Franchise franchise = Franchise.builder().id("1").name("Test Franchise").build();
        Branch existingBranch = Branch.builder().id("1").name("Old Name").address("Old Address").city("Old City").build();
        Branch branchUpdate = new Branch();
        branchUpdate.setCity("New City");

        when(franchiseGateway.findById("1")).thenReturn(Mono.just(franchise));
        when(branchGateway.findById("1", "1")).thenReturn(Mono.just(existingBranch));
        when(branchGateway.update("1", existingBranch)).thenReturn(Mono.just(existingBranch));

        StepVerifier.create(useCase.execute("1", "1", branchUpdate))
                .assertNext(branch -> {
                    assertEquals("Old Name", branch.getName());
                    assertEquals("Old Address", branch.getAddress());
                    assertEquals("New City", branch.getCity());
                })
                .verifyComplete();

        verify(franchiseGateway).findById("1");
        verify(branchGateway).findById("1", "1");
        verify(branchGateway).update("1", existingBranch);
    }

    @Test
    void execute_TrimWhitespace() {
        Franchise franchise = Franchise.builder().id("1").name("Test Franchise").build();
        Branch existingBranch = Branch.builder().id("1").name("Old Name").address("Old Address").city("Old City").build();
        Branch branchUpdate = new Branch();
        branchUpdate.setName("  New Name  ");
        branchUpdate.setAddress("  New Address  ");
        branchUpdate.setCity("  New City  ");

        when(franchiseGateway.findById("1")).thenReturn(Mono.just(franchise));
        when(branchGateway.findById("1", "1")).thenReturn(Mono.just(existingBranch));
        when(branchGateway.update("1", existingBranch)).thenReturn(Mono.just(existingBranch));

        StepVerifier.create(useCase.execute("1", "1", branchUpdate))
                .assertNext(branch -> {
                    assertEquals("New Name", branch.getName());
                    assertEquals("New Address", branch.getAddress());
                    assertEquals("New City", branch.getCity());
                })
                .verifyComplete();

        verify(franchiseGateway).findById("1");
        verify(branchGateway).findById("1", "1");
        verify(branchGateway).update("1", existingBranch);
    }

    @Test
    void execute_IgnoreEmptyName() {
        Franchise franchise = Franchise.builder().id("1").name("Test Franchise").build();
        Branch existingBranch = Branch.builder().id("1").name("Old Name").address("Old Address").city("Old City").build();
        Branch branchUpdate = new Branch();
        branchUpdate.setName("   ");

        when(franchiseGateway.findById("1")).thenReturn(Mono.just(franchise));
        when(branchGateway.findById("1", "1")).thenReturn(Mono.just(existingBranch));
        when(branchGateway.update("1", existingBranch)).thenReturn(Mono.just(existingBranch));

        StepVerifier.create(useCase.execute("1", "1", branchUpdate))
                .assertNext(branch -> assertEquals("Old Name", branch.getName()))
                .verifyComplete();

        verify(franchiseGateway).findById("1");
        verify(branchGateway).findById("1", "1");
        verify(branchGateway).update("1", existingBranch);
    }

    @Test
    void execute_NoFieldsToUpdate() {
        Franchise franchise = Franchise.builder().id("1").name("Test Franchise").build();
        Branch existingBranch = Branch.builder().id("1").name("Old Name").address("Old Address").city("Old City").build();
        Branch branchUpdate = new Branch();

        when(franchiseGateway.findById("1")).thenReturn(Mono.just(franchise));
        when(branchGateway.findById("1", "1")).thenReturn(Mono.just(existingBranch));
        when(branchGateway.update("1", existingBranch)).thenReturn(Mono.just(existingBranch));

        StepVerifier.create(useCase.execute("1", "1", branchUpdate))
                .assertNext(branch -> {
                    assertEquals("Old Name", branch.getName());
                    assertEquals("Old Address", branch.getAddress());
                    assertEquals("Old City", branch.getCity());
                })
                .verifyComplete();

        verify(franchiseGateway).findById("1");
        verify(branchGateway).findById("1", "1");
        verify(branchGateway).update("1", existingBranch);
    }

    @Test
    void execute_FranchiseNotFound() {
        Branch branchUpdate = new Branch();
        branchUpdate.setName("New Name");

        when(franchiseGateway.findById("999")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute("999", "1", branchUpdate))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_FRANCHISE_NOT_FOUND))
                .verify();

        verify(franchiseGateway).findById("999");
        verifyNoInteractions(branchGateway);
    }

    @Test
    void execute_BranchNotFound() {
        Franchise franchise = Franchise.builder().id("1").name("Test Franchise").build();
        Branch branchUpdate = new Branch();
        branchUpdate.setName("New Name");

        when(franchiseGateway.findById("1")).thenReturn(Mono.just(franchise));
        when(branchGateway.findById("1", "999")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute("1", "999", branchUpdate))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_BRANCH_NOT_FOUND))
                .verify();

        verify(franchiseGateway).findById("1");
        verify(branchGateway).findById("1", "999");
        verify(branchGateway, never()).update(anyString(), any());
    }
}