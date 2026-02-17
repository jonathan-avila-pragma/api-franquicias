package co.com.pragma.api;

import co.com.pragma.api.dto.*;
import co.com.pragma.api.helper.BusinessCode;
import co.com.pragma.api.helper.InputSanitizer;
import co.com.pragma.api.helper.ResponseUtil;
import co.com.pragma.api.helper.ValidationHelper;
import co.com.pragma.model.Franchise;
import co.com.pragma.model.Branch;
import co.com.pragma.model.Product;
import co.com.pragma.usecase.createfranchise.CreateFranchiseUseCase;
import co.com.pragma.usecase.addbranch.AddBranchUseCase;
import co.com.pragma.usecase.addproduct.AddProductUseCase;
import co.com.pragma.usecase.deleteproduct.DeleteProductUseCase;
import co.com.pragma.usecase.updateproductstock.UpdateProductStockUseCase;
import co.com.pragma.usecase.getmaxstockproducts.GetMaxStockProductsUseCase;
import co.com.pragma.usecase.getallfranchises.GetAllFranchisesUseCase;
import co.com.pragma.usecase.updatefranchisename.UpdateFranchiseNameUseCase;
import co.com.pragma.usecase.updatebranchname.UpdateBranchNameUseCase;
import co.com.pragma.usecase.updatebranch.UpdateBranchUseCase;
import co.com.pragma.usecase.updateproductname.UpdateProductNameUseCase;
import co.com.pragma.usecase.getfranchisebyid.GetFranchiseByIdUseCase;
import co.com.pragma.usecase.getbranchbyid.GetBranchByIdUseCase;
import co.com.pragma.usecase.getproductbyname.GetProductByNameUseCase;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private static final String PATH_VAR_FRANCHISE_ID = "franchiseId";
    private static final String PATH_VAR_BRANCH_ID = "branchId";
    private static final String PATH_VAR_PRODUCT_ID = "productId";
    private static final String PATH_VAR_PRODUCT_NAME = "productName";
    
    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final AddBranchUseCase addBranchUseCase;
    private final AddProductUseCase addProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final GetMaxStockProductsUseCase getMaxStockProductsUseCase;
    private final GetAllFranchisesUseCase getAllFranchisesUseCase;
    private final UpdateFranchiseNameUseCase updateFranchiseNameUseCase;
    private final UpdateBranchNameUseCase updateBranchNameUseCase;
    private final UpdateBranchUseCase updateBranchUseCase;
    private final UpdateProductNameUseCase updateProductNameUseCase;
    private final GetFranchiseByIdUseCase getFranchiseByIdUseCase;
    private final GetBranchByIdUseCase getBranchByIdUseCase;
    private final GetProductByNameUseCase getProductByNameUseCase;
    private final ValidationHelper validationHelper;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        log.info("Received POST request to create franchise");
        return request.bodyToMono(FranchiseRequest.class)
                .flatMap(validationHelper::validate)
                .doOnNext(franchiseRequest -> {
                    String sanitizedName = InputSanitizer.validateAndSanitizeId(franchiseRequest.getName());
                    franchiseRequest.setName(sanitizedName);
                    log.info("Creating franchise with name: {}", sanitizedName);
                })
                .flatMap(franchiseRequest -> {
                    Franchise franchise = new Franchise();
                    franchise.setName(franchiseRequest.getName());
                    franchise.setDescription(franchiseRequest.getDescription());
                    return createFranchiseUseCase.execute(franchise)
                            .doOnNext(f -> log.info("Franchise created successfully with ID: {}", f.getId()))
                            .doOnError(e -> log.error("Error creating franchise: {}", e.getMessage()));
                })
                .map(franchise -> ResponseUtil.responseSuccessful(franchise, BusinessCode.S201000))
                .flatMap(response -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .onErrorResume(CallNotPermittedException.class, e -> {
                    log.error("Circuit breaker is OPEN - service unavailable: {}", e.getMessage());
                    return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.B503000, "El servicio de base de datos no está disponible temporalmente. Por favor, intente más tarde."));
                })
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
        String franchiseId = InputSanitizer.validateAndSanitizeId(request.pathVariable(PATH_VAR_FRANCHISE_ID));
        return request.bodyToMono(BranchRequest.class)
                .flatMap(validationHelper::validate)
                .doOnNext(branchRequest -> {
                    String sanitizedName = InputSanitizer.validateAndSanitizeName(branchRequest.getName());
                    branchRequest.setName(sanitizedName);
                })
                .flatMap(branchRequest -> {
                    Branch branch = new Branch();
                    branch.setName(branchRequest.getName());
                    branch.setAddress(branchRequest.getAddress());
                    branch.setCity(branchRequest.getCity());
                    return addBranchUseCase.execute(franchiseId, branch);
                })
                .map(branch -> ResponseUtil.responseSuccessful(branch, BusinessCode.S201000))
                .flatMap(response -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .onErrorResume(CallNotPermittedException.class, e -> handleCircuitBreakerOpen())
                .onErrorResume(IllegalArgumentException.class, e -> 
                    ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.B400000, e.getMessage())))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ResponseUtil.responseError(BusinessCode.E500000)));
    }

    public Mono<ServerResponse> addProduct(ServerRequest request) {
        String franchiseId = InputSanitizer.validateAndSanitizeId(request.pathVariable(PATH_VAR_FRANCHISE_ID));
        String branchId = InputSanitizer.validateAndSanitizeId(request.pathVariable(PATH_VAR_BRANCH_ID));
        return request.bodyToMono(ProductRequest.class)
                .flatMap(validationHelper::validate)
                .doOnNext(productRequest -> {
                    String sanitizedName = InputSanitizer.validateAndSanitizeName(productRequest.getName());
                    productRequest.setName(sanitizedName);
                    productRequest.setStock(InputSanitizer.validateNumber(productRequest.getStock(), 0, 999999));
                })
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
                .onErrorResume(CallNotPermittedException.class, e -> handleCircuitBreakerOpen())
                .onErrorResume(IllegalArgumentException.class, e -> 
                    ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.B400000, e.getMessage())))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ResponseUtil.responseError(BusinessCode.E500000)));
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        String franchiseId = InputSanitizer.validateAndSanitizeId(request.pathVariable(PATH_VAR_FRANCHISE_ID));
        String branchId = InputSanitizer.validateAndSanitizeId(request.pathVariable(PATH_VAR_BRANCH_ID));
        String productId = InputSanitizer.validateAndSanitizeId(request.pathVariable(PATH_VAR_PRODUCT_ID));
        return deleteProductUseCase.execute(franchiseId, branchId, productId)
                .then(Mono.just(ResponseUtil.responseSuccessful(null, BusinessCode.S200000)))
                .flatMap(response -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .onErrorResume(CallNotPermittedException.class, e -> handleCircuitBreakerOpen())
                .onErrorResume(IllegalArgumentException.class, e -> 
                    ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.B400000, e.getMessage())))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ResponseUtil.responseError(BusinessCode.E500000)));
    }

    public Mono<ServerResponse> updateProductStock(ServerRequest request) {
        String franchiseId = InputSanitizer.validateAndSanitizeId(request.pathVariable(PATH_VAR_FRANCHISE_ID));
        String branchId = InputSanitizer.validateAndSanitizeId(request.pathVariable(PATH_VAR_BRANCH_ID));
        String productId = InputSanitizer.validateAndSanitizeId(request.pathVariable(PATH_VAR_PRODUCT_ID));
        return request.bodyToMono(UpdateStockRequest.class)
                .flatMap(validationHelper::validate)
                .doOnNext(updateStockRequest -> 
                    updateStockRequest.setStock(InputSanitizer.validateNumber(updateStockRequest.getStock(), 0, 999999)))
                .flatMap(updateStockRequest -> 
                    updateProductStockUseCase.execute(franchiseId, branchId, productId, updateStockRequest.getStock()))
                .map(product -> ResponseUtil.responseSuccessful(product, BusinessCode.S200000))
                .flatMap(response -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .onErrorResume(CallNotPermittedException.class, e -> handleCircuitBreakerOpen())
                .onErrorResume(IllegalArgumentException.class, e -> 
                    ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.B400000, e.getMessage())))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ResponseUtil.responseError(BusinessCode.E500000)));
    }

    public Mono<ServerResponse> getMaxStockProducts(ServerRequest request) {
        String franchiseId = InputSanitizer.validateAndSanitizeId(request.pathVariable(PATH_VAR_FRANCHISE_ID));
        return getMaxStockProductsUseCase.execute(franchiseId)
                .collectList()
                .map(products -> ResponseUtil.responseSuccessful(products, BusinessCode.S200000))
                .flatMap(response -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .onErrorResume(CallNotPermittedException.class, e -> handleCircuitBreakerOpen())
                .onErrorResume(IllegalArgumentException.class, e -> 
                    ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.B400000, e.getMessage())))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ResponseUtil.responseError(BusinessCode.E500000)));
    }

    @SuppressWarnings("java:S1172") // ServerRequest parameter required by RouterFunction signature
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
                .onErrorResume(CallNotPermittedException.class, e -> handleCircuitBreakerOpen())
                .onErrorResume(e -> {
                    log.error("Error getting all franchises", e);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.E500000));
                });
    }

    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {
        String franchiseId = InputSanitizer.validateAndSanitizeId(request.pathVariable(PATH_VAR_FRANCHISE_ID));
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(validationHelper::validate)
                .doOnNext(updateNameRequest -> {
                    String sanitizedName = InputSanitizer.validateAndSanitizeName(updateNameRequest.getName());
                    updateNameRequest.setName(sanitizedName);
                })
                .flatMap(updateNameRequest -> 
                    updateFranchiseNameUseCase.execute(franchiseId, updateNameRequest.getName()))
                .map(franchise -> ResponseUtil.responseSuccessful(franchise, BusinessCode.S200000))
                .flatMap(response -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .onErrorResume(CallNotPermittedException.class, e -> handleCircuitBreakerOpen())
                .onErrorResume(IllegalArgumentException.class, e -> 
                    ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.B400000, e.getMessage())))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ResponseUtil.responseError(BusinessCode.E500000)));
    }

    public Mono<ServerResponse> updateBranchName(ServerRequest request) {
        String franchiseId = InputSanitizer.validateAndSanitizeId(request.pathVariable(PATH_VAR_FRANCHISE_ID));
        String branchId = InputSanitizer.validateAndSanitizeId(request.pathVariable(PATH_VAR_BRANCH_ID));
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(validationHelper::validate)
                .doOnNext(updateNameRequest -> {
                    String sanitizedName = InputSanitizer.validateAndSanitizeName(updateNameRequest.getName());
                    updateNameRequest.setName(sanitizedName);
                })
                .flatMap(updateNameRequest -> 
                    updateBranchNameUseCase.execute(franchiseId, branchId, updateNameRequest.getName()))
                .map(branch -> ResponseUtil.responseSuccessful(branch, BusinessCode.S200000))
                .flatMap(response -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .onErrorResume(CallNotPermittedException.class, e -> handleCircuitBreakerOpen())
                .onErrorResume(IllegalArgumentException.class, e -> 
                    ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.B400000, e.getMessage())))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ResponseUtil.responseError(BusinessCode.E500000)));
    }

    public Mono<ServerResponse> updateBranch(ServerRequest request) {
        String franchiseId = InputSanitizer.validateAndSanitizeId(request.pathVariable(PATH_VAR_FRANCHISE_ID));
        String branchId = InputSanitizer.validateAndSanitizeId(request.pathVariable(PATH_VAR_BRANCH_ID));
        log.info("Received PUT request to update branch: {} for franchise: {}", branchId, franchiseId);
        return request.bodyToMono(UpdateBranchRequest.class)
                .flatMap(validationHelper::validate)
                .filter(UpdateBranchRequest::hasAtLeastOneField)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("At least one field (name, address, or city) must be provided")))
                .map(this::sanitizeBranchRequest)
                .map(this::mapToBranch)
                .flatMap(branchUpdate -> updateBranchUseCase.execute(franchiseId, branchId, branchUpdate))
                .doOnNext(branch -> log.info("Branch updated successfully: {}", branch.getId()))
                .map(branch -> ResponseUtil.responseSuccessful(branch, BusinessCode.S200000))
                .flatMap(response -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .onErrorResume(CallNotPermittedException.class, e -> handleCircuitBreakerOpen())
                .onErrorResume(IllegalArgumentException.class, e -> {
                    log.error("Validation error updating branch: {}", e.getMessage());
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.B400000, e.getMessage()));
                })
                .onErrorResume(e -> {
                    log.error("Error updating branch", e);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.E500000));
                });
    }

    public Mono<ServerResponse> updateProductName(ServerRequest request) {
        String franchiseId = InputSanitizer.validateAndSanitizeId(request.pathVariable(PATH_VAR_FRANCHISE_ID));
        String branchId = InputSanitizer.validateAndSanitizeId(request.pathVariable(PATH_VAR_BRANCH_ID));
        String productId = InputSanitizer.validateAndSanitizeId(request.pathVariable(PATH_VAR_PRODUCT_ID));
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(validationHelper::validate)
                .doOnNext(updateNameRequest -> {
                    String sanitizedName = InputSanitizer.validateAndSanitizeName(updateNameRequest.getName());
                    updateNameRequest.setName(sanitizedName);
                })
                .flatMap(updateNameRequest -> 
                    updateProductNameUseCase.execute(franchiseId, branchId, productId, updateNameRequest.getName()))
                .map(product -> ResponseUtil.responseSuccessful(product, BusinessCode.S200000))
                .flatMap(response -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .onErrorResume(CallNotPermittedException.class, e -> handleCircuitBreakerOpen())
                .onErrorResume(IllegalArgumentException.class, e -> 
                    ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.B400000, e.getMessage())))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.E500000)));
    }

    public Mono<ServerResponse> getFranchiseById(ServerRequest request) {
        String franchiseId = InputSanitizer.validateAndSanitizeId(request.pathVariable(PATH_VAR_FRANCHISE_ID));
        log.info("Received GET request to get franchise by ID: {}", franchiseId);
        return getFranchiseByIdUseCase.execute(franchiseId)
                .doOnNext(franchise -> log.info("Franchise found: {}", franchise.getId()))
                .map(franchise -> ResponseUtil.responseSuccessful(franchise, BusinessCode.S200000))
                .flatMap(response -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .onErrorResume(CallNotPermittedException.class, e -> handleCircuitBreakerOpen())
                .onErrorResume(IllegalArgumentException.class, e -> {
                    log.error("Franchise not found: {}", franchiseId);
                    return ServerResponse.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.B404000, e.getMessage()));
                })
                .onErrorResume(e -> {
                    log.error("Error getting franchise by ID", e);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.E500000));
                });
    }

    public Mono<ServerResponse> getBranchById(ServerRequest request) {
        String franchiseId = InputSanitizer.validateAndSanitizeId(request.pathVariable(PATH_VAR_FRANCHISE_ID));
        String branchId = InputSanitizer.validateAndSanitizeId(request.pathVariable(PATH_VAR_BRANCH_ID));
        log.info("Received GET request to get branch by ID: {} for franchise: {}", branchId, franchiseId);
        return getBranchByIdUseCase.execute(franchiseId, branchId)
                .doOnNext(branch -> log.info("Branch found: {}", branch.getId()))
                .map(branch -> ResponseUtil.responseSuccessful(branch, BusinessCode.S200000))
                .flatMap(response -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .onErrorResume(CallNotPermittedException.class, e -> handleCircuitBreakerOpen())
                .onErrorResume(IllegalArgumentException.class, e -> {
                    log.error("Branch not found: {} for franchise: {}", branchId, franchiseId);
                    return ServerResponse.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.B404000, e.getMessage()));
                })
                .onErrorResume(e -> {
                    log.error("Error getting branch by ID", e);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.E500000));
                });
    }

    public Mono<ServerResponse> getProductByName(ServerRequest request) {
        String franchiseId = InputSanitizer.validateAndSanitizeId(request.pathVariable(PATH_VAR_FRANCHISE_ID));
        String branchId = InputSanitizer.validateAndSanitizeId(request.pathVariable(PATH_VAR_BRANCH_ID));
        String productName = InputSanitizer.validateAndSanitizeName(request.pathVariable(PATH_VAR_PRODUCT_NAME));
        log.info("Received GET request to get product by name: {} for branch: {} in franchise: {}", productName, branchId, franchiseId);
        return getProductByNameUseCase.execute(franchiseId, branchId, productName)
                .doOnNext(product -> log.info("Product found: {}", product.getName()))
                .map(product -> ResponseUtil.responseSuccessful(product, BusinessCode.S200000))
                .flatMap(response -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .onErrorResume(CallNotPermittedException.class, e -> handleCircuitBreakerOpen())
                .onErrorResume(IllegalArgumentException.class, e -> {
                    log.error("Product not found: {} for branch: {} in franchise: {}", productName, branchId, franchiseId);
                    return ServerResponse.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.B404000, e.getMessage()));
                })
                .onErrorResume(e -> {
                    log.error("Error getting product by name", e);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ResponseUtil.responseError(BusinessCode.E500000));
                });
    }

    private Mono<ServerResponse> handleCircuitBreakerOpen() {
        log.error("Circuit breaker is OPEN - service unavailable");
        return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ResponseUtil.responseError(BusinessCode.B503000, 
                        "El servicio de base de datos no está disponible temporalmente. Por favor, intente más tarde."));
    }

    private UpdateBranchRequest sanitizeBranchRequest(UpdateBranchRequest request) {
        UpdateBranchRequest sanitized = new UpdateBranchRequest();
        
        sanitized.setName(Optional.ofNullable(request.getName())
                .filter(name -> !name.trim().isEmpty())
                .map(InputSanitizer::validateAndSanitizeName)
                .orElse(request.getName()));
        
        sanitized.setAddress(Optional.ofNullable(request.getAddress())
                .filter(address -> !address.trim().isEmpty())
                .map(InputSanitizer::sanitize)
                .orElse(request.getAddress()));
        
        sanitized.setCity(Optional.ofNullable(request.getCity())
                .filter(city -> !city.trim().isEmpty())
                .map(InputSanitizer::sanitize)
                .orElse(request.getCity()));
        
        return sanitized;
    }

    private Branch mapToBranch(UpdateBranchRequest request) {
        Branch branch = new Branch();
        branch.setName(request.getName());
        branch.setAddress(request.getAddress());
        branch.setCity(request.getCity());
        return branch;
    }
}
