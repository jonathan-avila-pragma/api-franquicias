package co.com.pragma.mongodb;

import co.com.pragma.model.Constants;
import co.com.pragma.model.Product;
import co.com.pragma.model.ProductWithBranch;
import co.com.pragma.model.gateways.BranchGateway;
import co.com.pragma.model.gateways.ProductGateway;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.Objects;

@Repository
public class ProductRepository implements ProductGateway {

    private final ReactiveMongoTemplate mongoTemplate;
    private final BranchGateway branchGateway;

    public ProductRepository(ReactiveMongoTemplate mongoTemplate, BranchGateway branchGateway) {
        this.mongoTemplate = mongoTemplate;
        this.branchGateway = branchGateway;
    }

    @Override
    public Mono<Product> save(String franchiseId, String branchId, Product product) {
        ProductEntity entity = new ProductEntity(franchiseId, branchId, product.getId(), product.getName(), product.getStock());
        return mongoTemplate.save(entity)
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
                });
    }
}
