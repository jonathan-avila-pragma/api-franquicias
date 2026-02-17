package co.com.pragma.mongodb;

import co.com.pragma.model.Constants;
import co.com.pragma.model.Product;
import co.com.pragma.model.ProductWithBranch;
import co.com.pragma.model.gateways.BranchGateway;
import co.com.pragma.model.gateways.ProductGateway;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.Objects;

@Slf4j
@Repository
public class ProductRepository implements ProductGateway {

    private final ReactiveMongoTemplate mongoTemplate;
    private final BranchGateway branchGateway;
    private final CircuitBreaker circuitBreaker;

    public ProductRepository(ReactiveMongoTemplate mongoTemplate, BranchGateway branchGateway, CircuitBreaker mongoCircuitBreaker) {
        this.mongoTemplate = mongoTemplate;
        this.branchGateway = branchGateway;
        this.circuitBreaker = mongoCircuitBreaker;
    }

    @Override
    public Mono<Product> save(String franchiseId, String branchId, Product product) {
        ProductEntity entity = new ProductEntity(franchiseId, branchId, product.getId(), product.getName(), product.getStock());
        return mongoTemplate.save(entity)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .map(e -> {
                    Product p = new Product();
                    p.setId(e.getId());
                    p.setName(e.getName());
                    p.setStock(e.getStock());
                    return p;
                });
    }

    @Override
    public Mono<Product> findById(String franchiseId, String branchId, String productId) {
        Query query = new Query(Criteria.where("franchiseId").is(franchiseId)
                .and("branchId").is(branchId)
                .and("id").is(productId));
        return mongoTemplate.findOne(query, ProductEntity.class)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .map(entity -> {
                    Product product = new Product();
                    product.setId(entity.getId());
                    product.setName(entity.getName());
                    product.setStock(entity.getStock());
                    return product;
                });
    }

    @Override
    public Mono<Product> findByName(String franchiseId, String branchId, String productName) {
        Query query = new Query(Criteria.where("franchiseId").is(franchiseId)
                .and("branchId").is(branchId)
                .and("name").is(productName));
        return mongoTemplate.findOne(query, ProductEntity.class)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .map(entity -> {
                    Product product = new Product();
                    product.setId(entity.getId());
                    product.setName(entity.getName());
                    product.setStock(entity.getStock());
                    return product;
                });
    }

    @Override
    public Mono<Void> deleteById(String franchiseId, String branchId, String productId) {
        Query query = new Query(Criteria.where("franchiseId").is(franchiseId)
                .and("branchId").is(branchId)
                .and("id").is(productId));
        return mongoTemplate.remove(query, ProductEntity.class)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .then();
    }

    @Override
    public Mono<Product> update(String franchiseId, String branchId, Product product) {
        return findById(franchiseId, branchId, product.getId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constants.ERROR_PRODUCT_NOT_FOUND)))
                .flatMap(existing -> save(franchiseId, branchId, product));
    }

    @Override
    public Flux<ProductWithBranch> findMaxStockProductsByFranchise(String franchiseId) {
        return branchGateway.findAllByFranchiseId(franchiseId)
                .flatMap(branch -> {
                    Query query = new Query(Criteria.where("franchiseId").is(franchiseId)
                            .and("branchId").is(branch.getId()));
                    return mongoTemplate.find(query, ProductEntity.class)
                            .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                            .map(entity -> {
                                Product product = new Product();
                                product.setId(entity.getId());
                                product.setName(entity.getName());
                                product.setStock(entity.getStock());
                                return product;
                            })
                            .collectList()
                            .map(products -> products.stream()
                                    .max(Comparator.comparing(Product::getStock))
                                    .map(product -> new ProductWithBranch(product, branch))
                                    .orElse(null))
                            .filter(Objects::nonNull);
                })
                .doOnError(error -> log.error("Error finding max stock products with circuit breaker: {}", error.getMessage()));
    }

    @Override
    public Mono<String> getNextId() {
        log.info("Getting next product ID - Thread: {}", Thread.currentThread().getName());
        String sequenceName = "product_sequence";
        Query query = new Query(Criteria.where("id").is(sequenceName));
        Update update = new Update().inc("sequence", 1);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true).upsert(true);
        
        return mongoTemplate.findAndModify(query, update, options, SequenceEntity.class)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .doOnNext(seq -> log.info("Sequence found, current value: {}", seq.getSequence()))
                .switchIfEmpty(
                    Mono.defer(() -> {
                        log.info("Sequence not found, creating initial sequence");
                        SequenceEntity initial = new SequenceEntity(sequenceName, 0L);
                        return mongoTemplate.save(initial)
                                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                                .doOnNext(saved -> log.info("Initial sequence created with ID: {}", saved.getId()))
                                .then(mongoTemplate.findAndModify(query, update, options, SequenceEntity.class)
                                        .transformDeferred(CircuitBreakerOperator.of(circuitBreaker)));
                    })
                )
                .map(sequence -> {
                    String nextId = String.valueOf(sequence.getSequence());
                    log.info("✓ Generated next product ID: {} - Thread: {}", nextId, Thread.currentThread().getName());
                    return nextId;
                })
                .doOnError(error -> log.error("✗ Error generating next product ID: {} - Thread: {}", error.getMessage(), Thread.currentThread().getName(), error));
    }
}
