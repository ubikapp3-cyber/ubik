package com.example.products.application;

import com.example.products.domain.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service interface for product operations following hexagonal architecture.
 * This is a port in the hexagonal architecture.
 */
public interface ProductService {
    
    Flux<Product> getAllProducts();
    
    Mono<Product> getProductById(Long id);
    
    Mono<Product> createProduct(Product product);
    
    Mono<Product> updateProduct(Long id, Product product);
    
    Mono<Void> deleteProduct(Long id);
}
