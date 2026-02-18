package co.com.pragma.mongodb;

import co.com.pragma.model.Constants;
import co.com.pragma.model.Franchise;
import com.mongodb.client.result.DeleteResult;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FranchiseRepositoryTest {

    @Mock
    private ReactiveMongoTemplate mongoTemplate;

    private CircuitBreaker circuitBreaker;
    private FranchiseRepository franchiseRepository;

    @BeforeEach
    void setUp() {
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();
        circuitBreaker = registry.circuitBreaker("mongoCircuitBreaker");
        franchiseRepository = new FranchiseRepository(mongoTemplate, circuitBreaker);
    }

    @Test
    void testSave() {
        Franchise franchise = Franchise.builder().id("1").name("Franchise 1").description("Description").build();
        FranchiseEntity entity = new FranchiseEntity("1", "Franchise 1", "Description");
        
        when(mongoTemplate.save(any(FranchiseEntity.class))).thenReturn(Mono.just(entity));

        StepVerifier.create(franchiseRepository.save(franchise))
                .expectNextMatches(f -> f.getId().equals("1") 
                        && f.getName().equals("Franchise 1")
                        && "Description".equals(f.getDescription())
                        && f.getBranches() != null)
                .verifyComplete();

        verify(mongoTemplate).save(any(FranchiseEntity.class));
    }

    @Test
    void testFindById() {
        FranchiseEntity entity = new FranchiseEntity("1", "Franchise 1", "Description");
        
        when(mongoTemplate.findById("1", FranchiseEntity.class)).thenReturn(Mono.just(entity));

        StepVerifier.create(franchiseRepository.findById("1"))
                .expectNextMatches(f -> f.getId().equals("1") 
                        && f.getName().equals("Franchise 1")
                        && "Description".equals(f.getDescription())
                        && f.getBranches() != null)
                .verifyComplete();

        verify(mongoTemplate).findById("1", FranchiseEntity.class);
    }

    @Test
    void testFindByIdNotFound() {
        when(mongoTemplate.findById("999", FranchiseEntity.class)).thenReturn(Mono.empty());

        StepVerifier.create(franchiseRepository.findById("999"))
                .verifyComplete();

        verify(mongoTemplate).findById("999", FranchiseEntity.class);
    }

    @Test
    void testDeleteById() {
        when(mongoTemplate.remove(any(Query.class), eq(FranchiseEntity.class))).thenReturn(Mono.just(DeleteResult.acknowledged(1)));

        StepVerifier.create(franchiseRepository.deleteById("1"))
                .verifyComplete();

        verify(mongoTemplate).remove(any(Query.class), eq(FranchiseEntity.class));
    }

    @Test
    void testUpdate() {
        Franchise franchise = Franchise.builder().id("1").name("Updated Franchise").description("Updated Description").build();
        FranchiseEntity existingEntity = new FranchiseEntity("1", "Franchise 1", "Description");
        FranchiseEntity updatedEntity = new FranchiseEntity("1", "Updated Franchise", "Updated Description");
        
        when(mongoTemplate.findById("1", FranchiseEntity.class)).thenReturn(Mono.just(existingEntity));
        when(mongoTemplate.save(any(FranchiseEntity.class))).thenReturn(Mono.just(updatedEntity));

        StepVerifier.create(franchiseRepository.update(franchise))
                .expectNextMatches(f -> f.getName().equals("Updated Franchise")
                        && "Updated Description".equals(f.getDescription())
                        && f.getBranches() != null)
                .verifyComplete();

        verify(mongoTemplate).findById("1", FranchiseEntity.class);
        verify(mongoTemplate).save(any(FranchiseEntity.class));
    }

    @Test
    void testUpdateNotFound() {
        Franchise franchise = Franchise.builder().id("999").name("Updated Franchise").description("Description").build();
        
        when(mongoTemplate.findById("999", FranchiseEntity.class)).thenReturn(Mono.empty());

        StepVerifier.create(franchiseRepository.update(franchise))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException && 
                        e.getMessage().equals(Constants.ERROR_FRANCHISE_NOT_FOUND))
                .verify();

        verify(mongoTemplate).findById("999", FranchiseEntity.class);
        verify(mongoTemplate, never()).save(any(FranchiseEntity.class));
    }

    @Test
    void testFindAll() {
        FranchiseEntity entity1 = new FranchiseEntity("1", "Franchise 1", "Description 1");
        FranchiseEntity entity2 = new FranchiseEntity("2", "Franchise 2", "Description 2");
        
        when(mongoTemplate.findAll(FranchiseEntity.class)).thenReturn(Flux.just(entity1, entity2));

        StepVerifier.create(franchiseRepository.findAll())
                .expectNextMatches(f -> f.getId().equals("1") 
                        && f.getName().equals("Franchise 1")
                        && "Description 1".equals(f.getDescription())
                        && f.getBranches() != null)
                .expectNextMatches(f -> f.getId().equals("2")
                        && f.getName().equals("Franchise 2")
                        && "Description 2".equals(f.getDescription())
                        && f.getBranches() != null)
                .verifyComplete();

        verify(mongoTemplate).findAll(FranchiseEntity.class);
    }

    @Test
    void testFindAllEmpty() {
        when(mongoTemplate.findAll(FranchiseEntity.class)).thenReturn(Flux.empty());

        StepVerifier.create(franchiseRepository.findAll())
                .verifyComplete();

        verify(mongoTemplate).findAll(FranchiseEntity.class);
    }

    @Test
    void testGetNextId() {
        SequenceEntity sequence = new SequenceEntity("franchise_sequence", 5L);
        
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(SequenceEntity.class)))
                .thenReturn(Mono.just(sequence));

        StepVerifier.create(franchiseRepository.getNextId())
                .expectNext("5")
                .verifyComplete();

        verify(mongoTemplate).findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(SequenceEntity.class));
    }

    @Test
    void testGetNextIdInitialSequence() {
        SequenceEntity initialSequence = new SequenceEntity("franchise_sequence", 0L);
        SequenceEntity incrementedSequence = new SequenceEntity("franchise_sequence", 1L);
        
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(SequenceEntity.class)))
                .thenReturn(Mono.empty())
                .thenReturn(Mono.just(incrementedSequence));
        when(mongoTemplate.save(any(SequenceEntity.class))).thenReturn(Mono.just(initialSequence));

        StepVerifier.create(franchiseRepository.getNextId())
                .expectNext("1")
                .verifyComplete();

        verify(mongoTemplate, times(2)).findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(SequenceEntity.class));
        verify(mongoTemplate).save(any(SequenceEntity.class));
    }
}
