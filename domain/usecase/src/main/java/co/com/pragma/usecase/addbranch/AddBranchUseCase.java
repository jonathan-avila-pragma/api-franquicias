package co.com.pragma.usecase.addbranch;

import co.com.pragma.model.Branch;
import co.com.pragma.model.Constants;
import co.com.pragma.model.Franchise;
import co.com.pragma.model.gateways.BranchGateway;
import co.com.pragma.model.gateways.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class AddBranchUseCase {
    private final BranchGateway branchGateway;
    private final FranchiseGateway franchiseGateway;

    public Mono<Branch> execute(String franchiseId, Branch branch) {
        if (branch.getName() == null || branch.getName().trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException(Constants.ERROR_INVALID_NAME));
        }
        
        return franchiseGateway.findById(franchiseId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constants.ERROR_FRANCHISE_NOT_FOUND)))
                .flatMap(franchise -> {
                    if (branch.getId() == null || branch.getId().trim().isEmpty()) {
                        branch.setId(UUID.randomUUID().toString());
                    }
                    return branchGateway.save(franchiseId, branch);
                });
    }
}
