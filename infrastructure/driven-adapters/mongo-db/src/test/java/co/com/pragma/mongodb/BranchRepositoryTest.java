package co.com.pragma.mongodb;

import co.com.pragma.model.Branch;
import co.com.pragma.model.Constants;
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
class BranchRepositoryTest {

    @Mock
    private ReactiveMongoTemplate mongoTemplate;

    private CircuitBreaker circuitBreaker;
    private BranchRepository branchRepository;

    @BeforeEach
    void setUp() {
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();
        circuitBreaker = registry.circuitBreaker("mongoCircuitBreaker");
        branchRepository = new BranchRepository(mongoTemplate, circuitBreaker);
    }

    @Test
    void testSave() {
        Branch branch = Branch.builder().id("1").name("Branch 1").address("Address").city("City").build();
        BranchEntity entity = new BranchEntity("1", "franchise1", "Branch 1", "Address", "City");
        
        when(mongoTemplate.save(any(BranchEntity.class))).thenReturn(Mono.just(entity));

        StepVerifier.create(branchRepository.save("franchise1", branch))
                .expectNextMatches(b -> b.getId().equals("1") 
                        && b.getName().equals("Branch 1")
                        && "Address".equals(b.getAddress())
                        && "City".equals(b.getCity())
                        && b.getProducts() != null)
                .verifyComplete();

        verify(mongoTemplate).save(any(BranchEntity.class));
    }

    @Test
    void testFindById() {
        BranchEntity entity = new BranchEntity("1", "franchise1", "Branch 1", "Address", "City");
        
        when(mongoTemplate.findOne(any(Query.class), eq(BranchEntity.class))).thenReturn(Mono.just(entity));

        StepVerifier.create(branchRepository.findById("franchise1", "1"))
                .expectNextMatches(b -> b.getId().equals("1") 
                        && b.getName().equals("Branch 1")
                        && "Address".equals(b.getAddress())
                        && "City".equals(b.getCity())
                        && b.getProducts() != null)
                .verifyComplete();

        verify(mongoTemplate).findOne(any(Query.class), eq(BranchEntity.class));
    }

    @Test
    void testFindByIdNotFound() {
        when(mongoTemplate.findOne(any(Query.class), eq(BranchEntity.class))).thenReturn(Mono.empty());

        StepVerifier.create(branchRepository.findById("franchise1", "999"))
                .verifyComplete();

        verify(mongoTemplate).findOne(any(Query.class), eq(BranchEntity.class));
    }

    @Test
    void testDeleteById() {
        when(mongoTemplate.remove(any(Query.class), eq(BranchEntity.class))).thenReturn(Mono.just(DeleteResult.acknowledged(1)));

        StepVerifier.create(branchRepository.deleteById("franchise1", "1"))
                .verifyComplete();

        verify(mongoTemplate).remove(any(Query.class), eq(BranchEntity.class));
    }

    @Test
    void testUpdate() {
        Branch branch = Branch.builder().id("1").name("Updated Branch").address("New Address").city("New City").build();
        BranchEntity existingEntity = new BranchEntity("1", "franchise1", "Branch 1", "Address", "City");
        BranchEntity updatedEntity = new BranchEntity("1", "franchise1", "Updated Branch", "New Address", "New City");
        
        when(mongoTemplate.findOne(any(Query.class), eq(BranchEntity.class))).thenReturn(Mono.just(existingEntity));
        when(mongoTemplate.save(any(BranchEntity.class))).thenReturn(Mono.just(updatedEntity));

        StepVerifier.create(branchRepository.update("franchise1", branch))
                .expectNextMatches(b -> b.getName().equals("Updated Branch")
                        && b.getAddress().equals("New Address")
                        && b.getCity().equals("New City"))
                .verifyComplete();

        verify(mongoTemplate).findOne(any(Query.class), eq(BranchEntity.class));
        verify(mongoTemplate).save(any(BranchEntity.class));
    }

    @Test
    void testUpdateNotFound() {
        Branch branch = Branch.builder().id("999").name("Updated Branch").address("Address").city("City").build();
        
        when(mongoTemplate.findOne(any(Query.class), eq(BranchEntity.class))).thenReturn(Mono.empty());

        StepVerifier.create(branchRepository.update("franchise1", branch))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException && 
                        e.getMessage().equals(Constants.ERROR_BRANCH_NOT_FOUND))
                .verify();

        verify(mongoTemplate).findOne(any(Query.class), eq(BranchEntity.class));
        verify(mongoTemplate, never()).save(any(BranchEntity.class));
    }

    @Test
    void testFindAllByFranchiseId() {
        BranchEntity entity1 = new BranchEntity("1", "franchise1", "Branch 1", "Address 1", "City 1");
        BranchEntity entity2 = new BranchEntity("2", "franchise1", "Branch 2", "Address 2", "City 2");
        
        when(mongoTemplate.find(any(Query.class), eq(BranchEntity.class)))
                .thenReturn(Flux.just(entity1, entity2));

        StepVerifier.create(branchRepository.findAllByFranchiseId("franchise1"))
                .expectNextMatches(b -> b.getId().equals("1") 
                        && b.getName().equals("Branch 1")
                        && "Address 1".equals(b.getAddress())
                        && "City 1".equals(b.getCity())
                        && b.getProducts() != null)
                .expectNextMatches(b -> b.getId().equals("2")
                        && b.getName().equals("Branch 2")
                        && "Address 2".equals(b.getAddress())
                        && "City 2".equals(b.getCity())
                        && b.getProducts() != null)
                .verifyComplete();

        verify(mongoTemplate).find(any(Query.class), eq(BranchEntity.class));
    }

    @Test
    void testFindAllByFranchiseIdEmpty() {
        when(mongoTemplate.find(any(Query.class), eq(BranchEntity.class))).thenReturn(Flux.empty());

        StepVerifier.create(branchRepository.findAllByFranchiseId("franchise999"))
                .verifyComplete();

        verify(mongoTemplate).find(any(Query.class), eq(BranchEntity.class));
    }

    @Test
    void testGetNextId() {
        SequenceEntity sequence = new SequenceEntity("branch_sequence", 5L);
        
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(SequenceEntity.class)))
                .thenReturn(Mono.just(sequence));

        StepVerifier.create(branchRepository.getNextId())
                .expectNext("5")
                .verifyComplete();

        verify(mongoTemplate).findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(SequenceEntity.class));
    }

    @Test
    void testGetNextIdInitialSequence() {
        SequenceEntity initialSequence = new SequenceEntity("branch_sequence", 0L);
        SequenceEntity incrementedSequence = new SequenceEntity("branch_sequence", 1L);
        
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(SequenceEntity.class)))
                .thenReturn(Mono.empty())
                .thenReturn(Mono.just(incrementedSequence));
        when(mongoTemplate.save(any(SequenceEntity.class))).thenReturn(Mono.just(initialSequence));

        StepVerifier.create(branchRepository.getNextId())
                .expectNext("1")
                .verifyComplete();

        verify(mongoTemplate, times(2)).findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(SequenceEntity.class));
        verify(mongoTemplate).save(any(SequenceEntity.class));
    }
}
