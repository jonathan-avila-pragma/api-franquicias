package co.com.pragma.usecase.getmaxstockproducts;

import co.com.pragma.model.ProductWithBranch;
import co.com.pragma.model.gateways.ProductGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class GetMaxStockProductsUseCase {
    private final ProductGateway productGateway;

    public Flux<ProductWithBranch> execute(String franchiseId) {
        return productGateway.findMaxStockProductsByFranchise(franchiseId);
    }
}
