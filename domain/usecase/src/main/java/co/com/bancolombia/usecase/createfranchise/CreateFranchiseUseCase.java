package co.com.bancolombia.usecase.createfranchise;

import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.model.Constants;
import co.com.bancolombia.model.gateways.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateFranchiseUseCase {
    private final FranchiseGateway franchiseGateway;

    public Mono<Franchise> execute(Franchise franchise) {
        if (franchise.getName() == null || franchise.getName().trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException(Constants.ERROR_INVALID_NAME));
        }
        
        if (franchise.getId() == null || franchise.getId().trim().isEmpty()) {
            return franchiseGateway.getNextId()
                    .flatMap(nextId -> {
                        franchise.setId(nextId);
                        return franchiseGateway.save(franchise);
                    });
        }
        
        return franchiseGateway.save(franchise);
    }
}
