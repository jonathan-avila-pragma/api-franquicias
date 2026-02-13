package co.com.bancolombia.usecase.deleteproduct;

import co.com.bancolombia.model.Constants;
import co.com.bancolombia.model.gateways.ProductGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DeleteProductUseCase {
    private final ProductGateway productGateway;

    public Mono<Void> execute(String franchiseId, String branchId, String productId) {
        return productGateway.findById(franchiseId, branchId, productId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constants.ERROR_PRODUCT_NOT_FOUND)))
                .flatMap(product -> productGateway.deleteById(franchiseId, branchId, productId));
    }
}
