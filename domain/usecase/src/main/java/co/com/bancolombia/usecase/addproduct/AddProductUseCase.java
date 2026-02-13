package co.com.bancolombia.usecase.addproduct;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Constants;
import co.com.bancolombia.model.Product;
import co.com.bancolombia.model.gateways.BranchGateway;
import co.com.bancolombia.model.gateways.FranchiseGateway;
import co.com.bancolombia.model.gateways.ProductGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

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
                .flatMap(branch -> {
                    if (product.getId() == null || product.getId().trim().isEmpty()) {
                        product.setId(UUID.randomUUID().toString());
                    }
                    return productGateway.save(franchiseId, branchId, product);
                });
    }
}
