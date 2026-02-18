package co.com.pragma.usecase.createfranchise;

import co.com.pragma.model.Constants;
import co.com.pragma.model.Franchise;
import co.com.pragma.model.gateways.FranchiseGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateFranchiseUseCaseTest {

    @Mock
    private FranchiseGateway franchiseGateway;

    private CreateFranchiseUseCase createFranchiseUseCase;

    @BeforeEach
    void setUp() {
        createFranchiseUseCase = new CreateFranchiseUseCase(franchiseGateway);
    }

    @Test
    void testExecuteWithNullName() {
        Franchise franchise = new Franchise();
        franchise.setName(null);

        StepVerifier.create(createFranchiseUseCase.execute(franchise))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_INVALID_NAME))
                .verify();

        verify(franchiseGateway, never()).save(any());
        verify(franchiseGateway, never()).getNextId();
    }

    @Test
    void testExecuteWithEmptyName() {
        Franchise franchise = new Franchise();
        franchise.setName("   ");

        StepVerifier.create(createFranchiseUseCase.execute(franchise))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_INVALID_NAME))
                .verify();

        verify(franchiseGateway, never()).save(any());
        verify(franchiseGateway, never()).getNextId();
    }

    @Test
    void testExecuteWithIdProvided() {
        Franchise franchise = Franchise.builder().id("1").name("Franchise 1").description("Description").build();
        Franchise savedFranchise = Franchise.builder().id("1").name("Franchise 1").description("Description").build();

        when(franchiseGateway.save(any(Franchise.class))).thenReturn(Mono.just(savedFranchise));

        StepVerifier.create(createFranchiseUseCase.execute(franchise))
                .expectNextMatches(f -> f.getId().equals("1") && f.getName().equals("Franchise 1"))
                .verifyComplete();

        verify(franchiseGateway).save(any(Franchise.class));
        verify(franchiseGateway, never()).getNextId();
    }

    @Test
    void testExecuteWithEmptyId() {
        Franchise franchise = new Franchise();
        franchise.setId("");
        franchise.setName("Franchise 1");
        franchise.setDescription("Description");

        Franchise savedFranchise = Franchise.builder().id("2").name("Franchise 1").description("Description").build();

        when(franchiseGateway.getNextId()).thenReturn(Mono.just("2"));
        when(franchiseGateway.save(any(Franchise.class))).thenReturn(Mono.just(savedFranchise));

        StepVerifier.create(createFranchiseUseCase.execute(franchise))
                .expectNextMatches(f -> f.getId().equals("2") && f.getName().equals("Franchise 1"))
                .verifyComplete();

        verify(franchiseGateway).getNextId();
        verify(franchiseGateway).save(any(Franchise.class));
    }

    @Test
    void testExecuteWithNullId() {
        Franchise franchise = new Franchise();
        franchise.setId(null);
        franchise.setName("Franchise 1");
        franchise.setDescription("Description");

        Franchise savedFranchise = Franchise.builder().id("3").name("Franchise 1").description("Description").build();

        when(franchiseGateway.getNextId()).thenReturn(Mono.just("3"));
        when(franchiseGateway.save(any(Franchise.class))).thenReturn(Mono.just(savedFranchise));

        StepVerifier.create(createFranchiseUseCase.execute(franchise))
                .expectNextMatches(f -> f.getId().equals("3") && f.getName().equals("Franchise 1"))
                .verifyComplete();

        verify(franchiseGateway).getNextId();
        verify(franchiseGateway).save(any(Franchise.class));
    }
}
