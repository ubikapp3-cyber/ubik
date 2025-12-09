package com.ubik.usermanagement.domain.service;

import com.ubik.usermanagement.domain.exception.ResourceNotFoundException;
import com.ubik.usermanagement.domain.exception.ValidationException;
import com.ubik.usermanagement.domain.model.Motel;
import com.ubik.usermanagement.domain.port.out.MotelRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MotelService Unit Tests")
class MotelServiceTest {

    @Mock
    private MotelRepositoryPort motelRepositoryPort;

    @InjectMocks
    private MotelService motelService;

    private Motel validMotel;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        validMotel = new Motel(
                1L,
                "Hotel Paradise",
                "Calle 123 #45-67",
                "1234567890",
                "Un lugar hermoso",
                "Bogotá",
                100L,
                now,
                Arrays.asList("url1.jpg", "url2.jpg")
        );
    }

    // ==================== CREATE MOTEL TESTS ====================

    @Test
    @DisplayName("createMotel - Success: should create motel with valid data")
    void createMotel_Success() {
        // Given
        when(motelRepositoryPort.save(any(Motel.class))).thenReturn(Mono.just(validMotel));

        // When & Then
        StepVerifier.create(motelService.createMotel(validMotel))
                .expectNext(validMotel)
                .verifyComplete();

        verify(motelRepositoryPort, times(1)).save(any(Motel.class));
    }

    @Test
    @DisplayName("createMotel - Error: should fail when name is null")
    void createMotel_NullName_ShouldFail() {
        // Given
        Motel invalidMotel = new Motel(
                null, null, "Address", "123", "Description", "City", 1L, now, null
        );

        // When & Then
        StepVerifier.create(motelService.createMotel(invalidMotel))
                .expectErrorMatches(error -> error instanceof ValidationException &&
                        error.getMessage().contains("nombre del motel es requerido"))
                .verify();

        verify(motelRepositoryPort, never()).save(any(Motel.class));
    }

    @Test
    @DisplayName("createMotel - Error: should fail when name is empty")
    void createMotel_EmptyName_ShouldFail() {
        // Given
        Motel invalidMotel = new Motel(
                null, "   ", "Address", "123", "Description", "City", 1L, now, null
        );

        // When & Then
        StepVerifier.create(motelService.createMotel(invalidMotel))
                .expectErrorMatches(error -> error instanceof ValidationException &&
                        error.getMessage().contains("nombre del motel es requerido"))
                .verify();

        verify(motelRepositoryPort, never()).save(any(Motel.class));
    }

    @Test
    @DisplayName("createMotel - Error: should fail when address is null")
    void createMotel_NullAddress_ShouldFail() {
        // Given
        Motel invalidMotel = new Motel(
                null, "Name", null, "123", "Description", "City", 1L, now, null
        );

        // When & Then
        StepVerifier.create(motelService.createMotel(invalidMotel))
                .expectErrorMatches(error -> error instanceof ValidationException &&
                        error.getMessage().contains("dirección del motel es requerida"))
                .verify();

        verify(motelRepositoryPort, never()).save(any(Motel.class));
    }

    @Test
    @DisplayName("createMotel - Error: should fail when address is empty")
    void createMotel_EmptyAddress_ShouldFail() {
        // Given
        Motel invalidMotel = new Motel(
                null, "Name", "  ", "123", "Description", "City", 1L, now, null
        );

        // When & Then
        StepVerifier.create(motelService.createMotel(invalidMotel))
                .expectErrorMatches(error -> error instanceof ValidationException &&
                        error.getMessage().contains("dirección del motel es requerida"))
                .verify();

        verify(motelRepositoryPort, never()).save(any(Motel.class));
    }

    @Test
    @DisplayName("createMotel - Error: should fail when city is null")
    void createMotel_NullCity_ShouldFail() {
        // Given
        Motel invalidMotel = new Motel(
                null, "Name", "Address", "123", "Description", null, 1L, now, null
        );

        // When & Then
        StepVerifier.create(motelService.createMotel(invalidMotel))
                .expectErrorMatches(error -> error instanceof ValidationException &&
                        error.getMessage().contains("ciudad del motel es requerida"))
                .verify();

        verify(motelRepositoryPort, never()).save(any(Motel.class));
    }

    @Test
    @DisplayName("createMotel - Error: should fail when city is empty")
    void createMotel_EmptyCity_ShouldFail() {
        // Given
        Motel invalidMotel = new Motel(
                null, "Name", "Address", "123", "Description", "", 1L, now, null
        );

        // When & Then
        StepVerifier.create(motelService.createMotel(invalidMotel))
                .expectErrorMatches(error -> error instanceof ValidationException &&
                        error.getMessage().contains("ciudad del motel es requerida"))
                .verify();

        verify(motelRepositoryPort, never()).save(any(Motel.class));
    }

    @Test
    @DisplayName("createMotel - Edge Case: should fail when imageUrls exceeds limit of 10")
    void createMotel_TooManyImages_ShouldFail() {
        // Given - 11 images
        List<String> tooManyImages = Arrays.asList(
                "url1.jpg", "url2.jpg", "url3.jpg", "url4.jpg", "url5.jpg",
                "url6.jpg", "url7.jpg", "url8.jpg", "url9.jpg", "url10.jpg", "url11.jpg"
        );
        Motel invalidMotel = new Motel(
                null, "Name", "Address", "123", "Description", "City", 1L, now, tooManyImages
        );

        // When & Then
        StepVerifier.create(motelService.createMotel(invalidMotel))
                .expectErrorMatches(error -> error instanceof ValidationException &&
                        error.getMessage().contains("No se pueden agregar más de 10 imágenes"))
                .verify();

        verify(motelRepositoryPort, never()).save(any(Motel.class));
    }

    @Test
    @DisplayName("createMotel - Edge Case: should succeed with exactly 10 images")
    void createMotel_ExactlyTenImages_ShouldSucceed() {
        // Given - exactly 10 images
        List<String> tenImages = Arrays.asList(
                "url1.jpg", "url2.jpg", "url3.jpg", "url4.jpg", "url5.jpg",
                "url6.jpg", "url7.jpg", "url8.jpg", "url9.jpg", "url10.jpg"
        );
        Motel motel = new Motel(
                null, "Name", "Address", "123", "Description", "City", 1L, now, tenImages
        );
        when(motelRepositoryPort.save(any(Motel.class))).thenReturn(Mono.just(motel));

        // When & Then
        StepVerifier.create(motelService.createMotel(motel))
                .expectNext(motel)
                .verifyComplete();

        verify(motelRepositoryPort, times(1)).save(any(Motel.class));
    }

    @Test
    @DisplayName("createMotel - Edge Case: should succeed with null imageUrls")
    void createMotel_NullImageUrls_ShouldSucceed() {
        // Given
        Motel motel = new Motel(
                null, "Name", "Address", "123", "Description", "City", 1L, now, null
        );
        when(motelRepositoryPort.save(any(Motel.class))).thenReturn(Mono.just(motel));

        // When & Then
        StepVerifier.create(motelService.createMotel(motel))
                .expectNext(motel)
                .verifyComplete();

        verify(motelRepositoryPort, times(1)).save(any(Motel.class));
    }

    @Test
    @DisplayName("createMotel - Edge Case: should succeed with empty imageUrls list")
    void createMotel_EmptyImageUrls_ShouldSucceed() {
        // Given
        Motel motel = new Motel(
                null, "Name", "Address", "123", "Description", "City", 1L, now, Collections.emptyList()
        );
        when(motelRepositoryPort.save(any(Motel.class))).thenReturn(Mono.just(motel));

        // When & Then
        StepVerifier.create(motelService.createMotel(motel))
                .expectNext(motel)
                .verifyComplete();

        verify(motelRepositoryPort, times(1)).save(any(Motel.class));
    }

    // ==================== GET MOTEL BY ID TESTS ====================

    @Test
    @DisplayName("getMotelById - Success: should return motel when found")
    void getMotelById_Success() {
        // Given
        when(motelRepositoryPort.findById(1L)).thenReturn(Mono.just(validMotel));

        // When & Then
        StepVerifier.create(motelService.getMotelById(1L))
                .expectNext(validMotel)
                .verifyComplete();

        verify(motelRepositoryPort, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getMotelById - Error: should throw ResourceNotFoundException when not found")
    void getMotelById_NotFound_ShouldFail() {
        // Given
        when(motelRepositoryPort.findById(999L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(motelService.getMotelById(999L))
                .expectErrorMatches(error -> error instanceof ResourceNotFoundException &&
                        error.getMessage().contains("999"))
                .verify();

        verify(motelRepositoryPort, times(1)).findById(999L);
    }

    // ==================== GET ALL MOTELS TESTS ====================

    @Test
    @DisplayName("getAllMotels - Success: should return all motels")
    void getAllMotels_Success() {
        // Given
        Motel motel2 = new Motel(2L, "Hotel 2", "Address 2", "987", "Desc", "Cali", 101L, now, null);
        when(motelRepositoryPort.findAll()).thenReturn(Flux.just(validMotel, motel2));

        // When & Then
        StepVerifier.create(motelService.getAllMotels())
                .expectNext(validMotel)
                .expectNext(motel2)
                .verifyComplete();

        verify(motelRepositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllMotels - Success: should return empty flux when no motels exist")
    void getAllMotels_Empty_Success() {
        // Given
        when(motelRepositoryPort.findAll()).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(motelService.getAllMotels())
                .verifyComplete();

        verify(motelRepositoryPort, times(1)).findAll();
    }

    // ==================== GET MOTELS BY CITY TESTS ====================

    @Test
    @DisplayName("getMotelsByCity - Success: should return motels for valid city")
    void getMotelsByCity_Success() {
        // Given
        when(motelRepositoryPort.findByCity("Bogotá")).thenReturn(Flux.just(validMotel));

        // When & Then
        StepVerifier.create(motelService.getMotelsByCity("Bogotá"))
                .expectNext(validMotel)
                .verifyComplete();

        verify(motelRepositoryPort, times(1)).findByCity("Bogotá");
    }

    @Test
    @DisplayName("getMotelsByCity - Success: should trim city name")
    void getMotelsByCity_WithSpaces_Success() {
        // Given
        when(motelRepositoryPort.findByCity("Bogotá")).thenReturn(Flux.just(validMotel));

        // When & Then
        StepVerifier.create(motelService.getMotelsByCity("  Bogotá  "))
                .expectNext(validMotel)
                .verifyComplete();

        verify(motelRepositoryPort, times(1)).findByCity("Bogotá");
    }

    @Test
    @DisplayName("getMotelsByCity - Error: should fail when city is null")
    void getMotelsByCity_NullCity_ShouldFail() {
        // When & Then
        StepVerifier.create(motelService.getMotelsByCity(null))
                .expectErrorMatches(error -> error instanceof ValidationException &&
                        error.getMessage().contains("ciudad es requerida"))
                .verify();

        verify(motelRepositoryPort, never()).findByCity(anyString());
    }

    @Test
    @DisplayName("getMotelsByCity - Error: should fail when city is empty")
    void getMotelsByCity_EmptyCity_ShouldFail() {
        // When & Then
        StepVerifier.create(motelService.getMotelsByCity(""))
                .expectErrorMatches(error -> error instanceof ValidationException &&
                        error.getMessage().contains("ciudad es requerida"))
                .verify();

        verify(motelRepositoryPort, never()).findByCity(anyString());
    }

    @Test
    @DisplayName("getMotelsByCity - Error: should fail when city is blank")
    void getMotelsByCity_BlankCity_ShouldFail() {
        // When & Then
        StepVerifier.create(motelService.getMotelsByCity("   "))
                .expectErrorMatches(error -> error instanceof ValidationException &&
                        error.getMessage().contains("ciudad es requerida"))
                .verify();

        verify(motelRepositoryPort, never()).findByCity(anyString());
    }

    @Test
    @DisplayName("getMotelsByCity - Success: should return empty flux when no motels in city")
    void getMotelsByCity_NoResults_Success() {
        // Given
        when(motelRepositoryPort.findByCity("UnknownCity")).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(motelService.getMotelsByCity("UnknownCity"))
                .verifyComplete();

        verify(motelRepositoryPort, times(1)).findByCity("UnknownCity");
    }

    // ==================== UPDATE MOTEL TESTS ====================

    @Test
    @DisplayName("updateMotel - Success: should update motel when found")
    void updateMotel_Success() {
        // Given
        Motel updatedInfo = new Motel(
                1L, "Updated Name", "Updated Address", "999", "New Desc", "Medellín", 100L, now, Arrays.asList("new.jpg")
        );
        when(motelRepositoryPort.findById(1L)).thenReturn(Mono.just(validMotel));
        when(motelRepositoryPort.update(any(Motel.class))).thenReturn(Mono.just(updatedInfo));

        // When & Then
        StepVerifier.create(motelService.updateMotel(1L, updatedInfo))
                .expectNext(updatedInfo)
                .verifyComplete();

        verify(motelRepositoryPort, times(1)).findById(1L);
        verify(motelRepositoryPort, times(1)).update(any(Motel.class));
    }

    @Test
    @DisplayName("updateMotel - Success: should preserve propertyId and dateCreated")
    void updateMotel_PreservesImmutableFields() {
        // Given
        Motel updatedInfo = new Motel(
                1L, "Updated Name", "Updated Address", "999", "New Desc", "Medellín", 999L, LocalDateTime.now(), null
        );
        when(motelRepositoryPort.findById(1L)).thenReturn(Mono.just(validMotel));
        when(motelRepositoryPort.update(argThat(motel ->
                motel.propertyId().equals(validMotel.propertyId()) &&
                        motel.dateCreated().equals(validMotel.dateCreated())
        ))).thenReturn(Mono.just(updatedInfo));

        // When & Then
        StepVerifier.create(motelService.updateMotel(1L, updatedInfo))
                .expectNextMatches(motel -> motel != null)
                .verifyComplete();

        verify(motelRepositoryPort, times(1)).findById(1L);
        verify(motelRepositoryPort, times(1)).update(any(Motel.class));
    }

    @Test
    @DisplayName("updateMotel - Error: should fail when motel not found")
    void updateMotel_NotFound_ShouldFail() {
        // Given
        when(motelRepositoryPort.findById(999L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(motelService.updateMotel(999L, validMotel))
                .expectErrorMatches(error -> error instanceof ResourceNotFoundException &&
                        error.getMessage().contains("999"))
                .verify();

        verify(motelRepositoryPort, times(1)).findById(999L);
        verify(motelRepositoryPort, never()).update(any(Motel.class));
    }

    @Test
    @DisplayName("updateMotel - Error: should fail with invalid data")
    void updateMotel_InvalidData_ShouldFail() {
        // Given
        Motel invalidUpdate = new Motel(
                1L, "", "Address", "999", "Desc", "City", 100L, now, null
        );
        when(motelRepositoryPort.findById(1L)).thenReturn(Mono.just(validMotel));

        // When & Then
        StepVerifier.create(motelService.updateMotel(1L, invalidUpdate))
                .expectErrorMatches(error -> error instanceof ValidationException)
                .verify();

        verify(motelRepositoryPort, times(1)).findById(1L);
        verify(motelRepositoryPort, never()).update(any(Motel.class));
    }

    // ==================== DELETE MOTEL TESTS ====================

    @Test
    @DisplayName("deleteMotel - Success: should delete motel when exists")
    void deleteMotel_Success() {
        // Given
        when(motelRepositoryPort.existsById(1L)).thenReturn(Mono.just(true));
        when(motelRepositoryPort.deleteById(1L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(motelService.deleteMotel(1L))
                .verifyComplete();

        verify(motelRepositoryPort, times(1)).existsById(1L);
        verify(motelRepositoryPort, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteMotel - Error: should fail when motel not found")
    void deleteMotel_NotFound_ShouldFail() {
        // Given
        when(motelRepositoryPort.existsById(999L)).thenReturn(Mono.just(false));

        // When & Then
        StepVerifier.create(motelService.deleteMotel(999L))
                .expectErrorMatches(error -> error instanceof ResourceNotFoundException &&
                        error.getMessage().contains("999"))
                .verify();

        verify(motelRepositoryPort, times(1)).existsById(999L);
        verify(motelRepositoryPort, never()).deleteById(anyLong());
    }
}
