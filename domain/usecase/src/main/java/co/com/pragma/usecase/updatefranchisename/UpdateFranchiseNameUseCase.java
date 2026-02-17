package co.com.pragma.usecase.updatefranchisename;

import co.com.pragma.model.Constants;
import co.com.pragma.model.Franchise;
import co.com.pragma.model.gateways.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateFranchiseNameUseCase {
    private final FranchiseGateway franchiseGateway;

    public Mono<Franchise> execute(String franchiseId, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException(Constants.ERROR_INVALID_NAME));
        }
        
        return franchiseGateway.findById(franchiseId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constants.ERROR_FRANCHISE_NOT_FOUND)))
                .flatMap(franchise -> {
                    franchise.setName(newName);
                    return franchiseGateway.update(franchise);
                });
    }
}
