package co.com.pragma.usecase.getallfranchises;

import co.com.pragma.model.Franchise;
import co.com.pragma.model.gateways.FranchiseGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllFranchisesUseCaseTest {

    @Mock
    private FranchiseGateway franchiseGateway;

    private GetAllFranchisesUseCase getAllFranchisesUseCase;

    @BeforeEach
    void setUp() {
        getAllFranchisesUseCase = new GetAllFranchisesUseCase(franchiseGateway);
    }

    @Test
    void testExecute() {
        Franchise franchise1 = new Franchise("1", "Franchise 1", "Description 1");
        Franchise franchise2 = new Franchise("2", "Franchise 2", "Description 2");

        when(franchiseGateway.findAll()).thenReturn(Flux.just(franchise1, franchise2));

        StepVerifier.create(getAllFranchisesUseCase.execute())
                .expectNextMatches(f -> f.getId().equals("1") && f.getName().equals("Franchise 1"))
                .expectNextMatches(f -> f.getId().equals("2") && f.getName().equals("Franchise 2"))
                .verifyComplete();

        verify(franchiseGateway).findAll();
    }
}
