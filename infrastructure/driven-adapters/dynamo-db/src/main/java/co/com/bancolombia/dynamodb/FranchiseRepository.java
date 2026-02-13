package co.com.bancolombia.dynamodb;

import co.com.bancolombia.dynamodb.helper.TemplateAdapterOperations;
import co.com.bancolombia.model.Constants;
import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.model.gateways.FranchiseGateway;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Repository
public class FranchiseRepository extends TemplateAdapterOperations<Franchise, String, FranchiseEntity> implements FranchiseGateway {

    public FranchiseRepository(DynamoDbEnhancedAsyncClient connectionFactory, ObjectMapper mapper) {
        super(connectionFactory, mapper, d -> mapper.map(d, Franchise.class), Constants.FRANCHISE_TABLE_NAME);
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return super.save(franchise);
    }

    @Override
    public Mono<Franchise> findById(String id) {
        return super.getById(id);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return findById(id)
                .flatMap(franchise -> super.delete(franchise))
                .then();
    }

    @Override
    public Mono<Franchise> update(Franchise franchise) {
        return findById(franchise.getId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constants.ERROR_FRANCHISE_NOT_FOUND)))
                .flatMap(existing -> super.save(franchise));
    }
}
