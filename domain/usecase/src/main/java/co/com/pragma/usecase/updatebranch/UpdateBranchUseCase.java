package co.com.pragma.usecase.updatebranch;

import co.com.pragma.model.Branch;
import co.com.pragma.model.Constants;
import co.com.pragma.model.gateways.BranchGateway;
import co.com.pragma.model.gateways.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateBranchUseCase {
    private final BranchGateway branchGateway;
    private final FranchiseGateway franchiseGateway;

    public Mono<Branch> execute(String franchiseId, String branchId, Branch branchUpdate) {
        return franchiseGateway.findById(franchiseId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constants.ERROR_FRANCHISE_NOT_FOUND)))
                .flatMap(franchise -> branchGateway.findById(franchiseId, branchId))
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constants.ERROR_BRANCH_NOT_FOUND)))
                .flatMap(existingBranch -> {
                    // Actualizar solo los campos que vienen en branchUpdate (no null)
                    if (branchUpdate.getName() != null && !branchUpdate.getName().trim().isEmpty()) {
                        existingBranch.setName(branchUpdate.getName().trim());
                    }
                    if (branchUpdate.getAddress() != null) {
                        existingBranch.setAddress(branchUpdate.getAddress().trim());
                    }
                    if (branchUpdate.getCity() != null) {
                        existingBranch.setCity(branchUpdate.getCity().trim());
                    }
                    return branchGateway.update(franchiseId, existingBranch);
                });
    }
}
