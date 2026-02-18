package co.com.pragma.usecase.getproductsbybranch;

import co.com.pragma.model.Product;
import co.com.pragma.model.gateways.BranchGateway;
import co.com.pragma.model.gateways.ProductGateway;
import co.com.pragma.model.Constants;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetProductsByBranchUseCase {
    private final ProductGateway productGateway;
    private final BranchGateway branchGateway;

    public Flux<Product> execute(String franchiseId, String branchId) {
        return branchGateway.findById(franchiseId, branchId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constants.ERROR_BRANCH_NOT_FOUND)))
                .flatMapMany(branch -> productGateway.findAllByBranch(franchiseId, branchId));
    }
}
