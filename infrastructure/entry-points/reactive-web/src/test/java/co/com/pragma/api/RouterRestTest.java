package co.com.pragma.api;

import co.com.pragma.api.dto.ResponseDto;
import co.com.pragma.api.helper.ValidationHelper;
import co.com.pragma.model.Branch;
import co.com.pragma.model.Constants;
import co.com.pragma.model.Franchise;
import co.com.pragma.model.Product;
import co.com.pragma.usecase.addbranch.AddBranchUseCase;
import co.com.pragma.usecase.addproduct.AddProductUseCase;
import co.com.pragma.usecase.createfranchise.CreateFranchiseUseCase;
import co.com.pragma.usecase.deleteproduct.DeleteProductUseCase;
import co.com.pragma.usecase.getmaxstockproducts.GetMaxStockProductsUseCase;
import co.com.pragma.usecase.getallfranchises.GetAllFranchisesUseCase;
import co.com.pragma.usecase.getfranchisebyid.GetFranchiseByIdUseCase;
import co.com.pragma.usecase.getbranchbyid.GetBranchByIdUseCase;
import co.com.pragma.usecase.getproductbyname.GetProductByNameUseCase;
import co.com.pragma.usecase.updatebranchname.UpdateBranchNameUseCase;
import co.com.pragma.usecase.updatebranch.UpdateBranchUseCase;
import co.com.pragma.usecase.updatefranchisename.UpdateFranchiseNameUseCase;
import co.com.pragma.usecase.updateproductname.UpdateProductNameUseCase;
import co.com.pragma.usecase.updateproductstock.UpdateProductStockUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class RouterRestTest {

    private WebTestClient webTestClient;

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
    private ValidationHelper validationHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Configurar ValidationHelper mock para que retorne el objeto validado
        when(validationHelper.validate(any()))
                .thenAnswer(invocation -> {
                    Object arg = invocation.getArgument(0);
                    if (arg == null) {
                        return Mono.error(new IllegalArgumentException("Object cannot be null"));
                    }
                    return Mono.just(arg);
                });
        
        Handler handler = new Handler(
                createFranchiseUseCase,
                addBranchUseCase,
                addProductUseCase,
                deleteProductUseCase,
                updateProductStockUseCase,
                getMaxStockProductsUseCase,
                getAllFranchisesUseCase,
                updateFranchiseNameUseCase,
                updateBranchNameUseCase,
                updateBranchUseCase,
                updateProductNameUseCase,
                getFranchiseByIdUseCase,
                getBranchByIdUseCase,
                getProductByNameUseCase,
                validationHelper
        );
        RouterRest routerRest = new RouterRest();
        RouterFunction<ServerResponse> routerFunction = routerRest.routerFunction(handler);
        
        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
    }

    @Test
    void testListenGETUseCase() {
        when(getMaxStockProductsUseCase.execute(anyString()))
                .thenReturn(Flux.empty());
        
        webTestClient.get()
                .uri(Constants.API_BASE_PATH + Constants.PATH_MAX_STOCK.replace("{franchiseId}", "franchise1"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseDto.class)
                .value(response -> {
                    assertEquals("S200000", response.getCode());
                    assertEquals("Successfully", response.getTitle());
                    assertNotNull(response.getData());
                });
    }

    @Test
    void testListenGETOtherUseCase() {
        when(getMaxStockProductsUseCase.execute(anyString()))
                .thenReturn(Flux.empty());
        
        webTestClient.get()
                .uri(Constants.API_BASE_PATH + Constants.PATH_MAX_STOCK.replace("{franchiseId}", "franchise2"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseDto.class)
                .value(response -> {
                    assertEquals("S200000", response.getCode());
                    assertEquals("Successfully", response.getTitle());
                    assertNotNull(response.getData());
                });
    }

    @Test
    void testListenPOSTUseCase() {
        Franchise franchise = new Franchise();
        franchise.setId("test-id");
        franchise.setName("Test Franchise");
        
        when(createFranchiseUseCase.execute(any()))
                .thenReturn(Mono.just(franchise));
        
        webTestClient.post()
                .uri(Constants.API_BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Test Franchise\"}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ResponseDto.class)
                .value(response -> {
                    assertEquals("S201000", response.getCode());
                    assertEquals("Successfully", response.getTitle());
                    assertNotNull(response.getData());
                });
    }

    @Test
    void testGetAllFranchisesRoute() {
        Franchise franchise = new Franchise();
        franchise.setId("1");
        franchise.setName("Test Franchise");
        
        when(getAllFranchisesUseCase.execute())
                .thenReturn(Flux.just(franchise));
        
        webTestClient.get()
                .uri(Constants.API_BASE_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseDto.class)
                .value(response -> {
                    assertEquals("S200000", response.getCode());
                    assertEquals("Successfully", response.getTitle());
                    assertNotNull(response.getData());
                });
    }

    @Test
    void testGetFranchiseByIdRoute() {
        Franchise franchise = new Franchise();
        franchise.setId("1");
        franchise.setName("Test Franchise");
        
        when(getFranchiseByIdUseCase.execute(anyString()))
                .thenReturn(Mono.just(franchise));
        
        webTestClient.get()
                .uri(Constants.API_BASE_PATH + Constants.PATH_FRANCHISE_ID.replace("{franchiseId}", "1"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseDto.class)
                .value(response -> {
                    assertEquals("S200000", response.getCode());
                    assertEquals("Successfully", response.getTitle());
                    assertNotNull(response.getData());
                });
    }

    @Test
    void testAddBranchRoute() {
        Branch branch = new Branch();
        branch.setId("1");
        branch.setName("Test Branch");
        
        when(addBranchUseCase.execute(anyString(), any()))
                .thenReturn(Mono.just(branch));
        
        webTestClient.post()
                .uri(Constants.API_BASE_PATH + Constants.PATH_BRANCHES.replace("{franchiseId}", "1"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Test Branch\"}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ResponseDto.class)
                .value(response -> {
                    assertEquals("S201000", response.getCode());
                    assertEquals("Successfully", response.getTitle());
                    assertNotNull(response.getData());
                });
    }

    @Test
    void testGetBranchByIdRoute() {
        Branch branch = new Branch();
        branch.setId("1");
        branch.setName("Test Branch");
        
        when(getBranchByIdUseCase.execute(anyString(), anyString()))
                .thenReturn(Mono.just(branch));
        
        webTestClient.get()
                .uri(Constants.API_BASE_PATH + Constants.PATH_BRANCH_ID.replace("{franchiseId}", "1").replace("{branchId}", "1"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseDto.class)
                .value(response -> {
                    assertEquals("S200000", response.getCode());
                    assertEquals("Successfully", response.getTitle());
                    assertNotNull(response.getData());
                });
    }

    @Test
    void testAddProductRoute() {
        Product product = new Product();
        product.setId("1");
        product.setName("Test Product");
        product.setStock(100);
        
        when(addProductUseCase.execute(anyString(), anyString(), any()))
                .thenReturn(Mono.just(product));
        
        webTestClient.post()
                .uri(Constants.API_BASE_PATH + Constants.PATH_PRODUCTS.replace("{franchiseId}", "1").replace("{branchId}", "1"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Test Product\",\"stock\":100}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ResponseDto.class)
                .value(response -> {
                    assertEquals("S201000", response.getCode());
                    assertEquals("Successfully", response.getTitle());
                    assertNotNull(response.getData());
                });
    }

    @Test
    void testGetProductByNameRoute() {
        Product product = new Product();
        product.setId("1");
        product.setName("Test Product");
        product.setStock(100);
        
        when(getProductByNameUseCase.execute(anyString(), anyString(), anyString()))
                .thenReturn(Mono.just(product));
        
        webTestClient.get()
                .uri(Constants.API_BASE_PATH + Constants.PATH_PRODUCT_BY_NAME.replace("{franchiseId}", "1").replace("{branchId}", "1").replace("{productName}", "Test Product"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseDto.class)
                .value(response -> {
                    assertEquals("S200000", response.getCode());
                    assertEquals("Successfully", response.getTitle());
                    assertNotNull(response.getData());
                });
    }
}