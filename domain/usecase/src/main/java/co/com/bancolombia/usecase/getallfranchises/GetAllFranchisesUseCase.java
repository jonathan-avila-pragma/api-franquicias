package co.com.bancolombia.usecase.getallfranchises;

import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.model.gateways.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class GetAllFranchisesUseCase {
    private final FranchiseGateway franchiseGateway;

    public Flux<Franchise> execute() {
        return franchiseGateway.findAll();
    }
}
