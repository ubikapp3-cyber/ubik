package com.example.products.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ProductRouter {
    
    @Bean
    RouterFunction<ServerResponse> routes(ProductHandler handler) {
        return RouterFunctions.route(GET("/products"), handler::all)
                .andRoute(GET("/products/{id}"), handler::byId)
                .andRoute(POST("/products"), handler::create)
                .andRoute(PUT("/products/{id}"), handler::update)
                .andRoute(DELETE("/products/{id}"), handler::delete);
    }
}
