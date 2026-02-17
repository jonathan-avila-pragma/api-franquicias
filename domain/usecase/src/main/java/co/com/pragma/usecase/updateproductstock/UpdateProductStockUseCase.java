package co.com.pragma.usecase.updateproductstock;

import co.com.pragma.model.Constants;
import co.com.pragma.model.Product;
import co.com.pragma.model.gateways.ProductGateway;
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
