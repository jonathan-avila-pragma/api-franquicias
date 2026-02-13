package co.com.bancolombia.api;

import co.com.bancolombia.model.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(Constants.API_BASE_PATH), handler::createFranchise)
                .andRoute(POST(Constants.PATH_BRANCHES), handler::addBranch)
                .andRoute(POST(Constants.PATH_PRODUCTS), handler::addProduct)
                .andRoute(DELETE(Constants.PATH_PRODUCT_ID), handler::deleteProduct)
                .andRoute(PUT(Constants.PATH_STOCK), handler::updateProductStock)
                .andRoute(GET(Constants.PATH_MAX_STOCK), handler::getMaxStockProducts)
                .andRoute(PUT(Constants.PATH_ID), handler::updateFranchiseName)
                .andRoute(PUT(Constants.PATH_BRANCH_ID), handler::updateBranchName)
                .andRoute(PUT(Constants.PATH_PRODUCT_ID), handler::updateProductName);
    }
}
