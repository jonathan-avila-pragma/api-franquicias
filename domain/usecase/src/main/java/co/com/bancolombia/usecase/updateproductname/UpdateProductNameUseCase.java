package co.com.bancolombia.usecase.updateproductname;

import co.com.bancolombia.model.Constants;
import co.com.bancolombia.model.Product;
import co.com.bancolombia.model.gateways.BranchGateway;
import co.com.bancolombia.model.gateways.FranchiseGateway;
import co.com.bancolombia.model.gateways.ProductGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateProductNameUseCase {
    private final ProductGateway productGateway;
    private final BranchGateway branchGateway;
    private final FranchiseGateway franchiseGateway;

    public Mono<Product> execute(String franchiseId, String branchId, String productId, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException(Constants.ERROR_INVALID_NAME));
        }
        
        return franchiseGateway.findById(franchiseId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constants.ERROR_FRANCHISE_NOT_FOUND)))
                .flatMap(franchise -> branchGateway.findById(franchiseId, branchId))
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constants.ERROR_BRANCH_NOT_FOUND)))
                .flatMap(branch -> productGateway.findById(franchiseId, branchId, productId))
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constants.ERROR_PRODUCT_NOT_FOUND)))
                .flatMap(product -> {
                    product.setName(newName);
                    return productGateway.update(franchiseId, branchId, product);
                });
    }
}
