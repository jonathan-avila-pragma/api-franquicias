package co.com.bancolombia.dynamodb;

import co.com.bancolombia.dynamodb.helper.TemplateAdapterOperations;
import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Constants;
import co.com.bancolombia.model.gateways.BranchGateway;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import reactor.core.publisher.Flux;

@Repository
public class BranchRepository extends TemplateAdapterOperations<Branch, String, BranchEntity> implements BranchGateway {
    
    private final DynamoDbAsyncTable<BranchEntity> table;

    public BranchRepository(DynamoDbEnhancedAsyncClient connectionFactory, ObjectMapper mapper) {
        super(connectionFactory, mapper, d -> {
            Branch branch = new Branch();
            branch.setId(d.getId());
            branch.setName(d.getName());
            return branch;
        }, Constants.BRANCH_TABLE_NAME);
        this.table = connectionFactory.table(Constants.BRANCH_TABLE_NAME, TableSchema.fromBean(BranchEntity.class));
    }

    @Override
    public Mono<Branch> save(String franchiseId, Branch branch) {
        BranchEntity entity = new BranchEntity(franchiseId, branch.getId(), branch.getName());
        return Mono.fromFuture(table.putItem(entity))
                .thenReturn(branch);
    }

    @Override
    public Mono<Branch> findById(String franchiseId, String branchId) {
        Key key = Key.builder()
                .partitionValue(AttributeValue.builder().s(franchiseId).build())
                .sortValue(AttributeValue.builder().s(branchId).build())
                .build();
        return Mono.fromFuture(table.getItem(key))
                .map(entity -> {
                    if (entity == null) return null;
                    Branch branch = new Branch();
                    branch.setId(entity.getId());
                    branch.setName(entity.getName());
                    return branch;
                });
    }

    @Override
    public Mono<Void> deleteById(String franchiseId, String branchId) {
        return findById(franchiseId, branchId)
                .flatMap(branch -> {
                    BranchEntity entity = new BranchEntity(franchiseId, branchId, branch.getName());
                    return Mono.fromFuture(table.deleteItem(entity));
                })
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
        Key partitionKey = Key.builder()
                .partitionValue(AttributeValue.builder().s(franchiseId).build())
                .build();
        
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(partitionKey))
                .build();
        
        return Flux.from(table.query(queryRequest))
                .flatMap(page -> Flux.fromIterable(page.items()))
                .map(entity -> {
                    Branch branch = new Branch();
                    branch.setId(entity.getId());
                    branch.setName(entity.getName());
                    return branch;
                });
    }
}
