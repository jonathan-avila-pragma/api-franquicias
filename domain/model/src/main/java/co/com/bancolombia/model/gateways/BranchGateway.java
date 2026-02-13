package co.com.bancolombia.model.gateways;

import co.com.bancolombia.model.Branch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchGateway {
    Mono<Branch> save(String franchiseId, Branch branch);
    Mono<Branch> findById(String franchiseId, String branchId);
    Mono<Void> deleteById(String franchiseId, String branchId);
    Mono<Branch> update(String franchiseId, Branch branch);
    Flux<Branch> findAllByFranchiseId(String franchiseId);
}
