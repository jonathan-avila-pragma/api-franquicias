package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.*;
import co.com.bancolombia.model.Constants;
import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Product;
import co.com.bancolombia.model.ProductWithBranch;
import co.com.bancolombia.usecase.createfranchise.CreateFranchiseUseCase;
import co.com.bancolombia.usecase.addbranch.AddBranchUseCase;
import co.com.bancolombia.usecase.addproduct.AddProductUseCase;
import co.com.bancolombia.usecase.deleteproduct.DeleteProductUseCase;
import co.com.bancolombia.usecase.updateproductstock.UpdateProductStockUseCase;
import co.com.bancolombia.usecase.getmaxstockproducts.GetMaxStockProductsUseCase;
import co.com.bancolombia.usecase.updatefranchisename.UpdateFranchiseNameUseCase;
import co.com.bancolombia.usecase.updatebranchname.UpdateBranchNameUseCase;
import co.com.bancolombia.usecase.updateproductname.UpdateProductNameUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class Handler {
    
    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final AddBranchUseCase addBranchUseCase;
    private final AddProductUseCase addProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final GetMaxStockProductsUseCase getMaxStockProductsUseCase;
    private final UpdateFranchiseNameUseCase updateFranchiseNameUseCase;
    private final UpdateBranchNameUseCase updateBranchNameUseCase;
    private final UpdateProductNameUseCase updateProductNameUseCase;
    private final ObjectMapper objectMapper;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(FranchiseRequest.class)
                .flatMap(franchiseRequest -> {
                    Franchise franchise = new Franchise();
                    franchise.setName(franchiseRequest.getName());
                    return createFranchiseUseCase.execute(franchise);
                })
                .flatMap(franchise -> ServerResponse.ok().bodyValue(franchise))
                .onErrorResume(IllegalArgumentException.class, e -> 
                    ServerResponse.badRequest().bodyValue(e.getMessage()))
                .onErrorResume(e -> ServerResponse.status(500).bodyValue("Internal server error"));
    }

    public Mono<ServerResponse> addBranch(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        return request.bodyToMono(BranchRequest.class)
                .flatMap(branchRequest -> {
                    Branch branch = new Branch();
                    branch.setName(branchRequest.getName());
                    return addBranchUseCase.execute(franchiseId, branch);
                })
                .flatMap(branch -> ServerResponse.ok().bodyValue(branch))
                .onErrorResume(IllegalArgumentException.class, e -> 
                    ServerResponse.badRequest().bodyValue(e.getMessage()))
                .onErrorResume(e -> ServerResponse.status(500).bodyValue("Internal server error"));
    }

    public Mono<ServerResponse> addProduct(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        return request.bodyToMono(ProductRequest.class)
                .flatMap(productRequest -> {
                    Product product = new Product();
                    product.setName(productRequest.getName());
                    product.setStock(productRequest.getStock());
                    return addProductUseCase.execute(franchiseId, branchId, product);
                })
                .flatMap(product -> ServerResponse.ok().bodyValue(product))
                .onErrorResume(IllegalArgumentException.class, e -> 
                    ServerResponse.badRequest().bodyValue(e.getMessage()))
                .onErrorResume(e -> ServerResponse.status(500).bodyValue("Internal server error"));
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        String productId = request.pathVariable("productId");
        return deleteProductUseCase.execute(franchiseId, branchId, productId)
                .then(ServerResponse.ok().build())
                .onErrorResume(IllegalArgumentException.class, e -> 
                    ServerResponse.badRequest().bodyValue(e.getMessage()))
                .onErrorResume(e -> ServerResponse.status(500).bodyValue("Internal server error"));
    }

    public Mono<ServerResponse> updateProductStock(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        String productId = request.pathVariable("productId");
        return request.bodyToMono(UpdateStockRequest.class)
                .flatMap(updateStockRequest -> 
                    updateProductStockUseCase.execute(franchiseId, branchId, productId, updateStockRequest.getStock()))
                .flatMap(product -> ServerResponse.ok().bodyValue(product))
                .onErrorResume(IllegalArgumentException.class, e -> 
                    ServerResponse.badRequest().bodyValue(e.getMessage()))
                .onErrorResume(e -> ServerResponse.status(500).bodyValue("Internal server error"));
    }

    public Mono<ServerResponse> getMaxStockProducts(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        return getMaxStockProductsUseCase.execute(franchiseId)
                .collectList()
                .flatMap(products -> ServerResponse.ok().bodyValue(products))
                .onErrorResume(IllegalArgumentException.class, e -> 
                    ServerResponse.badRequest().bodyValue(e.getMessage()))
                .onErrorResume(e -> ServerResponse.status(500).bodyValue("Internal server error"));
    }

    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(updateNameRequest -> 
                    updateFranchiseNameUseCase.execute(franchiseId, updateNameRequest.getName()))
                .flatMap(franchise -> ServerResponse.ok().bodyValue(franchise))
                .onErrorResume(IllegalArgumentException.class, e -> 
                    ServerResponse.badRequest().bodyValue(e.getMessage()))
                .onErrorResume(e -> ServerResponse.status(500).bodyValue("Internal server error"));
    }

    public Mono<ServerResponse> updateBranchName(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(updateNameRequest -> 
                    updateBranchNameUseCase.execute(franchiseId, branchId, updateNameRequest.getName()))
                .flatMap(branch -> ServerResponse.ok().bodyValue(branch))
                .onErrorResume(IllegalArgumentException.class, e -> 
                    ServerResponse.badRequest().bodyValue(e.getMessage()))
                .onErrorResume(e -> ServerResponse.status(500).bodyValue("Internal server error"));
    }

    public Mono<ServerResponse> updateProductName(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        String productId = request.pathVariable("productId");
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(updateNameRequest -> 
                    updateProductNameUseCase.execute(franchiseId, branchId, productId, updateNameRequest.getName()))
                .flatMap(product -> ServerResponse.ok().bodyValue(product))
                .onErrorResume(IllegalArgumentException.class, e -> 
                    ServerResponse.badRequest().bodyValue(e.getMessage()))
                .onErrorResume(e -> ServerResponse.status(500).bodyValue("Internal server error"));
    }
}
