package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.ResponseDto;
import co.com.bancolombia.model.Constants;
import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.usecase.addbranch.AddBranchUseCase;
import co.com.bancolombia.usecase.addproduct.AddProductUseCase;
import co.com.bancolombia.usecase.createfranchise.CreateFranchiseUseCase;
import co.com.bancolombia.usecase.deleteproduct.DeleteProductUseCase;
import co.com.bancolombia.usecase.getmaxstockproducts.GetMaxStockProductsUseCase;
import co.com.bancolombia.usecase.getallfranchises.GetAllFranchisesUseCase;
import co.com.bancolombia.usecase.updatebranchname.UpdateBranchNameUseCase;
import co.com.bancolombia.usecase.updatefranchisename.UpdateFranchiseNameUseCase;
import co.com.bancolombia.usecase.updateproductname.UpdateProductNameUseCase;
import co.com.bancolombia.usecase.updateproductstock.UpdateProductStockUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private UpdateProductNameUseCase updateProductNameUseCase;
    
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
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
                updateProductNameUseCase
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
}
