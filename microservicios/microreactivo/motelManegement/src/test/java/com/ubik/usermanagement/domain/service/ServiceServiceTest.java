package com.ubik.usermanagement.domain.service;

import com.ubik.usermanagement.domain.exception.BusinessRuleException;
import com.ubik.usermanagement.domain.exception.ResourceNotFoundException;
import com.ubik.usermanagement.domain.exception.ValidationException;
import com.ubik.usermanagement.domain.model.Service;
import com.ubik.usermanagement.domain.port.out.RoomRepositoryPort;
import com.ubik.usermanagement.domain.port.out.ServiceRepositoryPort;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ServiceService Unit Tests")
class ServiceServiceTest {

    @Mock
    private ServiceRepositoryPort serviceRepositoryPort;

    @Mock
    private RoomRepositoryPort roomRepositoryPort;

    @InjectMocks
    private ServiceService serviceService;

    private Service validService;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        validService = new Service(
                1L,
                "WiFi",
                "Internet de alta velocidad",
                "wifi-icon",
                now
        );
    }

    // ==================== CREATE SERVICE TESTS ====================

    @Test
    @DisplayName("createService - Success: should create service with valid data")
    void createService_Success() {
        // Given
        when(serviceRepositoryPort.existsByName("WiFi")).thenReturn(Mono.just(false));
        when(serviceRepositoryPort.save(any(Service.class))).thenReturn(Mono.just(validService));

        // When & Then
        StepVerifier.create(serviceService.createService(validService))
                .expectNext(validService)
                .verifyComplete();

        verify(serviceRepositoryPort, times(1)).existsByName("WiFi");
        verify(serviceRepositoryPort, times(1)).save(any(Service.class));
    }

    @Test
    @DisplayName("createService - Error: should fail when name is null")
    void createService_NullName_ShouldFail() {
        // Given
        Service invalidService = new Service(null, null, "Description", "icon", null);

        // When & Then
        StepVerifier.create(serviceService.createService(invalidService))
                .expectErrorMatches(error -> error instanceof ValidationException &&
                        error.getMessage().contains("nombre del servicio es requerido"))
                .verify();

        verify(serviceRepositoryPort, never()).existsByName(anyString());
        verify(serviceRepositoryPort, never()).save(any(Service.class));
    }

    @Test
    @DisplayName("createService - Error: should fail when name is empty")
    void createService_EmptyName_ShouldFail() {
        // Given
        Service invalidService = new Service(null, "   ", "Description", "icon", null);

        // When & Then
        StepVerifier.create(serviceService.createService(invalidService))
                .expectErrorMatches(error -> error instanceof ValidationException &&
                        error.getMessage().contains("nombre del servicio es requerido"))
                .verify();

        verify(serviceRepositoryPort, never()).existsByName(anyString());
        verify(serviceRepositoryPort, never()).save(any(Service.class));
    }

    @Test
    @DisplayName("createService - Error: should fail when service name already exists")
    void createService_DuplicateName_ShouldFail() {
        // Given
        when(serviceRepositoryPort.existsByName("WiFi")).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(serviceService.createService(validService))
                .expectErrorMatches(error -> error instanceof BusinessRuleException &&
                        error.getMessage().contains("Ya existe un servicio con el nombre: WiFi"))
                .verify();

        verify(serviceRepositoryPort, times(1)).existsByName("WiFi");
        verify(serviceRepositoryPort, never()).save(any(Service.class));
    }

    @Test
    @DisplayName("createService - Edge Case: should fail when name exceeds 50 characters")
    void createService_NameTooLong_ShouldFail() {
        // Given - 51 characters
        String longName = "a".repeat(51);
        Service invalidService = new Service(null, longName, "Description", "icon", null);

        // When & Then
        StepVerifier.create(serviceService.createService(invalidService))
                .expectErrorMatches(error -> error instanceof ValidationException &&
                        error.getMessage().contains("nombre del servicio no puede exceder 50 caracteres"))
                .verify();

        verify(serviceRepositoryPort, never()).save(any(Service.class));
    }

    @Test
    @DisplayName("createService - Edge Case: should succeed when name is exactly 50 characters")
    void createService_NameExactlyFiftyChars_ShouldSucceed() {
        // Given - exactly 50 characters
        String fiftyCharName = "a".repeat(50);
        Service service = new Service(null, fiftyCharName, "Description", "icon", null);
        when(serviceRepositoryPort.existsByName(fiftyCharName)).thenReturn(Mono.just(false));
        when(serviceRepositoryPort.save(any(Service.class))).thenReturn(Mono.just(service));

        // When & Then
        StepVerifier.create(serviceService.createService(service))
                .expectNext(service)
                .verifyComplete();

        verify(serviceRepositoryPort, times(1)).save(any(Service.class));
    }

    @Test
    @DisplayName("createService - Edge Case: should fail when description exceeds 255 characters")
    void createService_DescriptionTooLong_ShouldFail() {
        // Given - 256 characters
        String longDesc = "a".repeat(256);
        Service invalidService = new Service(null, "WiFi", longDesc, "icon", null);

        // When & Then
        StepVerifier.create(serviceService.createService(invalidService))
                .expectErrorMatches(error -> error instanceof ValidationException &&
                        error.getMessage().contains("descripción no puede exceder 255 caracteres"))
                .verify();

        verify(serviceRepositoryPort, never()).save(any(Service.class));
    }

    @Test
    @DisplayName("createService - Edge Case: should succeed when description is exactly 255 characters")
    void createService_DescriptionExactly255Chars_ShouldSucceed() {
        // Given - exactly 255 characters
        String desc = "a".repeat(255);
        Service service = new Service(null, "WiFi", desc, "icon", null);
        when(serviceRepositoryPort.existsByName("WiFi")).thenReturn(Mono.just(false));
        when(serviceRepositoryPort.save(any(Service.class))).thenReturn(Mono.just(service));

        // When & Then
        StepVerifier.create(serviceService.createService(service))
                .expectNext(service)
                .verifyComplete();

        verify(serviceRepositoryPort, times(1)).save(any(Service.class));
    }

    @Test
    @DisplayName("createService - Edge Case: should succeed with null description")
    void createService_NullDescription_ShouldSucceed() {
        // Given
        Service service = new Service(null, "WiFi", null, "icon", null);
        when(serviceRepositoryPort.existsByName("WiFi")).thenReturn(Mono.just(false));
        when(serviceRepositoryPort.save(any(Service.class))).thenReturn(Mono.just(service));

        // When & Then
        StepVerifier.create(serviceService.createService(service))
                .expectNext(service)
                .verifyComplete();

        verify(serviceRepositoryPort, times(1)).save(any(Service.class));
    }

    @Test
    @DisplayName("createService - Edge Case: should fail when icon exceeds 50 characters")
    void createService_IconTooLong_ShouldFail() {
        // Given - 51 characters
        String longIcon = "a".repeat(51);
        Service invalidService = new Service(null, "WiFi", "Description", longIcon, null);

        // When & Then
        StepVerifier.create(serviceService.createService(invalidService))
                .expectErrorMatches(error -> error instanceof ValidationException &&
                        error.getMessage().contains("icono no puede exceder 50 caracteres"))
                .verify();

        verify(serviceRepositoryPort, never()).save(any(Service.class));
    }

    @Test
    @DisplayName("createService - Edge Case: should succeed when icon is exactly 50 characters")
    void createService_IconExactlyFiftyChars_ShouldSucceed() {
        // Given - exactly 50 characters
        String fiftyCharIcon = "a".repeat(50);
        Service service = new Service(null, "WiFi", "Description", fiftyCharIcon, null);
        when(serviceRepositoryPort.existsByName("WiFi")).thenReturn(Mono.just(false));
        when(serviceRepositoryPort.save(any(Service.class))).thenReturn(Mono.just(service));

        // When & Then
        StepVerifier.create(serviceService.createService(service))
                .expectNext(service)
                .verifyComplete();

        verify(serviceRepositoryPort, times(1)).save(any(Service.class));
    }

    @Test
    @DisplayName("createService - Edge Case: should succeed with null icon")
    void createService_NullIcon_ShouldSucceed() {
        // Given
        Service service = new Service(null, "WiFi", "Description", null, null);
        when(serviceRepositoryPort.existsByName("WiFi")).thenReturn(Mono.just(false));
        when(serviceRepositoryPort.save(any(Service.class))).thenReturn(Mono.just(service));

        // When & Then
        StepVerifier.create(serviceService.createService(service))
                .expectNext(service)
                .verifyComplete();

        verify(serviceRepositoryPort, times(1)).save(any(Service.class));
    }

    // ==================== GET SERVICE BY ID TESTS ====================

    @Test
    @DisplayName("getServiceById - Success: should return service when found")
    void getServiceById_Success() {
        // Given
        when(serviceRepositoryPort.findById(1L)).thenReturn(Mono.just(validService));

        // When & Then
        StepVerifier.create(serviceService.getServiceById(1L))
                .expectNext(validService)
                .verifyComplete();

        verify(serviceRepositoryPort, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getServiceById - Error: should throw ResourceNotFoundException when not found")
    void getServiceById_NotFound_ShouldFail() {
        // Given
        when(serviceRepositoryPort.findById(999L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(serviceService.getServiceById(999L))
                .expectErrorMatches(error -> error instanceof ResourceNotFoundException &&
                        error.getMessage().contains("Servicio") &&
                        error.getMessage().contains("999"))
                .verify();

        verify(serviceRepositoryPort, times(1)).findById(999L);
    }

    // ==================== GET ALL SERVICES TESTS ====================

    @Test
    @DisplayName("getAllServices - Success: should return all services")
    void getAllServices_Success() {
        // Given
        Service service2 = new Service(2L, "TV Cable", "Television", "tv-icon", now);
        when(serviceRepositoryPort.findAll()).thenReturn(Flux.just(validService, service2));

        // When & Then
        StepVerifier.create(serviceService.getAllServices())
                .expectNext(validService)
                .expectNext(service2)
                .verifyComplete();

        verify(serviceRepositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllServices - Success: should return empty flux when no services exist")
    void getAllServices_Empty_Success() {
        // Given
        when(serviceRepositoryPort.findAll()).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(serviceService.getAllServices())
                .verifyComplete();

        verify(serviceRepositoryPort, times(1)).findAll();
    }

    // ==================== GET SERVICE BY NAME TESTS ====================

    @Test
    @DisplayName("getServiceByName - Success: should return service when found")
    void getServiceByName_Success() {
        // Given
        when(serviceRepositoryPort.findByName("WiFi")).thenReturn(Mono.just(validService));

        // When & Then
        StepVerifier.create(serviceService.getServiceByName("WiFi"))
                .expectNext(validService)
                .verifyComplete();

        verify(serviceRepositoryPort, times(1)).findByName("WiFi");
    }

    @Test
    @DisplayName("getServiceByName - Success: should trim name")
    void getServiceByName_WithSpaces_Success() {
        // Given
        when(serviceRepositoryPort.findByName("WiFi")).thenReturn(Mono.just(validService));

        // When & Then
        StepVerifier.create(serviceService.getServiceByName("  WiFi  "))
                .expectNext(validService)
                .verifyComplete();

        verify(serviceRepositoryPort, times(1)).findByName("WiFi");
    }

    @Test
    @DisplayName("getServiceByName - Error: should fail when name is null")
    void getServiceByName_NullName_ShouldFail() {
        // When & Then
        StepVerifier.create(serviceService.getServiceByName(null))
                .expectErrorMatches(error -> error instanceof ValidationException &&
                        error.getMessage().contains("nombre del servicio es requerido"))
                .verify();

        verify(serviceRepositoryPort, never()).findByName(anyString());
    }

    @Test
    @DisplayName("getServiceByName - Error: should fail when name is empty")
    void getServiceByName_EmptyName_ShouldFail() {
        // When & Then
        StepVerifier.create(serviceService.getServiceByName(""))
                .expectErrorMatches(error -> error instanceof ValidationException &&
                        error.getMessage().contains("nombre del servicio es requerido"))
                .verify();

        verify(serviceRepositoryPort, never()).findByName(anyString());
    }

    @Test
    @DisplayName("getServiceByName - Error: should fail when name is blank")
    void getServiceByName_BlankName_ShouldFail() {
        // When & Then
        StepVerifier.create(serviceService.getServiceByName("   "))
                .expectErrorMatches(error -> error instanceof ValidationException &&
                        error.getMessage().contains("nombre del servicio es requerido"))
                .verify();

        verify(serviceRepositoryPort, never()).findByName(anyString());
    }

    @Test
    @DisplayName("getServiceByName - Error: should fail when service not found")
    void getServiceByName_NotFound_ShouldFail() {
        // Given
        when(serviceRepositoryPort.findByName("Unknown")).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(serviceService.getServiceByName("Unknown"))
                .expectErrorMatches(error -> error instanceof ResourceNotFoundException &&
                        error.getMessage().contains("Servicio no encontrado con nombre: Unknown"))
                .verify();

        verify(serviceRepositoryPort, times(1)).findByName("Unknown");
    }

    // ==================== UPDATE SERVICE TESTS ====================

    @Test
    @DisplayName("updateService - Success: should update service when found and name not changed")
    void updateService_SameName_Success() {
        // Given
        Service updatedInfo = new Service(1L, "WiFi", "New description", "new-icon", now);
        when(serviceRepositoryPort.findById(1L)).thenReturn(Mono.just(validService));
        when(serviceRepositoryPort.update(any(Service.class))).thenReturn(Mono.just(updatedInfo));

        // When & Then
        StepVerifier.create(serviceService.updateService(1L, updatedInfo))
                .expectNext(updatedInfo)
                .verifyComplete();

        verify(serviceRepositoryPort, times(1)).findById(1L);
        verify(serviceRepositoryPort, never()).existsByName(anyString());
        verify(serviceRepositoryPort, times(1)).update(any(Service.class));
    }

    @Test
    @DisplayName("updateService - Success: should update service with new name when not duplicate")
    void updateService_NewName_Success() {
        // Given
        Service updatedInfo = new Service(1L, "NewWiFi", "New description", "new-icon", now);
        when(serviceRepositoryPort.findById(1L)).thenReturn(Mono.just(validService));
        when(serviceRepositoryPort.existsByName("NewWiFi")).thenReturn(Mono.just(false));
        when(serviceRepositoryPort.update(any(Service.class))).thenReturn(Mono.just(updatedInfo));

        // When & Then
        StepVerifier.create(serviceService.updateService(1L, updatedInfo))
                .expectNext(updatedInfo)
                .verifyComplete();

        verify(serviceRepositoryPort, times(1)).findById(1L);
        verify(serviceRepositoryPort, times(1)).existsByName("NewWiFi");
        verify(serviceRepositoryPort, times(1)).update(any(Service.class));
    }

    @Test
    @DisplayName("updateService - Error: should fail when new name already exists")
    void updateService_DuplicateNewName_ShouldFail() {
        // Given
        Service updatedInfo = new Service(1L, "NewWiFi", "New description", "new-icon", now);
        when(serviceRepositoryPort.findById(1L)).thenReturn(Mono.just(validService));
        when(serviceRepositoryPort.existsByName("NewWiFi")).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(serviceService.updateService(1L, updatedInfo))
                .expectErrorMatches(error -> error instanceof BusinessRuleException &&
                        error.getMessage().contains("Ya existe un servicio con el nombre: NewWiFi"))
                .verify();

        verify(serviceRepositoryPort, times(1)).findById(1L);
        verify(serviceRepositoryPort, times(1)).existsByName("NewWiFi");
        verify(serviceRepositoryPort, never()).update(any(Service.class));
    }

    @Test
    @DisplayName("updateService - Success: should preserve createdAt")
    void updateService_PreservesCreatedAt() {
        // Given
        LocalDateTime differentTime = LocalDateTime.now().plusDays(1);
        Service updatedInfo = new Service(1L, "WiFi", "New description", "new-icon", differentTime);
        when(serviceRepositoryPort.findById(1L)).thenReturn(Mono.just(validService));
        when(serviceRepositoryPort.update(argThat(service ->
                service.createdAt().equals(validService.createdAt())
        ))).thenReturn(Mono.just(updatedInfo));

        // When & Then
        StepVerifier.create(serviceService.updateService(1L, updatedInfo))
                .expectNextMatches(service -> service != null)
                .verifyComplete();

        verify(serviceRepositoryPort, times(1)).findById(1L);
        verify(serviceRepositoryPort, times(1)).update(any(Service.class));
    }

    @Test
    @DisplayName("updateService - Error: should fail when service not found")
    void updateService_NotFound_ShouldFail() {
        // Given
        when(serviceRepositoryPort.findById(999L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(serviceService.updateService(999L, validService))
                .expectErrorMatches(error -> error instanceof ResourceNotFoundException &&
                        error.getMessage().contains("Servicio") &&
                        error.getMessage().contains("999"))
                .verify();

        verify(serviceRepositoryPort, times(1)).findById(999L);
        verify(serviceRepositoryPort, never()).update(any(Service.class));
    }

    @Test
    @DisplayName("updateService - Error: should fail with invalid data")
    void updateService_InvalidData_ShouldFail() {
        // Given
        Service invalidUpdate = new Service(1L, "", "Description", "icon", now);
        when(serviceRepositoryPort.findById(1L)).thenReturn(Mono.just(validService));
        when(serviceRepositoryPort.existsByName("")).thenReturn(Mono.just(false));

        // When & Then
        StepVerifier.create(serviceService.updateService(1L, invalidUpdate))
                .expectErrorMatches(error -> error instanceof ValidationException)
                .verify();

        verify(serviceRepositoryPort, times(1)).findById(1L);
        verify(serviceRepositoryPort, never()).update(any(Service.class));
    }

    // ==================== DELETE SERVICE TESTS ====================

    @Test
    @DisplayName("deleteService - Success: should delete service when exists")
    void deleteService_Success() {
        // Given
        when(serviceRepositoryPort.existsById(1L)).thenReturn(Mono.just(true));
        when(serviceRepositoryPort.deleteById(1L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(serviceService.deleteService(1L))
                .verifyComplete();

        verify(serviceRepositoryPort, times(1)).existsById(1L);
        verify(serviceRepositoryPort, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteService - Error: should fail when service not found")
    void deleteService_NotFound_ShouldFail() {
        // Given
        when(serviceRepositoryPort.existsById(999L)).thenReturn(Mono.just(false));

        // When & Then
        StepVerifier.create(serviceService.deleteService(999L))
                .expectErrorMatches(error -> error instanceof ResourceNotFoundException &&
                        error.getMessage().contains("Servicio") &&
                        error.getMessage().contains("999"))
                .verify();

        verify(serviceRepositoryPort, times(1)).existsById(999L);
        verify(serviceRepositoryPort, never()).deleteById(anyLong());
    }

    // ==================== GET SERVICE IDS BY ROOM ID TESTS ====================

    @Test
    @DisplayName("getServiceIdsByRoomId - Success: should return service IDs")
    void getServiceIdsByRoomId_Success() {
        // Given
        when(serviceRepositoryPort.findServiceIdsByRoomId(100L)).thenReturn(Flux.just(1L, 2L, 3L));

        // When & Then
        StepVerifier.create(serviceService.getServiceIdsByRoomId(100L))
                .expectNext(1L)
                .expectNext(2L)
                .expectNext(3L)
                .verifyComplete();

        verify(serviceRepositoryPort, times(1)).findServiceIdsByRoomId(100L);
    }

    @Test
    @DisplayName("getServiceIdsByRoomId - Success: should return empty flux when no services")
    void getServiceIdsByRoomId_Empty_Success() {
        // Given
        when(serviceRepositoryPort.findServiceIdsByRoomId(100L)).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(serviceService.getServiceIdsByRoomId(100L))
                .verifyComplete();

        verify(serviceRepositoryPort, times(1)).findServiceIdsByRoomId(100L);
    }

    // ==================== ADD SERVICE TO ROOM TESTS ====================

    @Test
    @DisplayName("addServiceToRoom - Success: should add service to room when both exist")
    void addServiceToRoom_Success() {
        // Given
        when(roomRepositoryPort.existsById(100L)).thenReturn(Mono.just(true));
        when(serviceRepositoryPort.existsById(1L)).thenReturn(Mono.just(true));
        when(serviceRepositoryPort.existsRoomServiceRelation(100L, 1L)).thenReturn(Mono.just(false));
        when(serviceRepositoryPort.addServiceToRoom(100L, 1L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(serviceService.addServiceToRoom(100L, 1L))
                .verifyComplete();

        verify(roomRepositoryPort, times(1)).existsById(100L);
        verify(serviceRepositoryPort, times(1)).existsById(1L);
        verify(serviceRepositoryPort, times(1)).existsRoomServiceRelation(100L, 1L);
        verify(serviceRepositoryPort, times(1)).addServiceToRoom(100L, 1L);
    }

    @Test
    @DisplayName("addServiceToRoom - Error: should fail when room does not exist")
    void addServiceToRoom_RoomNotFound_ShouldFail() {
        // Given
        when(roomRepositoryPort.existsById(100L)).thenReturn(Mono.just(false));

        // When & Then
        StepVerifier.create(serviceService.addServiceToRoom(100L, 1L))
                .expectErrorMatches(error -> error instanceof ResourceNotFoundException &&
                        error.getMessage().contains("Habitación") &&
                        error.getMessage().contains("100"))
                .verify();

        verify(roomRepositoryPort, times(1)).existsById(100L);
        verify(serviceRepositoryPort, never()).existsById(anyLong());
        verify(serviceRepositoryPort, never()).addServiceToRoom(anyLong(), anyLong());
    }

    @Test
    @DisplayName("addServiceToRoom - Error: should fail when service does not exist")
    void addServiceToRoom_ServiceNotFound_ShouldFail() {
        // Given
        when(roomRepositoryPort.existsById(100L)).thenReturn(Mono.just(true));
        when(serviceRepositoryPort.existsById(1L)).thenReturn(Mono.just(false));

        // When & Then
        StepVerifier.create(serviceService.addServiceToRoom(100L, 1L))
                .expectErrorMatches(error -> error instanceof ResourceNotFoundException &&
                        error.getMessage().contains("Servicio") &&
                        error.getMessage().contains("1"))
                .verify();

        verify(roomRepositoryPort, times(1)).existsById(100L);
        verify(serviceRepositoryPort, times(1)).existsById(1L);
        verify(serviceRepositoryPort, never()).addServiceToRoom(anyLong(), anyLong());
    }

    @Test
    @DisplayName("addServiceToRoom - Error: should fail when relation already exists")
    void addServiceToRoom_RelationExists_ShouldFail() {
        // Given
        when(roomRepositoryPort.existsById(100L)).thenReturn(Mono.just(true));
        when(serviceRepositoryPort.existsById(1L)).thenReturn(Mono.just(true));
        when(serviceRepositoryPort.existsRoomServiceRelation(100L, 1L)).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(serviceService.addServiceToRoom(100L, 1L))
                .expectErrorMatches(error -> error instanceof BusinessRuleException &&
                        error.getMessage().contains("El servicio con ID 1 ya está asociado a la habitación con ID 100"))
                .verify();

        verify(roomRepositoryPort, times(1)).existsById(100L);
        verify(serviceRepositoryPort, times(1)).existsById(1L);
        verify(serviceRepositoryPort, times(1)).existsRoomServiceRelation(100L, 1L);
        verify(serviceRepositoryPort, never()).addServiceToRoom(anyLong(), anyLong());
    }

    // ==================== REMOVE SERVICE FROM ROOM TESTS ====================

    @Test
    @DisplayName("removeServiceFromRoom - Success: should remove service from room")
    void removeServiceFromRoom_Success() {
        // Given
        when(serviceRepositoryPort.removeServiceFromRoom(100L, 1L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(serviceService.removeServiceFromRoom(100L, 1L))
                .verifyComplete();

        verify(serviceRepositoryPort, times(1)).removeServiceFromRoom(100L, 1L);
    }
}
