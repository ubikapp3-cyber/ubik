package com.example.products.web;

import com.example.products.application.ProductNotFoundException;
import com.example.products.application.ProductService;
import com.example.products.domain.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * Handler for product-related HTTP requests.
 * This is part of the adapter layer in hexagonal architecture.
 */
@Component
public class ProductHandler {
    
    private final ProductService productService;
    
    public ProductHandler(ProductService productService) {
        this.productService = productService;
    }
    
    public Mono<ServerResponse> all(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productService.getAllProducts(), Product.class);
    }
    
    public Mono<ServerResponse> byId(ServerRequest request) {
        return parseId(request)
                .flatMap(productService::getProductById)
                .flatMap(product -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(product))
                .onErrorResume(ProductNotFoundException.class, 
                        e -> ServerResponse.notFound().build())
                .onErrorResume(IllegalArgumentException.class, 
                        e -> ServerResponse.badRequest().bodyValue(new ErrorResponse(e.getMessage())));
    }
    
    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(Product.class)
                .flatMap(productService::createProduct)
                .flatMap(product -> ServerResponse
                        .created(URI.create("/products/" + product.id()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(product))
                .onErrorResume(IllegalArgumentException.class, 
                        e -> ServerResponse.badRequest().bodyValue(new ErrorResponse(e.getMessage())))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .bodyValue(new ErrorResponse("An error occurred while creating the product")));
    }
    
    public Mono<ServerResponse> update(ServerRequest request) {
        return parseId(request)
                .flatMap(id -> request.bodyToMono(Product.class)
                        .flatMap(product -> productService.updateProduct(id, product)))
                .flatMap(product -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(product))
                .onErrorResume(ProductNotFoundException.class, 
                        e -> ServerResponse.notFound().build())
                .onErrorResume(IllegalArgumentException.class, 
                        e -> ServerResponse.badRequest().bodyValue(new ErrorResponse(e.getMessage())))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .bodyValue(new ErrorResponse("An error occurred while updating the product")));
    }
    
    public Mono<ServerResponse> delete(ServerRequest request) {
        return parseId(request)
                .flatMap(productService::deleteProduct)
                .then(ServerResponse.noContent().build())
                .onErrorResume(ProductNotFoundException.class, 
                        e -> ServerResponse.notFound().build())
                .onErrorResume(IllegalArgumentException.class, 
                        e -> ServerResponse.badRequest().bodyValue(new ErrorResponse(e.getMessage())));
    }
    
    /**
     * Parses the ID from the request path variable.
     * Returns an error if the ID is not a valid Long.
     */
    private Mono<Long> parseId(ServerRequest request) {
        try {
            String idParam = request.pathVariable("id");
            Long id = Long.parseLong(idParam);
            return Mono.just(id);
        } catch (NumberFormatException e) {
            return Mono.error(new IllegalArgumentException("Invalid product ID format"));
        }
    }
    
    /**
     * Simple error response record for consistent error messages.
     */
    private record ErrorResponse(String message) {}
}
