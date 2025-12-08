package com.ubik.usermanagement.domain.service;

import com.ubik.usermanagement.domain.exception.ResourceNotFoundException;
import com.ubik.usermanagement.domain.exception.ValidationException;
import com.ubik.usermanagement.domain.model.Room;
import com.ubik.usermanagement.domain.port.out.MotelRepositoryPort;
import com.ubik.usermanagement.domain.port.out.RoomRepositoryPort;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoomService Unit Tests")
class RoomServiceTest {

    @Mock
    private RoomRepositoryPort roomRepositoryPort;

    @Mock
    private MotelRepositoryPort motelRepositoryPort;

    @InjectMocks
    private RoomService roomService;

    private Room validRoom;

    @BeforeEach
    void setUp() {
        validRoom = new Room(
                1L,
                100L,
                "101",
                "Suite",
                150.0,
                "Habitación de lujo",
                true,
                Arrays.asList("room1.jpg", "room2.jpg")
        );
    }

    // ==================== CREATE ROOM TESTS ====================

    @Test
    @DisplayName("createRoom - Success: should create room when motel exists")
    void createRoom_Success() {
        // Given
        when(motelRepositoryPort.existsById(100L)).thenReturn(Mono.just(true));
        when(roomRepositoryPort.save(any(Room.class))).thenReturn(Mono.just(validRoom));

        // When & Then
        StepVerifier.create(roomService.createRoom(validRoom))
                .expectNext(validRoom)
                .verifyComplete();

        verify(motelRepositoryPort, times(1)).existsById(100L);
        verify(roomRepositoryPort, times(1)).save(any(Room.class));
    }

    @Test
    @DisplayName("createRoom - Error: should fail when motel does not exist")
    void createRoom_MotelNotFound_ShouldFail() {
        // Given
        when(motelRepositoryPort.existsById(100L)).thenReturn(Mono.just(false));

        // When & Then
        StepVerifier.create(roomService.createRoom(validRoom))
                .expectErrorMatches(error -> error instanceof ResourceNotFoundException &&
                        error.getMessage().contains("Motel") &&
                        error.getMessage().contains("100"))
                .verify();

        verify(motelRepositoryPort, times(1)).existsById(100L);
        verify(roomRepositoryPort, never()).save(any(Room.class));
    }

    @Test
    @DisplayName("createRoom - Error: should fail when room number is null")
    void createRoom_NullNumber_ShouldFail() {
        // Given
        Room invalidRoom = new Room(null, 100L, null, "Suite", 150.0, "Desc", true, null);
        when(motelRepositoryPort.existsById(100L)).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(roomService.createRoom(invalidRoom))
                .expectErrorMatches(error -> error instanceof ValidationException &&
                        error.getMessage().contains("número de habitación es requerido"))
                .verify();

        verify(roomRepositoryPort, never()).save(any(Room.class));
    }

    @Test
    @DisplayName("createRoom - Error: should fail when room number is empty")
    void createRoom_EmptyNumber_ShouldFail() {
        // Given
        Room invalidRoom = new Room(null, 100L, "   ", "Suite", 150.0, "Desc", true, null);
        when(motelRepositoryPort.existsById(100L)).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(roomService.createRoom(invalidRoom))
                .expectErrorMatches(error -> error instanceof ValidationException &&
                        error.getMessage().contains("número de habitación es requerido"))
                .verify();

        verify(roomRepositoryPort, never()).save(any(Room.class));
    }

    @Test
    @DisplayName("createRoom - Error: should fail when room type is null")
    void createRoom_NullRoomType_ShouldFail() {
        // Given
        Room invalidRoom = new Room(null, 100L, "101", null, 150.0, "Desc", true, null);
        when(motelRepositoryPort.existsById(100L)).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(roomService.createRoom(invalidRoom))
                .expectErrorMatches(error -> error instanceof ValidationException &&
                        error.getMessage().contains("tipo de habitación es requerido"))
                .verify();

        verify(roomRepositoryPort, never()).save(any(Room.class));
    }

    @Test
    @DisplayName("createRoom - Error: should fail when room type is empty")
    void createRoom_EmptyRoomType_ShouldFail() {
        // Given
        Room invalidRoom = new Room(null, 100L, "101", "", 150.0, "Desc", true, null);
        when(motelRepositoryPort.existsById(100L)).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(roomService.createRoom(invalidRoom))
                .expectErrorMatches(error -> error instanceof ValidationException &&
                        error.getMessage().contains("tipo de habitación es requerido"))
                .verify();

        verify(roomRepositoryPort, never()).save(any(Room.class));
    }

    @Test
    @DisplayName("createRoom - Error: should fail when price is null")
    void createRoom_NullPrice_ShouldFail() {
        // Given
        Room invalidRoom = new Room(null, 100L, "101", "Suite", null, "Desc", true, null);
        when(motelRepositoryPort.existsById(100L)).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(roomService.createRoom(invalidRoom))
                .expectErrorMatches(error -> error instanceof ValidationException &&
                        error.getMessage().contains("precio debe ser mayor que cero"))
                .verify();

        verify(roomRepositoryPort, never()).save(any(Room.class));
    }

    @Test
    @DisplayName("createRoom - Error: should fail when price is zero")
    void createRoom_ZeroPrice_ShouldFail() {
        // Given
        Room invalidRoom = new Room(null, 100L, "101", "Suite", 0.0, "Desc", true, null);
        when(motelRepositoryPort.existsById(100L)).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(roomService.createRoom(invalidRoom))
                .expectErrorMatches(error -> error instanceof ValidationException &&
                        error.getMessage().contains("precio debe ser mayor que cero"))
                .verify();

        verify(roomRepositoryPort, never()).save(any(Room.class));
    }

    @Test
    @DisplayName("createRoom - Error: should fail when price is negative")
    void createRoom_NegativePrice_ShouldFail() {
        // Given
        Room invalidRoom = new Room(null, 100L, "101", "Suite", -50.0, "Desc", true, null);
        when(motelRepositoryPort.existsById(100L)).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(roomService.createRoom(invalidRoom))
                .expectErrorMatches(error -> error instanceof ValidationException &&
                        error.getMessage().contains("precio debe ser mayor que cero"))
                .verify();

        verify(roomRepositoryPort, never()).save(any(Room.class));
    }

    @Test
    @DisplayName("createRoom - Edge Case: should fail when imageUrls exceeds limit of 15")
    void createRoom_TooManyImages_ShouldFail() {
        // Given - 16 images
        List<String> tooManyImages = Arrays.asList(
                "url1.jpg", "url2.jpg", "url3.jpg", "url4.jpg", "url5.jpg",
                "url6.jpg", "url7.jpg", "url8.jpg", "url9.jpg", "url10.jpg",
                "url11.jpg", "url12.jpg", "url13.jpg", "url14.jpg", "url15.jpg", "url16.jpg"
        );
        Room invalidRoom = new Room(null, 100L, "101", "Suite", 150.0, "Desc", true, tooManyImages);
        when(motelRepositoryPort.existsById(100L)).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(roomService.createRoom(invalidRoom))
                .expectErrorMatches(error -> error instanceof ValidationException &&
                        error.getMessage().contains("No se pueden agregar más de 15 imágenes"))
                .verify();

        verify(roomRepositoryPort, never()).save(any(Room.class));
    }

    @Test
    @DisplayName("createRoom - Edge Case: should succeed with exactly 15 images")
    void createRoom_ExactlyFifteenImages_ShouldSucceed() {
        // Given - exactly 15 images
        List<String> fifteenImages = Arrays.asList(
                "url1.jpg", "url2.jpg", "url3.jpg", "url4.jpg", "url5.jpg",
                "url6.jpg", "url7.jpg", "url8.jpg", "url9.jpg", "url10.jpg",
                "url11.jpg", "url12.jpg", "url13.jpg", "url14.jpg", "url15.jpg"
        );
        Room room = new Room(null, 100L, "101", "Suite", 150.0, "Desc", true, fifteenImages);
        when(motelRepositoryPort.existsById(100L)).thenReturn(Mono.just(true));
        when(roomRepositoryPort.save(any(Room.class))).thenReturn(Mono.just(room));

        // When & Then
        StepVerifier.create(roomService.createRoom(room))
                .expectNext(room)
                .verifyComplete();

        verify(roomRepositoryPort, times(1)).save(any(Room.class));
    }

    @Test
    @DisplayName("createRoom - Edge Case: should succeed with null imageUrls")
    void createRoom_NullImageUrls_ShouldSucceed() {
        // Given
        Room room = new Room(null, 100L, "101", "Suite", 150.0, "Desc", true, null);
        when(motelRepositoryPort.existsById(100L)).thenReturn(Mono.just(true));
        when(roomRepositoryPort.save(any(Room.class))).thenReturn(Mono.just(room));

        // When & Then
        StepVerifier.create(roomService.createRoom(room))
                .expectNext(room)
                .verifyComplete();

        verify(roomRepositoryPort, times(1)).save(any(Room.class));
    }

    @Test
    @DisplayName("createRoom - Edge Case: should succeed with empty imageUrls list")
    void createRoom_EmptyImageUrls_ShouldSucceed() {
        // Given
        Room room = new Room(null, 100L, "101", "Suite", 150.0, "Desc", true, Collections.emptyList());
        when(motelRepositoryPort.existsById(100L)).thenReturn(Mono.just(true));
        when(roomRepositoryPort.save(any(Room.class))).thenReturn(Mono.just(room));

        // When & Then
        StepVerifier.create(roomService.createRoom(room))
                .expectNext(room)
                .verifyComplete();

        verify(roomRepositoryPort, times(1)).save(any(Room.class));
    }

    // ==================== GET ROOM BY ID TESTS ====================

    @Test
    @DisplayName("getRoomById - Success: should return room when found")
    void getRoomById_Success() {
        // Given
        when(roomRepositoryPort.findById(1L)).thenReturn(Mono.just(validRoom));

        // When & Then
        StepVerifier.create(roomService.getRoomById(1L))
                .expectNext(validRoom)
                .verifyComplete();

        verify(roomRepositoryPort, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getRoomById - Error: should throw ResourceNotFoundException when not found")
    void getRoomById_NotFound_ShouldFail() {
        // Given
        when(roomRepositoryPort.findById(999L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(roomService.getRoomById(999L))
                .expectErrorMatches(error -> error instanceof ResourceNotFoundException &&
                        error.getMessage().contains("Habitación") &&
                        error.getMessage().contains("999"))
                .verify();

        verify(roomRepositoryPort, times(1)).findById(999L);
    }

    // ==================== GET ALL ROOMS TESTS ====================

    @Test
    @DisplayName("getAllRooms - Success: should return all rooms")
    void getAllRooms_Success() {
        // Given
        Room room2 = new Room(2L, 100L, "102", "Standard", 100.0, "Desc", true, null);
        when(roomRepositoryPort.findAll()).thenReturn(Flux.just(validRoom, room2));

        // When & Then
        StepVerifier.create(roomService.getAllRooms())
                .expectNext(validRoom)
                .expectNext(room2)
                .verifyComplete();

        verify(roomRepositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllRooms - Success: should return empty flux when no rooms exist")
    void getAllRooms_Empty_Success() {
        // Given
        when(roomRepositoryPort.findAll()).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(roomService.getAllRooms())
                .verifyComplete();

        verify(roomRepositoryPort, times(1)).findAll();
    }

    // ==================== GET ROOMS BY MOTEL ID TESTS ====================

    @Test
    @DisplayName("getRoomsByMotelId - Success: should return rooms for motel")
    void getRoomsByMotelId_Success() {
        // Given
        when(roomRepositoryPort.findByMotelId(100L)).thenReturn(Flux.just(validRoom));

        // When & Then
        StepVerifier.create(roomService.getRoomsByMotelId(100L))
                .expectNext(validRoom)
                .verifyComplete();

        verify(roomRepositoryPort, times(1)).findByMotelId(100L);
    }

    @Test
    @DisplayName("getRoomsByMotelId - Success: should return empty flux when no rooms for motel")
    void getRoomsByMotelId_Empty_Success() {
        // Given
        when(roomRepositoryPort.findByMotelId(999L)).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(roomService.getRoomsByMotelId(999L))
                .verifyComplete();

        verify(roomRepositoryPort, times(1)).findByMotelId(999L);
    }

    // ==================== GET AVAILABLE ROOMS BY MOTEL ID TESTS ====================

    @Test
    @DisplayName("getAvailableRoomsByMotelId - Success: should return available rooms")
    void getAvailableRoomsByMotelId_Success() {
        // Given
        when(roomRepositoryPort.findAvailableByMotelId(100L)).thenReturn(Flux.just(validRoom));

        // When & Then
        StepVerifier.create(roomService.getAvailableRoomsByMotelId(100L))
                .expectNext(validRoom)
                .verifyComplete();

        verify(roomRepositoryPort, times(1)).findAvailableByMotelId(100L);
    }

    @Test
    @DisplayName("getAvailableRoomsByMotelId - Success: should return empty flux when no available rooms")
    void getAvailableRoomsByMotelId_Empty_Success() {
        // Given
        when(roomRepositoryPort.findAvailableByMotelId(100L)).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(roomService.getAvailableRoomsByMotelId(100L))
                .verifyComplete();

        verify(roomRepositoryPort, times(1)).findAvailableByMotelId(100L);
    }

    // ==================== UPDATE ROOM TESTS ====================

    @Test
    @DisplayName("updateRoom - Success: should update room when found")
    void updateRoom_Success() {
        // Given
        Room updatedInfo = new Room(1L, 100L, "101A", "Deluxe", 200.0, "New Desc", false, Arrays.asList("new.jpg"));
        when(roomRepositoryPort.findById(1L)).thenReturn(Mono.just(validRoom));
        when(roomRepositoryPort.update(any(Room.class))).thenReturn(Mono.just(updatedInfo));

        // When & Then
        StepVerifier.create(roomService.updateRoom(1L, updatedInfo))
                .expectNext(updatedInfo)
                .verifyComplete();

        verify(roomRepositoryPort, times(1)).findById(1L);
        verify(roomRepositoryPort, times(1)).update(any(Room.class));
    }

    @Test
    @DisplayName("updateRoom - Success: should preserve motelId")
    void updateRoom_PreservesMotelId() {
        // Given - try to change motelId (should be preserved)
        Room updatedInfo = new Room(1L, 999L, "101A", "Deluxe", 200.0, "New Desc", false, null);
        when(roomRepositoryPort.findById(1L)).thenReturn(Mono.just(validRoom));
        when(roomRepositoryPort.update(argThat(room ->
                room.motelId().equals(validRoom.motelId())
        ))).thenReturn(Mono.just(updatedInfo));

        // When & Then
        StepVerifier.create(roomService.updateRoom(1L, updatedInfo))
                .expectNextMatches(room -> room != null)
                .verifyComplete();

        verify(roomRepositoryPort, times(1)).findById(1L);
        verify(roomRepositoryPort, times(1)).update(any(Room.class));
    }

    @Test
    @DisplayName("updateRoom - Error: should fail when room not found")
    void updateRoom_NotFound_ShouldFail() {
        // Given
        when(roomRepositoryPort.findById(999L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(roomService.updateRoom(999L, validRoom))
                .expectErrorMatches(error -> error instanceof ResourceNotFoundException &&
                        error.getMessage().contains("Habitación") &&
                        error.getMessage().contains("999"))
                .verify();

        verify(roomRepositoryPort, times(1)).findById(999L);
        verify(roomRepositoryPort, never()).update(any(Room.class));
    }

    @Test
    @DisplayName("updateRoom - Error: should fail with invalid data")
    void updateRoom_InvalidData_ShouldFail() {
        // Given
        Room invalidUpdate = new Room(1L, 100L, "", "Suite", 150.0, "Desc", true, null);
        when(roomRepositoryPort.findById(1L)).thenReturn(Mono.just(validRoom));

        // When & Then
        StepVerifier.create(roomService.updateRoom(1L, invalidUpdate))
                .expectErrorMatches(error -> error instanceof ValidationException)
                .verify();

        verify(roomRepositoryPort, times(1)).findById(1L);
        verify(roomRepositoryPort, never()).update(any(Room.class));
    }

    // ==================== DELETE ROOM TESTS ====================

    @Test
    @DisplayName("deleteRoom - Success: should delete room when exists")
    void deleteRoom_Success() {
        // Given
        when(roomRepositoryPort.existsById(1L)).thenReturn(Mono.just(true));
        when(roomRepositoryPort.deleteById(1L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(roomService.deleteRoom(1L))
                .verifyComplete();

        verify(roomRepositoryPort, times(1)).existsById(1L);
        verify(roomRepositoryPort, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteRoom - Error: should fail when room not found")
    void deleteRoom_NotFound_ShouldFail() {
        // Given
        when(roomRepositoryPort.existsById(999L)).thenReturn(Mono.just(false));

        // When & Then
        StepVerifier.create(roomService.deleteRoom(999L))
                .expectErrorMatches(error -> error instanceof ResourceNotFoundException &&
                        error.getMessage().contains("Habitación") &&
                        error.getMessage().contains("999"))
                .verify();

        verify(roomRepositoryPort, times(1)).existsById(999L);
        verify(roomRepositoryPort, never()).deleteById(anyLong());
    }
}
