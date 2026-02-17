package co.com.pragma.model.gateways;

import co.com.pragma.model.Product;
import co.com.pragma.model.ProductWithBranch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductGateway {
    Mono<Product> save(String franchiseId, String branchId, Product product);
    Mono<Product> findById(String franchiseId, String branchId, String productId);
    Mono<Product> findByName(String franchiseId, String branchId, String productName);
    Mono<Void> deleteById(String franchiseId, String branchId, String productId);
    Mono<Product> update(String franchiseId, String branchId, Product product);
    Flux<ProductWithBranch> findMaxStockProductsByFranchise(String franchiseId);
    Mono<String> getNextId();
}
