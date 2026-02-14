package co.com.pragma.usecase.getallfranchises;

import co.com.pragma.model.Franchise;
import co.com.pragma.model.gateways.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class GetAllFranchisesUseCase {
    private final FranchiseGateway franchiseGateway;

    public Flux<Franchise> execute() {
        return franchiseGateway.findAll();
    }
}
