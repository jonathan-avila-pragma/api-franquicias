package co.com.bancolombia.usecase.getmaxstockproducts;

import co.com.bancolombia.model.ProductWithBranch;
import co.com.bancolombia.model.gateways.ProductGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class GetMaxStockProductsUseCase {
    private final ProductGateway productGateway;

    public Flux<ProductWithBranch> execute(String franchiseId) {
        return productGateway.findMaxStockProductsByFranchise(franchiseId);
    }
}
