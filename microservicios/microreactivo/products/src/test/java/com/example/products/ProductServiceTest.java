package com.example.products;

import com.example.products.application.ProductNotFoundException;
import com.example.products.application.ProductService;
import com.example.products.application.ProductServiceImpl;
import com.example.products.domain.Product;
import com.example.products.repo.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    
    @Mock
    private ProductRepository repository;
    
    private ProductService service;
    
    @BeforeEach
    void setUp() {
        service = new ProductServiceImpl(repository);
    }
    
    @Test
    void getAllProducts_shouldReturnAllProducts() {
        Product product1 = new Product(1L, "Product 1", BigDecimal.valueOf(10.0), BigDecimal.valueOf(5));
        Product product2 = new Product(2L, "Product 2", BigDecimal.valueOf(20.0), BigDecimal.valueOf(10));
        
        when(repository.findAll()).thenReturn(Flux.just(product1, product2));
        
        StepVerifier.create(service.getAllProducts())
                .expectNext(product1, product2)
                .verifyComplete();
    }
    
    @Test
    void getProductById_withValidId_shouldReturnProduct() {
        Product product = new Product(1L, "Product 1", BigDecimal.valueOf(10.0), BigDecimal.valueOf(5));
        when(repository.findById(1L)).thenReturn(Mono.just(product));
        
        StepVerifier.create(service.getProductById(1L))
                .expectNext(product)
                .verifyComplete();
    }
    
    @Test
    void getProductById_withNullId_shouldReturnError() {
        StepVerifier.create(service.getProductById(null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
    
    @Test
    void getProductById_withNegativeId_shouldReturnError() {
        StepVerifier.create(service.getProductById(-1L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
    
    @Test
    void getProductById_withZeroId_shouldReturnError() {
        StepVerifier.create(service.getProductById(0L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
    
    @Test
    void getProductById_whenNotFound_shouldReturnError() {
        when(repository.findById(999L)).thenReturn(Mono.empty());
        
        StepVerifier.create(service.getProductById(999L))
                .expectError(ProductNotFoundException.class)
                .verify();
    }
    
    @Test
    void createProduct_withValidProduct_shouldSaveProduct() {
        Product input = new Product(null, "New Product", BigDecimal.valueOf(15.0), BigDecimal.valueOf(8));
        Product saved = new Product(1L, "New Product", BigDecimal.valueOf(15.0), BigDecimal.valueOf(8));
        
        when(repository.save(any(Product.class))).thenReturn(Mono.just(saved));
        
        StepVerifier.create(service.createProduct(input))
                .expectNext(saved)
                .verifyComplete();
    }
    
    @Test
    void createProduct_withNullProduct_shouldReturnError() {
        StepVerifier.create(service.createProduct(null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
    
    @Test
    void createProduct_withNullName_shouldReturnError() {
        Product product = new Product(null, null, BigDecimal.valueOf(10.0), BigDecimal.valueOf(5));
        
        StepVerifier.create(service.createProduct(product))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
    
    @Test
    void createProduct_withEmptyName_shouldReturnError() {
        Product product = new Product(null, "   ", BigDecimal.valueOf(10.0), BigDecimal.valueOf(5));
        
        StepVerifier.create(service.createProduct(product))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
    
    @Test
    void createProduct_withNullPrice_shouldReturnError() {
        Product product = new Product(null, "Product", null, BigDecimal.valueOf(5));
        
        StepVerifier.create(service.createProduct(product))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
    
    @Test
    void createProduct_withNegativePrice_shouldReturnError() {
        Product product = new Product(null, "Product", BigDecimal.valueOf(-10.0), BigDecimal.valueOf(5));
        
        StepVerifier.create(service.createProduct(product))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
    
    @Test
    void createProduct_withZeroPrice_shouldReturnError() {
        Product product = new Product(null, "Product", BigDecimal.ZERO, BigDecimal.valueOf(5));
        
        StepVerifier.create(service.createProduct(product))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
    
    @Test
    void createProduct_withNullStock_shouldReturnError() {
        Product product = new Product(null, "Product", BigDecimal.valueOf(10.0), null);
        
        StepVerifier.create(service.createProduct(product))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
    
    @Test
    void createProduct_withNegativeStock_shouldReturnError() {
        Product product = new Product(null, "Product", BigDecimal.valueOf(10.0), BigDecimal.valueOf(-5));
        
        StepVerifier.create(service.createProduct(product))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
    
    @Test
    void updateProduct_withValidProduct_shouldUpdateProduct() {
        Product input = new Product(null, "Updated Product", BigDecimal.valueOf(25.0), BigDecimal.valueOf(15));
        Product updated = new Product(1L, "Updated Product", BigDecimal.valueOf(25.0), BigDecimal.valueOf(15));
        
        when(repository.existsById(1L)).thenReturn(Mono.just(true));
        when(repository.save(any(Product.class))).thenReturn(Mono.just(updated));
        
        StepVerifier.create(service.updateProduct(1L, input))
                .expectNext(updated)
                .verifyComplete();
    }
    
    @Test
    void updateProduct_whenNotFound_shouldReturnError() {
        Product product = new Product(null, "Product", BigDecimal.valueOf(10.0), BigDecimal.valueOf(5));
        
        when(repository.existsById(999L)).thenReturn(Mono.just(false));
        
        StepVerifier.create(service.updateProduct(999L, product))
                .expectError(ProductNotFoundException.class)
                .verify();
    }
    
    @Test
    void updateProduct_withInvalidId_shouldReturnError() {
        Product product = new Product(null, "Product", BigDecimal.valueOf(10.0), BigDecimal.valueOf(5));
        
        StepVerifier.create(service.updateProduct(-1L, product))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
    
    @Test
    void deleteProduct_withValidId_shouldDeleteProduct() {
        when(repository.existsById(1L)).thenReturn(Mono.just(true));
        when(repository.deleteById(1L)).thenReturn(Mono.empty());
        
        StepVerifier.create(service.deleteProduct(1L))
                .verifyComplete();
    }
    
    @Test
    void deleteProduct_whenNotFound_shouldReturnError() {
        when(repository.existsById(999L)).thenReturn(Mono.just(false));
        
        StepVerifier.create(service.deleteProduct(999L))
                .expectError(ProductNotFoundException.class)
                .verify();
    }
    
    @Test
    void deleteProduct_withInvalidId_shouldReturnError() {
        StepVerifier.create(service.deleteProduct(-1L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}
