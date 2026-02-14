package co.com.bancolombia.mongodb;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Constants;
import co.com.bancolombia.model.gateways.BranchGateway;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class BranchRepository implements BranchGateway {

    private final ReactiveMongoTemplate mongoTemplate;

    public BranchRepository(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Mono<Branch> save(String franchiseId, Branch branch) {
        BranchEntity entity = new BranchEntity(franchiseId, branch.getId(), branch.getName());
        return mongoTemplate.save(entity)
                .map(e -> {
                    Branch b = new Branch();
                    b.setId(e.getId());
                    b.setName(e.getName());
                    return b;
                });
    }

    @Override
    public Mono<Branch> findById(String franchiseId, String branchId) {
        Query query = new Query(Criteria.where("franchiseId").is(franchiseId).and("id").is(branchId));
        return mongoTemplate.findOne(query, BranchEntity.class)
                .map(entity -> {
                    Branch branch = new Branch();
                    branch.setId(entity.getId());
                    branch.setName(entity.getName());
                    return branch;
                });
    }

    @Override
    public Mono<Void> deleteById(String franchiseId, String branchId) {
        Query query = new Query(Criteria.where("franchiseId").is(franchiseId).and("id").is(branchId));
        return mongoTemplate.remove(query, BranchEntity.class)
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
        Query query = new Query(Criteria.where("franchiseId").is(franchiseId));
        return mongoTemplate.find(query, BranchEntity.class)
                .map(entity -> {
                    Branch branch = new Branch();
                    branch.setId(entity.getId());
                    branch.setName(entity.getName());
                    return branch;
                });
    }
}
