package co.com.pragma.mongodb;

import co.com.pragma.model.Constants;
import co.com.pragma.model.Franchise;
import co.com.pragma.model.gateways.FranchiseGateway;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class FranchiseRepository implements FranchiseGateway {

    private final ReactiveMongoTemplate mongoTemplate;
    private final CircuitBreaker circuitBreaker;

    public FranchiseRepository(ReactiveMongoTemplate mongoTemplate, CircuitBreaker mongoCircuitBreaker) {
        this.mongoTemplate = mongoTemplate;
        this.circuitBreaker = mongoCircuitBreaker;
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        FranchiseEntity entity = new FranchiseEntity(franchise.getId(), franchise.getName(), franchise.getDescription());
        return mongoTemplate.save(entity)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .map(e -> Franchise.builder()
                        .id(e.getId())
                        .name(e.getName())
                        .description(e.getDescription())
                        .build());
    }

    @Override
    public Mono<Franchise> findById(String id) {
        return mongoTemplate.findById(id, FranchiseEntity.class)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .map(entity -> Franchise.builder()
                        .id(entity.getId())
                        .name(entity.getName())
                        .description(entity.getDescription())
                        .build());
    }

    @Override
    public Mono<Void> deleteById(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        return mongoTemplate.remove(query, FranchiseEntity.class)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .then();
    }

    @Override
    public Mono<Franchise> update(Franchise franchise) {
        return findById(franchise.getId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constants.ERROR_FRANCHISE_NOT_FOUND)))
                .flatMap(existing -> save(franchise));
    }

    @Override
    public Flux<Franchise> findAll() {
        return mongoTemplate.findAll(FranchiseEntity.class)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .map(entity -> Franchise.builder()
                        .id(entity.getId())
                        .name(entity.getName())
                        .description(entity.getDescription())
                        .build());
    }

    @Override
    public Mono<String> getNextId() {
        String sequenceName = "franchise_sequence";
        Query query = new Query(Criteria.where("id").is(sequenceName));
        Update update = new Update().inc("sequence", 1);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true).upsert(true);
        
        return mongoTemplate.findAndModify(query, update, options, SequenceEntity.class)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .switchIfEmpty(
                    Mono.defer(() -> {
                        SequenceEntity initial = new SequenceEntity(sequenceName, 0L);
                        return mongoTemplate.save(initial)
                                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                                .then(mongoTemplate.findAndModify(query, update, options, SequenceEntity.class)
                                        .transformDeferred(CircuitBreakerOperator.of(circuitBreaker)));
                    })
                )
                .map(sequence -> String.valueOf(sequence.getSequence()));
    }
}
