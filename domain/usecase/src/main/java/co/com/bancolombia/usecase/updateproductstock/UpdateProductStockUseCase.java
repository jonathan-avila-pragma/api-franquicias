package co.com.bancolombia.usecase.updateproductstock;

import co.com.bancolombia.model.Constants;
import co.com.bancolombia.model.Product;
import co.com.bancolombia.model.gateways.ProductGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateProductStockUseCase {
    private final ProductGateway productGateway;

    public Mono<Product> execute(String franchiseId, String branchId, String productId, Integer stock) {
        if (stock == null || stock < 0) {
            return Mono.error(new IllegalArgumentException(Constants.ERROR_INVALID_STOCK));
        }
        
        return productGateway.findById(franchiseId, branchId, productId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constants.ERROR_PRODUCT_NOT_FOUND)))
                .flatMap(product -> {
                    product.setStock(stock);
                    return productGateway.update(franchiseId, branchId, product);
                });
    }
}
