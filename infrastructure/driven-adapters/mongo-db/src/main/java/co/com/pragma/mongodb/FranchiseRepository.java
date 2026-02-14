package co.com.pragma.mongodb;

import co.com.pragma.model.Constants;
import co.com.pragma.model.Franchise;
import co.com.pragma.model.gateways.FranchiseGateway;
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
public class FranchiseRepository implements FranchiseGateway {

    private final ReactiveMongoTemplate mongoTemplate;

    public FranchiseRepository(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        FranchiseEntity entity = new FranchiseEntity(franchise.getId(), franchise.getName());
        return mongoTemplate.save(entity)
                .map(e -> {
                    Franchise f = new Franchise();
                    f.setId(e.getId());
                    f.setName(e.getName());
                    return f;
                });
    }

    @Override
    public Mono<Franchise> findById(String id) {
        return mongoTemplate.findById(id, FranchiseEntity.class)
                .map(entity -> {
                    Franchise franchise = new Franchise();
                    franchise.setId(entity.getId());
                    franchise.setName(entity.getName());
                    return franchise;
                });
    }

    @Override
    public Mono<Void> deleteById(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        return mongoTemplate.remove(query, FranchiseEntity.class)
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
                .map(entity -> {
                    Franchise franchise = new Franchise();
                    franchise.setId(entity.getId());
                    franchise.setName(entity.getName());
                    return franchise;
                });
    }

    @Override
    public Mono<String> getNextId() {
        log.info("Getting next franchise ID - Thread: {}", Thread.currentThread().getName());
        String sequenceName = "franchise_sequence";
        Query query = new Query(Criteria.where("id").is(sequenceName));
        Update update = new Update().inc("sequence", 1);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true).upsert(true);
        
        return mongoTemplate.findAndModify(query, update, options, SequenceEntity.class)
                .doOnNext(seq -> log.info("Sequence found, current value: {}", seq.getSequence()))
                .switchIfEmpty(
                    Mono.defer(() -> {
                        log.info("Sequence not found, creating initial sequence");
                        SequenceEntity initial = new SequenceEntity(sequenceName, 0L);
                        return mongoTemplate.save(initial)
                                .doOnNext(saved -> log.info("Initial sequence created with ID: {}", saved.getId()))
                                .then(mongoTemplate.findAndModify(query, update, options, SequenceEntity.class));
                    })
                )
                .map(sequence -> {
                    String nextId = String.valueOf(sequence.getSequence());
                    log.info("✓ Generated next franchise ID: {} - Thread: {}", nextId, Thread.currentThread().getName());
                    return nextId;
                })
                .doOnError(error -> log.error("✗ Error generating next ID: {} - Thread: {}", error.getMessage(), Thread.currentThread().getName(), error));
    }
}
