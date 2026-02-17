package co.com.pragma.usecase.addproduct;

import co.com.pragma.model.Constants;
import co.com.pragma.model.Product;
import co.com.pragma.model.gateways.BranchGateway;
import co.com.pragma.model.gateways.FranchiseGateway;
import co.com.pragma.model.gateways.ProductGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AddProductUseCase {
    private final ProductGateway productGateway;
    private final BranchGateway branchGateway;
    private final FranchiseGateway franchiseGateway;

    public Mono<Product> execute(String franchiseId, String branchId, Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException(Constants.ERROR_INVALID_NAME));
        }
        
        if (product.getStock() == null || product.getStock() < 0) {
            return Mono.error(new IllegalArgumentException(Constants.ERROR_INVALID_STOCK));
        }
        
        return franchiseGateway.findById(franchiseId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constants.ERROR_FRANCHISE_NOT_FOUND)))
                .flatMap(franchise -> branchGateway.findById(franchiseId, branchId))
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constants.ERROR_BRANCH_NOT_FOUND)))
                .flatMap(branch -> productGateway.getNextId())
                .flatMap(nextId -> {
                    product.setId(nextId);
                    return productGateway.save(franchiseId, branchId, product);
                });
    }
}
