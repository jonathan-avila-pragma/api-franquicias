package co.com.pragma.api;

import co.com.pragma.api.dto.*;
import co.com.pragma.api.helper.ValidationHelper;
import co.com.pragma.model.Branch;
import co.com.pragma.model.Franchise;
import co.com.pragma.model.Product;
import co.com.pragma.usecase.addbranch.AddBranchUseCase;
import co.com.pragma.usecase.addproduct.AddProductUseCase;
import co.com.pragma.usecase.createfranchise.CreateFranchiseUseCase;
import co.com.pragma.usecase.deleteproduct.DeleteProductUseCase;
import co.com.pragma.usecase.getallfranchises.GetAllFranchisesUseCase;
import co.com.pragma.usecase.getbranchbyid.GetBranchByIdUseCase;
import co.com.pragma.usecase.getfranchisebyid.GetFranchiseByIdUseCase;
import co.com.pragma.usecase.getmaxstockproducts.GetMaxStockProductsUseCase;
import co.com.pragma.usecase.getproductbyname.GetProductByNameUseCase;
import co.com.pragma.usecase.getproductsbybranch.GetProductsByBranchUseCase;
import co.com.pragma.usecase.updatebranch.UpdateBranchUseCase;
import co.com.pragma.usecase.updatebranchname.UpdateBranchNameUseCase;
import co.com.pragma.usecase.updatefranchisename.UpdateFranchiseNameUseCase;
import co.com.pragma.usecase.updateproductname.UpdateProductNameUseCase;
import co.com.pragma.usecase.updateproductstock.UpdateProductStockUseCase;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private CreateFranchiseUseCase createFranchiseUseCase;
    @Mock
    private AddBranchUseCase addBranchUseCase;
    @Mock
    private AddProductUseCase addProductUseCase;
    @Mock
    private DeleteProductUseCase deleteProductUseCase;
    @Mock
    private UpdateProductStockUseCase updateProductStockUseCase;
    @Mock
    private GetMaxStockProductsUseCase getMaxStockProductsUseCase;
    @Mock
    private GetAllFranchisesUseCase getAllFranchisesUseCase;
    @Mock
    private UpdateFranchiseNameUseCase updateFranchiseNameUseCase;
    @Mock
    private UpdateBranchNameUseCase updateBranchNameUseCase;
    @Mock
    private UpdateBranchUseCase updateBranchUseCase;
    @Mock
    private UpdateProductNameUseCase updateProductNameUseCase;
    @Mock
    private GetFranchiseByIdUseCase getFranchiseByIdUseCase;
    @Mock
    private GetBranchByIdUseCase getBranchByIdUseCase;
    @Mock
    private GetProductByNameUseCase getProductByNameUseCase;
    @Mock
    private GetProductsByBranchUseCase getProductsByBranchUseCase;
    @Mock
    private ValidationHelper validationHelper;
    @Mock
    private ServerRequest serverRequest;

    private Handler handler;

    @BeforeEach
    void setUp() {
        handler = new Handler(createFranchiseUseCase, addBranchUseCase, addProductUseCase,
                deleteProductUseCase, updateProductStockUseCase, getMaxStockProductsUseCase,
                getAllFranchisesUseCase, updateFranchiseNameUseCase, updateBranchNameUseCase,
                updateBranchUseCase, updateProductNameUseCase, getFranchiseByIdUseCase,
                getBranchByIdUseCase, getProductByNameUseCase, getProductsByBranchUseCase, validationHelper);
    }

    @Test
    void createFranchise_Success() {
        FranchiseRequest request = new FranchiseRequest();
        request.setName("Test Franchise");
        request.setDescription("Test Description");
        Franchise franchise = new Franchise();
        franchise.setId("1");
        franchise.setName("Test Franchise");
        franchise.setDescription("Test Description");

        when(serverRequest.bodyToMono(FranchiseRequest.class)).thenReturn(Mono.just(request));
        when(validationHelper.validate(any())).thenReturn(Mono.just(request));
        when(createFranchiseUseCase.execute(any())).thenReturn(Mono.just(franchise));

        StepVerifier.create(handler.createFranchise(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.CREATED)
                .verifyComplete();

        verify(createFranchiseUseCase).execute(any());
    }

    @Test
    void createFranchise_SuccessWithoutDescription() {
        FranchiseRequest request = new FranchiseRequest();
        request.setName("Test Franchise");
        Franchise franchise = new Franchise();
        franchise.setId("1");
        franchise.setName("Test Franchise");

        when(serverRequest.bodyToMono(FranchiseRequest.class)).thenReturn(Mono.just(request));
        when(validationHelper.validate(any())).thenReturn(Mono.just(request));
        when(createFranchiseUseCase.execute(any())).thenReturn(Mono.just(franchise));

        StepVerifier.create(handler.createFranchise(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.CREATED)
                .verifyComplete();

        verify(createFranchiseUseCase).execute(any());
    }

    @Test
    void createFranchise_ValidationError() {
        when(serverRequest.bodyToMono(FranchiseRequest.class)).thenReturn(Mono.error(new IllegalArgumentException("Invalid name")));

        StepVerifier.create(handler.createFranchise(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.BAD_REQUEST)
                .verifyComplete();
    }

    @Test
    void createFranchise_CircuitBreakerOpen() {
        FranchiseRequest request = new FranchiseRequest();
        request.setName("Test");
        CallNotPermittedException exception = mock(CallNotPermittedException.class);

        when(serverRequest.bodyToMono(FranchiseRequest.class)).thenReturn(Mono.just(request));
        when(validationHelper.validate(any())).thenReturn(Mono.just(request));
        when(createFranchiseUseCase.execute(any())).thenReturn(Mono.error(exception));

        StepVerifier.create(handler.createFranchise(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.SERVICE_UNAVAILABLE)
                .verifyComplete();
    }

    @Test
    void addBranch_Success() {
        BranchRequest request = new BranchRequest();
        request.setName("Test Branch");
        request.setAddress("123 Main St");
        request.setCity("Bogotá");
        Branch branch = new Branch();
        branch.setId("1");
        branch.setName("Test Branch");
        branch.setAddress("123 Main St");
        branch.setCity("Bogotá");

        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(serverRequest.bodyToMono(BranchRequest.class)).thenReturn(Mono.just(request));
        when(validationHelper.validate(any())).thenReturn(Mono.just(request));
        when(addBranchUseCase.execute(anyString(), any())).thenReturn(Mono.just(branch));

        StepVerifier.create(handler.addBranch(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.CREATED)
                .verifyComplete();

        verify(addBranchUseCase).execute(eq("1"), any());
    }

    @Test
    void addBranch_SuccessWithoutAddressAndCity() {
        BranchRequest request = new BranchRequest();
        request.setName("Test Branch");
        Branch branch = new Branch();
        branch.setId("1");
        branch.setName("Test Branch");

        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(serverRequest.bodyToMono(BranchRequest.class)).thenReturn(Mono.just(request));
        when(validationHelper.validate(any())).thenReturn(Mono.just(request));
        when(addBranchUseCase.execute(anyString(), any())).thenReturn(Mono.just(branch));

        StepVerifier.create(handler.addBranch(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.CREATED)
                .verifyComplete();

        verify(addBranchUseCase).execute(eq("1"), any());
    }

    @Test
    void addProduct_Success() {
        ProductRequest request = new ProductRequest();
        request.setName("Test Product");
        request.setStock(100);
        Product product = new Product();
        product.setId("1");
        product.setName("Test Product");
        product.setStock(100);

        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(serverRequest.pathVariable("branchId")).thenReturn("1");
        when(serverRequest.bodyToMono(ProductRequest.class)).thenReturn(Mono.just(request));
        when(validationHelper.validate(any())).thenReturn(Mono.just(request));
        when(addProductUseCase.execute(anyString(), anyString(), any())).thenReturn(Mono.just(product));

        StepVerifier.create(handler.addProduct(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.CREATED)
                .verifyComplete();

        verify(addProductUseCase).execute(eq("1"), eq("1"), any());
    }

    @Test
    void deleteProduct_Success() {
        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(serverRequest.pathVariable("branchId")).thenReturn("1");
        when(serverRequest.pathVariable("productId")).thenReturn("1");
        when(deleteProductUseCase.execute(anyString(), anyString(), anyString())).thenReturn(Mono.empty());

        StepVerifier.create(handler.deleteProduct(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.OK)
                .verifyComplete();

        verify(deleteProductUseCase).execute("1", "1", "1");
    }

    @Test
    void updateProductStock_Success() {
        UpdateStockRequest request = new UpdateStockRequest();
        request.setStock(200);
        Product product = new Product();
        product.setStock(200);

        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(serverRequest.pathVariable("branchId")).thenReturn("1");
        when(serverRequest.pathVariable("productId")).thenReturn("1");
        when(serverRequest.bodyToMono(UpdateStockRequest.class)).thenReturn(Mono.just(request));
        when(validationHelper.validate(any())).thenReturn(Mono.just(request));
        when(updateProductStockUseCase.execute(anyString(), anyString(), anyString(), anyInt())).thenReturn(Mono.just(product));

        StepVerifier.create(handler.updateProductStock(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.OK)
                .verifyComplete();

        verify(updateProductStockUseCase).execute("1", "1", "1", 200);
    }

    @Test
    void getMaxStockProducts_Success() {
        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(getMaxStockProductsUseCase.execute(anyString())).thenReturn(Flux.empty());

        StepVerifier.create(handler.getMaxStockProducts(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.OK)
                .verifyComplete();

        verify(getMaxStockProductsUseCase).execute("1");
    }

    @Test
    void getAllFranchises_Success() {
        Franchise franchise = new Franchise();
        franchise.setId("1");

        when(getAllFranchisesUseCase.execute()).thenReturn(Flux.just(franchise));

        StepVerifier.create(handler.getAllFranchises(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.OK)
                .verifyComplete();

        verify(getAllFranchisesUseCase).execute();
    }

    @Test
    void updateFranchiseName_Success() {
        UpdateNameRequest request = new UpdateNameRequest();
        request.setName("Updated Name");
        Franchise franchise = new Franchise();
        franchise.setName("Updated Name");

        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(serverRequest.bodyToMono(UpdateNameRequest.class)).thenReturn(Mono.just(request));
        when(validationHelper.validate(any())).thenReturn(Mono.just(request));
        when(updateFranchiseNameUseCase.execute(anyString(), anyString())).thenReturn(Mono.just(franchise));

        StepVerifier.create(handler.updateFranchiseName(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.OK)
                .verifyComplete();

        verify(updateFranchiseNameUseCase).execute("1", "Updated Name");
    }

    @Test
    void updateBranchName_Success() {
        UpdateNameRequest request = new UpdateNameRequest();
        request.setName("Updated Branch");
        Branch branch = new Branch();
        branch.setName("Updated Branch");

        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(serverRequest.pathVariable("branchId")).thenReturn("1");
        when(serverRequest.bodyToMono(UpdateNameRequest.class)).thenReturn(Mono.just(request));
        when(validationHelper.validate(any())).thenReturn(Mono.just(request));
        when(updateBranchNameUseCase.execute(anyString(), anyString(), anyString())).thenReturn(Mono.just(branch));

        StepVerifier.create(handler.updateBranchName(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.OK)
                .verifyComplete();

        verify(updateBranchNameUseCase).execute("1", "1", "Updated Branch");
    }

    @Test
    void updateBranch_Success() {
        UpdateBranchRequest request = new UpdateBranchRequest();
        request.setName("Updated");
        Branch branch = new Branch();
        branch.setName("Updated");

        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(serverRequest.pathVariable("branchId")).thenReturn("1");
        when(serverRequest.bodyToMono(UpdateBranchRequest.class)).thenReturn(Mono.just(request));
        when(validationHelper.validate(any())).thenReturn(Mono.just(request));
        when(updateBranchUseCase.execute(anyString(), anyString(), any())).thenReturn(Mono.just(branch));

        StepVerifier.create(handler.updateBranch(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.OK)
                .verifyComplete();

        verify(updateBranchUseCase).execute(eq("1"), eq("1"), any());
    }

    @Test
    void updateBranch_SuccessWithAddress() {
        UpdateBranchRequest request = new UpdateBranchRequest();
        request.setAddress("New Address");
        Branch branch = new Branch();
        branch.setAddress("New Address");

        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(serverRequest.pathVariable("branchId")).thenReturn("1");
        when(serverRequest.bodyToMono(UpdateBranchRequest.class)).thenReturn(Mono.just(request));
        when(validationHelper.validate(any())).thenReturn(Mono.just(request));
        when(updateBranchUseCase.execute(anyString(), anyString(), any())).thenReturn(Mono.just(branch));

        StepVerifier.create(handler.updateBranch(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.OK)
                .verifyComplete();

        verify(updateBranchUseCase).execute(eq("1"), eq("1"), any());
    }

    @Test
    void updateBranch_SuccessWithCity() {
        UpdateBranchRequest request = new UpdateBranchRequest();
        request.setCity("New City");
        Branch branch = new Branch();
        branch.setCity("New City");

        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(serverRequest.pathVariable("branchId")).thenReturn("1");
        when(serverRequest.bodyToMono(UpdateBranchRequest.class)).thenReturn(Mono.just(request));
        when(validationHelper.validate(any())).thenReturn(Mono.just(request));
        when(updateBranchUseCase.execute(anyString(), anyString(), any())).thenReturn(Mono.just(branch));

        StepVerifier.create(handler.updateBranch(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.OK)
                .verifyComplete();

        verify(updateBranchUseCase).execute(eq("1"), eq("1"), any());
    }

    @Test
    void updateBranch_SuccessWithAllFields() {
        UpdateBranchRequest request = new UpdateBranchRequest();
        request.setName("Updated Name");
        request.setAddress("Updated Address");
        request.setCity("Updated City");
        Branch branch = new Branch();
        branch.setName("Updated Name");
        branch.setAddress("Updated Address");
        branch.setCity("Updated City");

        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(serverRequest.pathVariable("branchId")).thenReturn("1");
        when(serverRequest.bodyToMono(UpdateBranchRequest.class)).thenReturn(Mono.just(request));
        when(validationHelper.validate(any())).thenReturn(Mono.just(request));
        when(updateBranchUseCase.execute(anyString(), anyString(), any())).thenReturn(Mono.just(branch));

        StepVerifier.create(handler.updateBranch(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.OK)
                .verifyComplete();

        verify(updateBranchUseCase).execute(eq("1"), eq("1"), any());
    }

    @Test
    void updateBranch_CircuitBreakerOpen() {
        UpdateBranchRequest request = new UpdateBranchRequest();
        request.setName("Updated");
        CallNotPermittedException exception = mock(CallNotPermittedException.class);

        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(serverRequest.pathVariable("branchId")).thenReturn("1");
        when(serverRequest.bodyToMono(UpdateBranchRequest.class)).thenReturn(Mono.just(request));
        when(validationHelper.validate(any())).thenReturn(Mono.just(request));
        when(updateBranchUseCase.execute(anyString(), anyString(), any())).thenReturn(Mono.error(exception));

        StepVerifier.create(handler.updateBranch(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.SERVICE_UNAVAILABLE)
                .verifyComplete();
    }

    @Test
    void updateBranch_NoFieldsProvided() {
        UpdateBranchRequest request = new UpdateBranchRequest();

        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(serverRequest.pathVariable("branchId")).thenReturn("1");
        when(serverRequest.bodyToMono(UpdateBranchRequest.class)).thenReturn(Mono.just(request));
        when(validationHelper.validate(any())).thenReturn(Mono.just(request));

        StepVerifier.create(handler.updateBranch(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.BAD_REQUEST)
                .verifyComplete();
    }

    @Test
    void updateProductName_Success() {
        UpdateNameRequest request = new UpdateNameRequest();
        request.setName("Updated Product");
        Product product = new Product();
        product.setName("Updated Product");

        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(serverRequest.pathVariable("branchId")).thenReturn("1");
        when(serverRequest.pathVariable("productId")).thenReturn("1");
        when(serverRequest.bodyToMono(UpdateNameRequest.class)).thenReturn(Mono.just(request));
        when(validationHelper.validate(any())).thenReturn(Mono.just(request));
        when(updateProductNameUseCase.execute(anyString(), anyString(), anyString(), anyString())).thenReturn(Mono.just(product));

        StepVerifier.create(handler.updateProductName(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.OK)
                .verifyComplete();

        verify(updateProductNameUseCase).execute("1", "1", "1", "Updated Product");
    }

    @Test
    void getFranchiseById_Success() {
        Franchise franchise = new Franchise();
        franchise.setId("1");

        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(getFranchiseByIdUseCase.execute(anyString())).thenReturn(Mono.just(franchise));

        StepVerifier.create(handler.getFranchiseById(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.OK)
                .verifyComplete();

        verify(getFranchiseByIdUseCase).execute("1");
    }

    @Test
    void getFranchiseById_NotFound() {
        when(serverRequest.pathVariable("franchiseId")).thenReturn("999");
        when(getFranchiseByIdUseCase.execute(anyString())).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        StepVerifier.create(handler.getFranchiseById(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.NOT_FOUND)
                .verifyComplete();
    }

    @Test
    void getBranchById_Success() {
        Branch branch = new Branch();
        branch.setId("1");

        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(serverRequest.pathVariable("branchId")).thenReturn("1");
        when(getBranchByIdUseCase.execute(anyString(), anyString())).thenReturn(Mono.just(branch));

        StepVerifier.create(handler.getBranchById(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.OK)
                .verifyComplete();

        verify(getBranchByIdUseCase).execute("1", "1");
    }

    @Test
    void getProductByName_Success() {
        Product product = new Product();
        product.setName("Test Product");

        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(serverRequest.pathVariable("branchId")).thenReturn("1");
        when(serverRequest.pathVariable("productName")).thenReturn("Test Product");
        when(getProductByNameUseCase.execute(anyString(), anyString(), anyString())).thenReturn(Mono.just(product));

        StepVerifier.create(handler.getProductByName(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.OK)
                .verifyComplete();

        verify(getProductByNameUseCase).execute("1", "1", "Test Product");
    }

    @Test
    void getProductsByBranch_Success() {
        Product product1 = new Product("1", "Product 1", 10);
        Product product2 = new Product("2", "Product 2", 20);

        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(serverRequest.pathVariable("branchId")).thenReturn("1");
        when(getProductsByBranchUseCase.execute(anyString(), anyString()))
                .thenReturn(Flux.just(product1, product2));

        StepVerifier.create(handler.getProductsByBranch(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.OK)
                .verifyComplete();

        verify(getProductsByBranchUseCase).execute("1", "1");
    }

    @Test
    void getProductsByBranch_Empty() {
        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(serverRequest.pathVariable("branchId")).thenReturn("1");
        when(getProductsByBranchUseCase.execute(anyString(), anyString()))
                .thenReturn(Flux.empty());

        StepVerifier.create(handler.getProductsByBranch(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.OK)
                .verifyComplete();

        verify(getProductsByBranchUseCase).execute("1", "1");
    }

    @Test
    void getProductsByBranch_BranchNotFound() {
        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(serverRequest.pathVariable("branchId")).thenReturn("1");
        when(getProductsByBranchUseCase.execute(anyString(), anyString()))
                .thenReturn(Flux.error(new IllegalArgumentException("Branch not found")));

        StepVerifier.create(handler.getProductsByBranch(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.NOT_FOUND)
                .verifyComplete();

        verify(getProductsByBranchUseCase).execute("1", "1");
    }

    @Test
    void getProductByName_NotFound() {
        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(serverRequest.pathVariable("branchId")).thenReturn("1");
        when(serverRequest.pathVariable("productName")).thenReturn("Unknown");
        when(getProductByNameUseCase.execute(anyString(), anyString(), anyString())).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        StepVerifier.create(handler.getProductByName(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.NOT_FOUND)
                .verifyComplete();
    }

    @Test
    void addBranch_InternalError() {
        BranchRequest request = new BranchRequest();
        request.setName("Test");

        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(serverRequest.bodyToMono(BranchRequest.class)).thenReturn(Mono.just(request));
        when(validationHelper.validate(any())).thenReturn(Mono.just(request));
        when(addBranchUseCase.execute(anyString(), any())).thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(handler.addBranch(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.INTERNAL_SERVER_ERROR)
                .verifyComplete();
    }

    @Test
    void addBranch_CircuitBreakerOpen() {
        BranchRequest request = new BranchRequest();
        request.setName("Test");
        CallNotPermittedException exception = mock(CallNotPermittedException.class);

        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(serverRequest.bodyToMono(BranchRequest.class)).thenReturn(Mono.just(request));
        when(validationHelper.validate(any())).thenReturn(Mono.just(request));
        when(addBranchUseCase.execute(anyString(), any())).thenReturn(Mono.error(exception));

        StepVerifier.create(handler.addBranch(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.SERVICE_UNAVAILABLE)
                .verifyComplete();
    }

    @Test
    void getBranchById_NotFound() {
        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(serverRequest.pathVariable("branchId")).thenReturn("999");
        when(getBranchByIdUseCase.execute(anyString(), anyString())).thenReturn(Mono.error(new IllegalArgumentException("Not found")));

        StepVerifier.create(handler.getBranchById(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.NOT_FOUND)
                .verifyComplete();
    }

    @Test
    void getBranchById_CircuitBreakerOpen() {
        CallNotPermittedException exception = mock(CallNotPermittedException.class);

        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(serverRequest.pathVariable("branchId")).thenReturn("1");
        when(getBranchByIdUseCase.execute(anyString(), anyString())).thenReturn(Mono.error(exception));

        StepVerifier.create(handler.getBranchById(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.SERVICE_UNAVAILABLE)
                .verifyComplete();
    }

    @Test
    void getFranchiseById_CircuitBreakerOpen() {
        CallNotPermittedException exception = mock(CallNotPermittedException.class);

        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(getFranchiseByIdUseCase.execute(anyString())).thenReturn(Mono.error(exception));

        StepVerifier.create(handler.getFranchiseById(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.SERVICE_UNAVAILABLE)
                .verifyComplete();
    }

    @Test
    void getProductByName_CircuitBreakerOpen() {
        CallNotPermittedException exception = mock(CallNotPermittedException.class);

        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(serverRequest.pathVariable("branchId")).thenReturn("1");
        when(serverRequest.pathVariable("productName")).thenReturn("Test Product");
        when(getProductByNameUseCase.execute(anyString(), anyString(), anyString())).thenReturn(Mono.error(exception));

        StepVerifier.create(handler.getProductByName(serverRequest))
                .expectNextMatches(response -> response.statusCode() == HttpStatus.SERVICE_UNAVAILABLE)
                .verifyComplete();
    }
}
