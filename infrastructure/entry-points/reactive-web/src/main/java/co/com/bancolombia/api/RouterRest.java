package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.*;
import co.com.bancolombia.model.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.*;

@Configuration
public class RouterRest {
    
    @Bean
    @RouterOperations({
        @RouterOperation(
            path = "/api/franchises",
            method = RequestMethod.POST,
            beanClass = Handler.class,
            beanMethod = "createFranchise",
                operation = @Operation(
                operationId = "createFranchise",
                summary = "Create a new franchise",
                tags = {"Franchises"},
                requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = FranchiseRequest.class))),
                responses = {
                    @ApiResponse(
                        responseCode = "201",
                        description = "Franchise created successfully",
                        content = @Content(schema = @Schema(implementation = FranchiseResponseDto.class))
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request",
                        content = @Content(schema = @Schema(implementation = ResponseErrorDto.class))
                    ),
                    @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = ResponseErrorDto.class))
                    )
                }
            )
        ),
        @RouterOperation(
            path = "/api/franchises/{franchiseId}",
            method = RequestMethod.PUT,
            beanClass = Handler.class,
            beanMethod = "updateFranchiseName",
                operation = @Operation(
                operationId = "updateFranchiseName",
                summary = "Update franchise name",
                tags = {"Franchises"},
                parameters = {@Parameter(in = ParameterIn.PATH, name = "franchiseId", description = "Franchise ID")},
                requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UpdateNameRequest.class))),
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Name updated successfully",
                        content = @Content(schema = @Schema(implementation = FranchiseResponseDto.class))
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request",
                        content = @Content(schema = @Schema(implementation = ResponseErrorDto.class))
                    ),
                    @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = ResponseErrorDto.class))
                    )
                }
            )
        ),
        @RouterOperation(
            path = "/api/franchises",
            method = RequestMethod.GET,
            beanClass = Handler.class,
            beanMethod = "getAllFranchises",
                operation = @Operation(
                operationId = "getAllFranchises",
                summary = "Get all franchises",
                tags = {"Franchises"},
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "List of franchises",
                        content = @Content(schema = @Schema(implementation = FranchiseListResponseDto.class))
                    ),
                    @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = ResponseErrorDto.class))
                    )
                }
            )
        ),
        @RouterOperation(
            path = "/api/franchises/{franchiseId}/branches",
            method = RequestMethod.POST,
            beanClass = Handler.class,
            beanMethod = "addBranch",
                operation = @Operation(
                operationId = "addBranch",
                summary = "Add branch to a franchise",
                tags = {"Branches"},
                parameters = {@Parameter(in = ParameterIn.PATH, name = "franchiseId", description = "Franchise ID")},
                requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = BranchRequest.class))),
                responses = {
                    @ApiResponse(
                        responseCode = "201",
                        description = "Branch added successfully",
                        content = @Content(schema = @Schema(implementation = BranchResponseDto.class))
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request",
                        content = @Content(schema = @Schema(implementation = ResponseErrorDto.class))
                    ),
                    @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = ResponseErrorDto.class))
                    )
                }
            )
        ),
        @RouterOperation(
            path = "/api/franchises/{franchiseId}/branches/{branchId}",
            method = RequestMethod.PUT,
            beanClass = Handler.class,
            beanMethod = "updateBranchName",
                operation = @Operation(
                operationId = "updateBranchName",
                summary = "Update branch name",
                tags = {"Branches"},
                parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "franchiseId", description = "Franchise ID"),
                    @Parameter(in = ParameterIn.PATH, name = "branchId", description = "Branch ID")
                },
                requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UpdateNameRequest.class))),
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Name updated successfully",
                        content = @Content(schema = @Schema(implementation = BranchResponseDto.class))
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request",
                        content = @Content(schema = @Schema(implementation = ResponseErrorDto.class))
                    ),
                    @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = ResponseErrorDto.class))
                    )
                }
            )
        ),
        @RouterOperation(
            path = "/api/franchises/{franchiseId}/branches/{branchId}/products",
            method = RequestMethod.POST,
            beanClass = Handler.class,
            beanMethod = "addProduct",
                operation = @Operation(
                operationId = "addProduct",
                summary = "Add product to a branch",
                tags = {"Products"},
                parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "franchiseId", description = "Franchise ID"),
                    @Parameter(in = ParameterIn.PATH, name = "branchId", description = "Branch ID")
                },
                requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = ProductRequest.class))),
                responses = {
                    @ApiResponse(
                        responseCode = "201",
                        description = "Product added successfully",
                        content = @Content(schema = @Schema(implementation = ProductResponseDto.class))
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request",
                        content = @Content(schema = @Schema(implementation = ResponseErrorDto.class))
                    ),
                    @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = ResponseErrorDto.class))
                    )
                }
            )
        ),
        @RouterOperation(
            path = "/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}",
            method = RequestMethod.DELETE,
            beanClass = Handler.class,
            beanMethod = "deleteProduct",
                operation = @Operation(
                operationId = "deleteProduct",
                summary = "Delete a product",
                tags = {"Products"},
                parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "franchiseId", description = "Franchise ID"),
                    @Parameter(in = ParameterIn.PATH, name = "branchId", description = "Branch ID"),
                    @Parameter(in = ParameterIn.PATH, name = "productId", description = "Product ID")
                },
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Product deleted successfully",
                        content = @Content(schema = @Schema(implementation = ResponseDto.class))
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request",
                        content = @Content(schema = @Schema(implementation = ResponseErrorDto.class))
                    ),
                    @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = ResponseErrorDto.class))
                    )
                }
            )
        ),
        @RouterOperation(
            path = "/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}",
            method = RequestMethod.PUT,
            beanClass = Handler.class,
            beanMethod = "updateProductName",
                operation = @Operation(
                operationId = "updateProductName",
                summary = "Update product name",
                tags = {"Products"},
                parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "franchiseId", description = "Franchise ID"),
                    @Parameter(in = ParameterIn.PATH, name = "branchId", description = "Branch ID"),
                    @Parameter(in = ParameterIn.PATH, name = "productId", description = "Product ID")
                },
                requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UpdateNameRequest.class))),
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Name updated successfully",
                        content = @Content(schema = @Schema(implementation = ProductResponseDto.class))
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request",
                        content = @Content(schema = @Schema(implementation = ResponseErrorDto.class))
                    ),
                    @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = ResponseErrorDto.class))
                    )
                }
            )
        ),
        @RouterOperation(
            path = "/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock",
            method = RequestMethod.PUT,
            beanClass = Handler.class,
            beanMethod = "updateProductStock",
                operation = @Operation(
                operationId = "updateProductStock",
                summary = "Update product stock",
                tags = {"Products"},
                parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "franchiseId", description = "Franchise ID"),
                    @Parameter(in = ParameterIn.PATH, name = "branchId", description = "Branch ID"),
                    @Parameter(in = ParameterIn.PATH, name = "productId", description = "Product ID")
                },
                requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UpdateStockRequest.class))),
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Stock updated successfully",
                        content = @Content(schema = @Schema(implementation = ProductResponseDto.class))
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request",
                        content = @Content(schema = @Schema(implementation = ResponseErrorDto.class))
                    ),
                    @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = ResponseErrorDto.class))
                    )
                }
            )
        ),
        @RouterOperation(
            path = "/api/franchises/{franchiseId}/max-stock-products",
            method = RequestMethod.GET,
            beanClass = Handler.class,
            beanMethod = "getMaxStockProducts",
                operation = @Operation(
                operationId = "getMaxStockProducts",
                summary = "Get products with maximum stock by branch",
                tags = {"Products"},
                parameters = {@Parameter(in = ParameterIn.PATH, name = "franchiseId", description = "Franchise ID")},
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "List of products with maximum stock",
                        content = @Content(schema = @Schema(implementation = ProductWithBranchListResponseDto.class))
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request",
                        content = @Content(schema = @Schema(implementation = ResponseErrorDto.class))
                    ),
                    @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = ResponseErrorDto.class))
                    )
                }
            )
        )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return nest(path(Constants.API_BASE_PATH),
                route(GET(""), handler::getAllFranchises)
                        .andRoute(POST(""), handler::createFranchise)
                        .andRoute(POST(Constants.PATH_BRANCHES), handler::addBranch)
                        .andRoute(POST(Constants.PATH_PRODUCTS), handler::addProduct)
                        .andRoute(DELETE(Constants.PATH_PRODUCT_ID), handler::deleteProduct)
                        .andRoute(PUT(Constants.PATH_STOCK), handler::updateProductStock)
                        .andRoute(GET(Constants.PATH_MAX_STOCK), handler::getMaxStockProducts)
                        .andRoute(PUT(Constants.PATH_ID), handler::updateFranchiseName)
                        .andRoute(PUT(Constants.PATH_BRANCH_ID), handler::updateBranchName)
                        .andRoute(PUT(Constants.PATH_PRODUCT_ID), handler::updateProductName));
    }
}
