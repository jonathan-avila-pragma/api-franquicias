package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.*;
import co.com.bancolombia.api.helper.BusinessCode;
import co.com.bancolombia.api.helper.ResponseUtil;
import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Product;
import co.com.bancolombia.usecase.createfranchise.CreateFranchiseUseCase;
import co.com.bancolombia.usecase.addbranch.AddBranchUseCase;
import co.com.bancolombia.usecase.addproduct.AddProductUseCase;
import co.com.bancolombia.usecase.deleteproduct.DeleteProductUseCase;
import co.com.bancolombia.usecase.updateproductstock.UpdateProductStockUseCase;
import co.com.bancolombia.usecase.getmaxstockproducts.GetMaxStockProductsUseCase;
import co.com.bancolombia.usecase.getallfranchises.GetAllFranchisesUseCase;
import co.com.bancolombia.usecase.updatefranchisename.UpdateFranchiseNameUseCase;
import co.com.bancolombia.usecase.updatebranchname.UpdateBranchNameUseCase;
import co.com.bancolombia.usecase.updateproductname.UpdateProductNameUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {
    
    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final AddBranchUseCase addBranchUseCase;
    private final AddProductUseCase addProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final GetMaxStockProductsUseCase getMaxStockProductsUseCase;
    private final GetAllFranchisesUseCase getAllFranchisesUseCase;
    private final UpdateFranchiseNameUseCase updateFranchiseNameUseCase;
    private final UpdateBranchNameUseCase updateBranchNameUseCase;
    private final UpdateProductNameUseCase updateProductNameUseCase;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        log.info("Received POST request to create franchise");
        return request.bodyToMono(FranchiseRequest.class)
                .doOnNext(franchiseRequest -> log.info("Creating franchise with name: {}", franchiseRequest.getName()))
                .flatMap(franchiseRequest -> {
                    Franchise franchise = new Franchise();
                    franchise.setName(franchiseRequest.getName());
                    return createFranchiseUseCase.execute(franchise)
                            .doOnNext(f -> log.info("Franchise created successfully with ID: {}", f.getId()))
                            .doOnError(e -> log.error("Error creating franchise: {}", e.getMessage()));
                })
                .map(franchise -> ResponseUtil.responseSuccessful(franchise, BusinessCode.S201000))
                .flatMap(response -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .onErrorResume(IllegalArgumentException.class, e -> {
                    log.error("Validation error creating franchise: {}", e.getMessage());
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.B400000, e.getMessage()));
                })
                .onErrorResume(e -> {
                    log.error("Internal error creating franchise", e);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.E500000));
                });
    }

    public Mono<ServerResponse> addBranch(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        return request.bodyToMono(BranchRequest.class)
                .flatMap(branchRequest -> {
                    Branch branch = new Branch();
                    branch.setName(branchRequest.getName());
                    return addBranchUseCase.execute(franchiseId, branch);
                })
                .map(branch -> ResponseUtil.responseSuccessful(branch, BusinessCode.S201000))
                .flatMap(response -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .onErrorResume(IllegalArgumentException.class, e -> 
                    ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.B400000, e.getMessage())))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ResponseUtil.responseError(BusinessCode.E500000)));
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
                .map(product -> ResponseUtil.responseSuccessful(product, BusinessCode.S201000))
                .flatMap(response -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .onErrorResume(IllegalArgumentException.class, e -> 
                    ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.B400000, e.getMessage())))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ResponseUtil.responseError(BusinessCode.E500000)));
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        String productId = request.pathVariable("productId");
        return deleteProductUseCase.execute(franchiseId, branchId, productId)
                .then(Mono.just(ResponseUtil.responseSuccessful(null, BusinessCode.S200000)))
                .flatMap(response -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .onErrorResume(IllegalArgumentException.class, e -> 
                    ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.B400000, e.getMessage())))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ResponseUtil.responseError(BusinessCode.E500000)));
    }

    public Mono<ServerResponse> updateProductStock(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        String productId = request.pathVariable("productId");
        return request.bodyToMono(UpdateStockRequest.class)
                .flatMap(updateStockRequest -> 
                    updateProductStockUseCase.execute(franchiseId, branchId, productId, updateStockRequest.getStock()))
                .map(product -> ResponseUtil.responseSuccessful(product, BusinessCode.S200000))
                .flatMap(response -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .onErrorResume(IllegalArgumentException.class, e -> 
                    ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.B400000, e.getMessage())))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ResponseUtil.responseError(BusinessCode.E500000)));
    }

    public Mono<ServerResponse> getMaxStockProducts(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        return getMaxStockProductsUseCase.execute(franchiseId)
                .collectList()
                .map(products -> ResponseUtil.responseSuccessful(products, BusinessCode.S200000))
                .flatMap(response -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .onErrorResume(IllegalArgumentException.class, e -> 
                    ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.B400000, e.getMessage())))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ResponseUtil.responseError(BusinessCode.E500000)));
    }

    public Mono<ServerResponse> getAllFranchises(ServerRequest request) {
        log.info("Received GET request to get all franchises");
        return getAllFranchisesUseCase.execute()
                .collectList()
                .doOnNext(franchises -> log.info("Retrieved {} franchises", franchises.size()))
                .map(franchises -> ResponseUtil.responseSuccessful(franchises, BusinessCode.S200000))
                .flatMap(response -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .onErrorResume(e -> {
                    log.error("Error getting all franchises", e);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.E500000));
                });
    }

    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(updateNameRequest -> 
                    updateFranchiseNameUseCase.execute(franchiseId, updateNameRequest.getName()))
                .map(franchise -> ResponseUtil.responseSuccessful(franchise, BusinessCode.S200000))
                .flatMap(response -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .onErrorResume(IllegalArgumentException.class, e -> 
                    ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.B400000, e.getMessage())))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ResponseUtil.responseError(BusinessCode.E500000)));
    }

    public Mono<ServerResponse> updateBranchName(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(updateNameRequest -> 
                    updateBranchNameUseCase.execute(franchiseId, branchId, updateNameRequest.getName()))
                .map(branch -> ResponseUtil.responseSuccessful(branch, BusinessCode.S200000))
                .flatMap(response -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .onErrorResume(IllegalArgumentException.class, e -> 
                    ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.B400000, e.getMessage())))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ResponseUtil.responseError(BusinessCode.E500000)));
    }

    public Mono<ServerResponse> updateProductName(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        String productId = request.pathVariable("productId");
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(updateNameRequest -> 
                    updateProductNameUseCase.execute(franchiseId, branchId, productId, updateNameRequest.getName()))
                .map(product -> ResponseUtil.responseSuccessful(product, BusinessCode.S200000))
                .flatMap(response -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .onErrorResume(IllegalArgumentException.class, e -> 
                    ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.B400000, e.getMessage())))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ResponseUtil.responseError(BusinessCode.E500000)));
    }
}
