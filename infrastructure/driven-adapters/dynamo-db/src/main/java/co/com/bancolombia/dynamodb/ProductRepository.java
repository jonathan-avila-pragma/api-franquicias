package co.com.bancolombia.dynamodb;

import co.com.bancolombia.dynamodb.helper.TemplateAdapterOperations;
import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Constants;
import co.com.bancolombia.model.Product;
import co.com.bancolombia.model.ProductWithBranch;
import co.com.bancolombia.model.gateways.ProductGateway;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Comparator;

@Repository
public class ProductRepository extends TemplateAdapterOperations<Product, String, ProductEntity> implements ProductGateway {
    
    private final DynamoDbAsyncTable<ProductEntity> table;
    private final co.com.bancolombia.model.gateways.BranchGateway branchGateway;

    public ProductRepository(DynamoDbEnhancedAsyncClient connectionFactory, ObjectMapper mapper, co.com.bancolombia.model.gateways.BranchGateway branchGateway) {
        super(connectionFactory, mapper, d -> {
            Product product = new Product();
            product.setId(d.getId());
            product.setName(d.getName());
            product.setStock(d.getStock());
            return product;
        }, Constants.PRODUCT_TABLE_NAME);
        this.table = connectionFactory.table(Constants.PRODUCT_TABLE_NAME, TableSchema.fromBean(ProductEntity.class));
        this.branchGateway = branchGateway;
    }

    @Override
    public Mono<Product> save(String franchiseId, String branchId, Product product) {
        String compositeKey = franchiseId + "#" + branchId;
        ProductEntity entity = new ProductEntity(compositeKey, product.getId(), product.getName(), product.getStock());
        return Mono.fromFuture(table.putItem(entity))
                .thenReturn(product);
    }

    @Override
    public Mono<Product> findById(String franchiseId, String branchId, String productId) {
        String compositeKey = franchiseId + "#" + branchId;
        Key key = Key.builder()
                .partitionValue(AttributeValue.builder().s(compositeKey).build())
                .sortValue(AttributeValue.builder().s(productId).build())
                .build();
        return Mono.fromFuture(table.getItem(key))
                .map(entity -> {
                    if (entity == null) return null;
                    Product product = new Product();
                    product.setId(entity.getId());
                    product.setName(entity.getName());
                    product.setStock(entity.getStock());
                    return product;
                });
    }

    @Override
    public Mono<Void> deleteById(String franchiseId, String branchId, String productId) {
        return findById(franchiseId, branchId, productId)
                .flatMap(product -> {
                    String compositeKey = franchiseId + "#" + branchId;
                    ProductEntity entity = new ProductEntity(compositeKey, productId, product.getName(), product.getStock());
                    return Mono.fromFuture(table.deleteItem(entity));
                })
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
                    String compositeKey = franchiseId + "#" + branch.getId();
                    QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                            .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                                    .partitionValue(AttributeValue.builder().s(compositeKey).build())
                                    .build()))
                            .build();
                    
                    return Mono.from(table.query(queryRequest))
                            .collectList()
                            .map(pages -> pages.stream()
                                    .flatMap(page -> page.items().stream())
                                    .map(entity -> {
                                        Product product = new Product();
                                        product.setId(entity.getId());
                                        product.setName(entity.getName());
                                        product.setStock(entity.getStock());
                                        return product;
                                    })
                                    .max(Comparator.comparing(Product::getStock))
                                    .map(product -> new ProductWithBranch(product, branch))
                                    .orElse(null))
                            .filter(productWithBranch -> productWithBranch != null);
                });
    }
}
