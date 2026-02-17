package co.com.pragma.usecase.getbranchbyid;

import co.com.pragma.model.Branch;
import co.com.pragma.model.Constants;
import co.com.pragma.model.gateways.BranchGateway;
import co.com.pragma.model.gateways.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetBranchByIdUseCase {
    private final BranchGateway branchGateway;
    private final FranchiseGateway franchiseGateway;

    public Mono<Branch> execute(String franchiseId, String branchId) {
        if (franchiseId == null || franchiseId.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException(Constants.ERROR_FRANCHISE_NOT_FOUND));
        }
        
        if (branchId == null || branchId.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException(Constants.ERROR_BRANCH_NOT_FOUND));
        }
        
        return franchiseGateway.findById(franchiseId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constants.ERROR_FRANCHISE_NOT_FOUND)))
                .flatMap(franchise -> branchGateway.findById(franchiseId, branchId))
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constants.ERROR_BRANCH_NOT_FOUND)));
    }
}
