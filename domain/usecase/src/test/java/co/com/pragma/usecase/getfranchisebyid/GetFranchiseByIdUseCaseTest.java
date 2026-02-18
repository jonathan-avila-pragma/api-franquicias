package co.com.pragma.usecase.getfranchisebyid;

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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetFranchiseByIdUseCaseTest {

    @Mock
    private FranchiseGateway franchiseGateway;

    private GetFranchiseByIdUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetFranchiseByIdUseCase(franchiseGateway);
    }

    @Test
    void execute_Success() {
        Franchise franchise = Franchise.builder().id("1").name("Test Franchise").build();

        when(franchiseGateway.findById("1")).thenReturn(Mono.just(franchise));

        StepVerifier.create(useCase.execute("1"))
                .expectNext(franchise)
                .verifyComplete();

        verify(franchiseGateway).findById("1");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void execute_FranchiseIdInvalid(String franchiseId) {
        StepVerifier.create(useCase.execute(franchiseId))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_FRANCHISE_NOT_FOUND))
                .verify();

        verifyNoInteractions(franchiseGateway);
    }

    @Test
    void execute_FranchiseNotFound() {
        when(franchiseGateway.findById("999")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute("999"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_FRANCHISE_NOT_FOUND))
                .verify();

        verify(franchiseGateway).findById("999");
    }
}