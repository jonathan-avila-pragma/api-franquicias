package co.com.pragma.mongodb;

import co.com.pragma.model.Branch;
import co.com.pragma.model.Constants;
import co.com.pragma.model.gateways.BranchGateway;
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

@Slf4j
@Repository
public class BranchRepository implements BranchGateway {

    private static final String FIELD_FRANCHISE_ID = "franchiseId";

    private final ReactiveMongoTemplate mongoTemplate;
    private final CircuitBreaker circuitBreaker;

    public BranchRepository(ReactiveMongoTemplate mongoTemplate, CircuitBreaker mongoCircuitBreaker) {
        this.mongoTemplate = mongoTemplate;
        this.circuitBreaker = mongoCircuitBreaker;
    }

    @Override
    public Mono<Branch> save(String franchiseId, Branch branch) {
        BranchEntity entity = new BranchEntity(branch.getId(), franchiseId, branch.getName(), branch.getAddress(), branch.getCity());
        return mongoTemplate.save(entity)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .map(this::mapToBranch);
    }

    @Override
    public Mono<Branch> findById(String franchiseId, String branchId) {
        Query query = new Query(Criteria.where(FIELD_FRANCHISE_ID).is(franchiseId).and("id").is(branchId));
        return mongoTemplate.findOne(query, BranchEntity.class)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .map(this::mapToBranch);
    }

    @Override
    public Mono<Void> deleteById(String franchiseId, String branchId) {
        Query query = new Query(Criteria.where(FIELD_FRANCHISE_ID).is(franchiseId).and("id").is(branchId));
        return mongoTemplate.remove(query, BranchEntity.class)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .then();
    }

    @Override
    public Mono<Branch> update(String franchiseId, Branch branch) {
        return findById(franchiseId, branch.getId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constants.ERROR_BRANCH_NOT_FOUND)))
                .flatMap(existing -> save(franchiseId, branch));
    }

    @Override
    public Flux<Branch> findAllByFranchiseId(String franchiseId) {
        Query query = new Query(Criteria.where(FIELD_FRANCHISE_ID).is(franchiseId));
        return mongoTemplate.find(query, BranchEntity.class)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .map(this::mapToBranch);
    }

    @Override
    public Mono<String> getNextId() {
        log.info("Getting next branch ID - Thread: {}", Thread.currentThread().getName());
        String sequenceName = "branch_sequence";
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
                    log.info("✓ Generated next branch ID: {} - Thread: {}", nextId, Thread.currentThread().getName());
                    return nextId;
                })
                .doOnError(error -> log.error("✗ Error generating next branch ID: {} - Thread: {}", error.getMessage(), Thread.currentThread().getName(), error));
    }

    private Branch mapToBranch(BranchEntity entity) {
        Branch branch = Branch.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(entity.getAddress())
                .city(entity.getCity())
                .build();
        initializeProductsIfNull(branch);
        return branch;
    }

    private void initializeProductsIfNull(Branch branch) {
        if (branch.getProducts() == null) {
            branch.setProducts(new java.util.ArrayList<>());
        }
    }
}
