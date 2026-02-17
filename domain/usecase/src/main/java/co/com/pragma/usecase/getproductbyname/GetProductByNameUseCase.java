package co.com.pragma.usecase.getproductbyname;

import co.com.pragma.model.Constants;
import co.com.pragma.model.Product;
import co.com.pragma.model.gateways.BranchGateway;
import co.com.pragma.model.gateways.FranchiseGateway;
import co.com.pragma.model.gateways.ProductGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetProductByNameUseCase {
    private final ProductGateway productGateway;
    private final BranchGateway branchGateway;
    private final FranchiseGateway franchiseGateway;

    public Mono<Product> execute(String franchiseId, String branchId, String productName) {
        if (franchiseId == null || franchiseId.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException(Constants.ERROR_FRANCHISE_NOT_FOUND));
        }
        
        if (branchId == null || branchId.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException(Constants.ERROR_BRANCH_NOT_FOUND));
        }
        
        if (productName == null || productName.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException(Constants.ERROR_INVALID_NAME));
        }
        
        return franchiseGateway.findById(franchiseId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constants.ERROR_FRANCHISE_NOT_FOUND)))
                .flatMap(franchise -> branchGateway.findById(franchiseId, branchId))
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constants.ERROR_BRANCH_NOT_FOUND)))
                .flatMap(branch -> productGateway.findByName(franchiseId, branchId, productName))
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constants.ERROR_PRODUCT_NOT_FOUND)));
    }
}
