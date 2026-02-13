package co.com.bancolombia.model.gateways;

import co.com.bancolombia.model.Franchise;
import reactor.core.publisher.Mono;

import java.util.List;

public interface FranchiseGateway {
    Mono<Franchise> save(Franchise franchise);
    Mono<Franchise> findById(String id);
    Mono<Void> deleteById(String id);
    Mono<Franchise> update(Franchise franchise);
}
