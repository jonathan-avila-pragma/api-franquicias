package co.com.pragma.mongodb;

import co.com.pragma.model.Branch;
import co.com.pragma.model.Constants;
import co.com.pragma.model.Product;
import co.com.pragma.model.gateways.BranchGateway;
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
class ProductRepositoryTest {

    @Mock
    private ReactiveMongoTemplate mongoTemplate;

    @Mock
    private BranchGateway branchGateway;

    private CircuitBreaker circuitBreaker;
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();
        circuitBreaker = registry.circuitBreaker("mongoCircuitBreaker");
        productRepository = new ProductRepository(mongoTemplate, branchGateway, circuitBreaker);
    }

    @Test
    void testSave() {
        Product product = new Product("1", "Product 1", 10);
        ProductEntity entity = new ProductEntity("1", "franchise1", "branch1", "Product 1", 10);

        when(mongoTemplate.save(any(ProductEntity.class))).thenReturn(Mono.just(entity));

        StepVerifier.create(productRepository.save("franchise1", "branch1", product))
                .expectNextMatches(p -> p.getId().equals("1") 
                        && p.getName().equals("Product 1")
                        && p.getStock().equals(10))
                .verifyComplete();

        verify(mongoTemplate).save(any(ProductEntity.class));
    }

    @Test
    void testFindById() {
        ProductEntity entity = new ProductEntity("1", "franchise1", "branch1", "Product 1", 10);

        when(mongoTemplate.findOne(any(Query.class), eq(ProductEntity.class))).thenReturn(Mono.just(entity));

        StepVerifier.create(productRepository.findById("franchise1", "branch1", "1"))
                .expectNextMatches(p -> p.getId().equals("1") 
                        && p.getName().equals("Product 1")
                        && p.getStock().equals(10))
                .verifyComplete();

        verify(mongoTemplate).findOne(any(Query.class), eq(ProductEntity.class));
    }

    @Test
    void testFindByIdNotFound() {
        when(mongoTemplate.findOne(any(Query.class), eq(ProductEntity.class))).thenReturn(Mono.empty());

        StepVerifier.create(productRepository.findById("franchise1", "branch1", "999"))
                .verifyComplete();

        verify(mongoTemplate).findOne(any(Query.class), eq(ProductEntity.class));
    }

    @Test
    void testFindByName() {
        ProductEntity entity = new ProductEntity("1", "franchise1", "branch1", "Product 1", 10);

        when(mongoTemplate.findOne(any(Query.class), eq(ProductEntity.class))).thenReturn(Mono.just(entity));

        StepVerifier.create(productRepository.findByName("franchise1", "branch1", "Product 1"))
                .expectNextMatches(p -> p.getId().equals("1") 
                        && p.getName().equals("Product 1")
                        && p.getStock().equals(10))
                .verifyComplete();

        verify(mongoTemplate).findOne(any(Query.class), eq(ProductEntity.class));
    }

    @Test
    void testFindByNameNotFound() {
        when(mongoTemplate.findOne(any(Query.class), eq(ProductEntity.class))).thenReturn(Mono.empty());

        StepVerifier.create(productRepository.findByName("franchise1", "branch1", "NonExistent"))
                .verifyComplete();

        verify(mongoTemplate).findOne(any(Query.class), eq(ProductEntity.class));
    }

    @Test
    void testDeleteById() {
        when(mongoTemplate.remove(any(Query.class), eq(ProductEntity.class))).thenReturn(Mono.just(DeleteResult.acknowledged(1)));

        StepVerifier.create(productRepository.deleteById("franchise1", "branch1", "1"))
                .verifyComplete();

        verify(mongoTemplate).remove(any(Query.class), eq(ProductEntity.class));
    }

    @Test
    void testUpdate() {
        Product product = new Product("1", "Updated Product", 20);
        ProductEntity existingEntity = new ProductEntity("1", "franchise1", "branch1", "Product 1", 10);
        ProductEntity updatedEntity = new ProductEntity("1", "franchise1", "branch1", "Updated Product", 20);

        when(mongoTemplate.findOne(any(Query.class), eq(ProductEntity.class)))
                .thenReturn(Mono.just(existingEntity));
        when(mongoTemplate.save(any(ProductEntity.class))).thenReturn(Mono.just(updatedEntity));

        StepVerifier.create(productRepository.update("franchise1", "branch1", product))
                .expectNextMatches(p -> p.getName().equals("Updated Product") && p.getStock().equals(20))
                .verifyComplete();

        verify(mongoTemplate, atLeastOnce()).findOne(any(Query.class), eq(ProductEntity.class));
        verify(mongoTemplate).save(any(ProductEntity.class));
    }

    @Test
    void testUpdateNotFound() {
        Product product = new Product("999", "Updated Product", 20);

        when(mongoTemplate.findOne(any(Query.class), eq(ProductEntity.class))).thenReturn(Mono.empty());

        StepVerifier.create(productRepository.update("franchise1", "branch1", product))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException 
                        && e.getMessage().equals(Constants.ERROR_PRODUCT_NOT_FOUND))
                .verify();

        verify(mongoTemplate).findOne(any(Query.class), eq(ProductEntity.class));
        verify(mongoTemplate, never()).save(any(ProductEntity.class));
    }

    @Test
    void testFindMaxStockProductsByFranchise() {
        ProductEntity product1 = new ProductEntity("1", "franchise1", "branch1", "Product 1", 50);
        ProductEntity product2 = new ProductEntity("2", "franchise1", "branch1", "Product 2", 30);
        ProductEntity product3 = new ProductEntity("3", "franchise1", "branch2", "Product 3", 100);
        ProductEntity product4 = new ProductEntity("4", "franchise1", "branch2", "Product 4", 80);

        Branch branch1 = Branch.builder().id("branch1").name("Branch 1").address("Address 1").city("City 1").build();
        Branch branch2 = Branch.builder().id("branch2").name("Branch 2").address("Address 2").city("City 2").build();

        when(branchGateway.findAllByFranchiseId("franchise1"))
                .thenReturn(Flux.just(branch1, branch2));
        when(mongoTemplate.find(any(Query.class), eq(ProductEntity.class)))
                .thenReturn(Flux.just(product1, product2))
                .thenReturn(Flux.just(product3, product4));

        StepVerifier.create(productRepository.findMaxStockProductsByFranchise("franchise1"))
                .expectNextMatches(pwb -> pwb.getProduct().getId().equals("1") 
                        && pwb.getProduct().getStock().equals(50)
                        && pwb.getBranch().getId().equals("branch1"))
                .expectNextMatches(pwb -> pwb.getProduct().getId().equals("3") 
                        && pwb.getProduct().getStock().equals(100)
                        && pwb.getBranch().getId().equals("branch2"))
                .verifyComplete();

        verify(branchGateway).findAllByFranchiseId("franchise1");
        verify(mongoTemplate, atLeastOnce()).find(any(Query.class), eq(ProductEntity.class));
    }

    @Test
    void testFindAllByBranch() {
        ProductEntity product1 = new ProductEntity("1", "franchise1", "branch1", "Product 1", 10);
        ProductEntity product2 = new ProductEntity("2", "franchise1", "branch1", "Product 2", 20);

        when(mongoTemplate.find(any(Query.class), eq(ProductEntity.class)))
                .thenReturn(Flux.just(product1, product2));

        StepVerifier.create(productRepository.findAllByBranch("franchise1", "branch1"))
                .expectNextMatches(p -> p.getId().equals("1") 
                        && p.getName().equals("Product 1")
                        && p.getStock().equals(10))
                .expectNextMatches(p -> p.getId().equals("2") 
                        && p.getName().equals("Product 2")
                        && p.getStock().equals(20))
                .verifyComplete();

        verify(mongoTemplate).find(any(Query.class), eq(ProductEntity.class));
    }

    @Test
    void testFindAllByBranchEmpty() {
        when(mongoTemplate.find(any(Query.class), eq(ProductEntity.class)))
                .thenReturn(Flux.empty());

        StepVerifier.create(productRepository.findAllByBranch("franchise1", "branch1"))
                .verifyComplete();

        verify(mongoTemplate).find(any(Query.class), eq(ProductEntity.class));
    }

    @Test
    void testGetNextId() {
        SequenceEntity sequence = new SequenceEntity("product_sequence", 5L);

        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(SequenceEntity.class)))
                .thenReturn(Mono.just(sequence));

        StepVerifier.create(productRepository.getNextId())
                .expectNext("5")
                .verifyComplete();

        verify(mongoTemplate).findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(SequenceEntity.class));
    }

    @Test
    void testGetNextIdInitialSequence() {
        SequenceEntity initialSequence = new SequenceEntity("product_sequence", 0L);
        SequenceEntity incrementedSequence = new SequenceEntity("product_sequence", 1L);

        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(SequenceEntity.class)))
                .thenReturn(Mono.empty())
                .thenReturn(Mono.just(incrementedSequence));
        when(mongoTemplate.save(any(SequenceEntity.class))).thenReturn(Mono.just(initialSequence));

        StepVerifier.create(productRepository.getNextId())
                .expectNext("1")
                .verifyComplete();

        verify(mongoTemplate, times(2)).findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(SequenceEntity.class));
        verify(mongoTemplate).save(any(SequenceEntity.class));
    }
}
