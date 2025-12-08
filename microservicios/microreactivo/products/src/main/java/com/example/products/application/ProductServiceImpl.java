package com.example.products.application;

import com.example.products.domain.Product;
import com.example.products.repo.ProductRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Service implementation for product operations.
 * This is an adapter in the hexagonal architecture, implementing the port.
 */
@Service
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository repository;
    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    
    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public Flux<Product> getAllProducts() {
        return repository.findAll()
                .timeout(TIMEOUT);
    }
    
    @Override
    public Mono<Product> getProductById(Long id) {
        if (id == null || id <= 0) {
            return Mono.error(new IllegalArgumentException("Invalid product ID: " + id));
        }
        
        return repository.findById(id)
                .timeout(TIMEOUT)
                .switchIfEmpty(Mono.error(new ProductNotFoundException("Product not found with id: " + id)));
    }
    
    @Override
    public Mono<Product> createProduct(Product product) {
        if (product == null) {
            return Mono.error(new IllegalArgumentException("Product cannot be null"));
        }
        
        if (product.name() == null || product.name().trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Product name is required"));
        }
        
        if (product.price() == null || product.price().signum() <= 0) {
            return Mono.error(new IllegalArgumentException("Product price must be positive"));
        }
        
        if (product.stock() == null || product.stock().signum() < 0) {
            return Mono.error(new IllegalArgumentException("Product stock cannot be negative"));
        }
        
        // Create new product with null ID to let the database generate it
        Product newProduct = new Product(null, product.name(), product.price(), product.stock());
        
        return repository.save(newProduct)
                .timeout(TIMEOUT);
    }
    
    @Override
    public Mono<Product> updateProduct(Long id, Product product) {
        if (id == null || id <= 0) {
            return Mono.error(new IllegalArgumentException("Invalid product ID: " + id));
        }
        
        if (product == null) {
            return Mono.error(new IllegalArgumentException("Product cannot be null"));
        }
        
        if (product.name() == null || product.name().trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Product name is required"));
        }
        
        if (product.price() == null || product.price().signum() <= 0) {
            return Mono.error(new IllegalArgumentException("Product price must be positive"));
        }
        
        if (product.stock() == null || product.stock().signum() < 0) {
            return Mono.error(new IllegalArgumentException("Product stock cannot be negative"));
        }
        
        return repository.existsById(id)
                .timeout(TIMEOUT)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new ProductNotFoundException("Product not found with id: " + id));
                    }
                    
                    Product updatedProduct = new Product(id, product.name(), product.price(), product.stock());
                    return repository.save(updatedProduct);
                })
                .timeout(TIMEOUT);
    }
    
    @Override
    public Mono<Void> deleteProduct(Long id) {
        if (id == null || id <= 0) {
            return Mono.error(new IllegalArgumentException("Invalid product ID: " + id));
        }
        
        return repository.existsById(id)
                .timeout(TIMEOUT)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new ProductNotFoundException("Product not found with id: " + id));
                    }
                    return repository.deleteById(id);
                })
                .timeout(TIMEOUT);
    }
}
