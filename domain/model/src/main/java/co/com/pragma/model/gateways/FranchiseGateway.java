package co.com.pragma.model.gateways;

import co.com.pragma.model.Franchise;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface FranchiseGateway {
    Mono<Franchise> save(Franchise franchise);
    Mono<Franchise> findById(String id);
    Mono<Void> deleteById(String id);
    Mono<Franchise> update(Franchise franchise);
    Flux<Franchise> findAll();
    Mono<String> getNextId();
}
