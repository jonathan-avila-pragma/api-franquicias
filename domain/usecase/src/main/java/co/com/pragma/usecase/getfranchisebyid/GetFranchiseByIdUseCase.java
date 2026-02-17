package co.com.pragma.usecase.getfranchisebyid;

import co.com.pragma.model.Constants;
import co.com.pragma.model.Franchise;
import co.com.pragma.model.gateways.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetFranchiseByIdUseCase {
    private final FranchiseGateway franchiseGateway;

    public Mono<Franchise> execute(String franchiseId) {
        if (franchiseId == null || franchiseId.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException(Constants.ERROR_FRANCHISE_NOT_FOUND));
        }
        
        return franchiseGateway.findById(franchiseId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constants.ERROR_FRANCHISE_NOT_FOUND)));
    }
}
