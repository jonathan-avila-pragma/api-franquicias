package co.com.pragma.usecase.updatefranchisename;

import co.com.pragma.model.Constants;
import co.com.pragma.model.Franchise;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateFranchiseNameUseCaseTest {

    @Mock
    private FranchiseGateway franchiseGateway;

    private UpdateFranchiseNameUseCase updateFranchiseNameUseCase;

    @BeforeEach
    void setUp() {
        updateFranchiseNameUseCase = new UpdateFranchiseNameUseCase(franchiseGateway);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void testExecuteWithInvalidName(String name) {
        StepVerifier.create(updateFranchiseNameUseCase.execute("1", name))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_INVALID_NAME))
                .verify();

        verify(franchiseGateway, never()).findById(anyString());
        verify(franchiseGateway, never()).update(any());
    }

    @Test
    void testExecuteFranchiseNotFound() {
        when(franchiseGateway.findById("999")).thenReturn(Mono.empty());

        StepVerifier.create(updateFranchiseNameUseCase.execute("999", "New Name"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_FRANCHISE_NOT_FOUND))
                .verify();

        verify(franchiseGateway).findById("999");
        verify(franchiseGateway, never()).update(any());
    }

    @Test
    void testExecuteSuccess() {
        Franchise franchise = new Franchise("1", "Old Name", "Description");
        Franchise updatedFranchise = new Franchise("1", "New Name", "Description");

        when(franchiseGateway.findById("1")).thenReturn(Mono.just(franchise));
        when(franchiseGateway.update(any(Franchise.class))).thenReturn(Mono.just(updatedFranchise));

        StepVerifier.create(updateFranchiseNameUseCase.execute("1", "New Name"))
                .expectNextMatches(f -> f.getName().equals("New Name"))
                .verifyComplete();

        verify(franchiseGateway).findById("1");
        verify(franchiseGateway).update(any(Franchise.class));
    }
}
