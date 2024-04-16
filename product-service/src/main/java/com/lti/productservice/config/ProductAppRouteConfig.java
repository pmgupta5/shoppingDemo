package com.lti.productservice.config;


import com.lti.productservice.handler.ProductHandler;
import com.lti.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class ProductAppRouteConfig {

    @Autowired
    private ProductHandler productHandler;
    @Bean
    public WebProperties.Resources resources(){
        return new WebProperties.Resources();
    }

    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
       /* return RouterFunctions.route()
                .GET("/route/api/product", productService::getProducts)
                .GET("/route/api/product/{productId}",productService::getProductById)
                .build();
        */
        return RouterFunctions.route()
                .GET("/route/api/product", productHandler::getProducts)
                .GET("/route/api/product/{productId:[0-9]+}", productHandler::getProductById)
                .PUT("/route/api/product/updateQty/{productId:[0-9]+}", productHandler::updateProductQty)
                .PUT("/route/api/product/updateQtys/{productId:[0-9]+}", productHandler::updateProductQtys)
                .build();
    }
}
