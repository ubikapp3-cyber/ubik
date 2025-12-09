package com.ubik.usermanagement.domain.service;

import com.ubik.usermanagement.domain.exception.ResourceNotFoundException;
import com.ubik.usermanagement.domain.exception.ValidationException;
import com.ubik.usermanagement.domain.model.Room;
import com.ubik.usermanagement.domain.port.in.RoomUseCasePort;
import com.ubik.usermanagement.domain.port.out.MotelRepositoryPort;
import com.ubik.usermanagement.domain.port.out.RoomRepositoryPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Servicio de dominio que implementa los casos de uso de Room
 * Contiene la lógica de negocio
 */
@Service
public class RoomService implements RoomUseCasePort {

    private final RoomRepositoryPort roomRepositoryPort;
    private final MotelRepositoryPort motelRepositoryPort;

    public RoomService(RoomRepositoryPort roomRepositoryPort, MotelRepositoryPort motelRepositoryPort) {
        this.roomRepositoryPort = roomRepositoryPort;
        this.motelRepositoryPort = motelRepositoryPort;
    }

    @Override
    public Mono<Room> createRoom(Room room) {
        // Validar que el motel existe
        return motelRepositoryPort.existsById(room.motelId())
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new ResourceNotFoundException("Motel", room.motelId()));
                    }
                    return validateRoom(room)
                            .then(Mono.defer(() -> roomRepositoryPort.save(room)));
                });
    }

    @Override
    public Mono<Room> getRoomById(Long id) {
        return roomRepositoryPort.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Habitación", id)));
    }

    @Override
    public Flux<Room> getAllRooms() {
        return roomRepositoryPort.findAll();
    }

    @Override
    public Flux<Room> getRoomsByMotelId(Long motelId) {
        return roomRepositoryPort.findByMotelId(motelId);
    }

    @Override
    public Flux<Room> getAvailableRoomsByMotelId(Long motelId) {
        return roomRepositoryPort.findAvailableByMotelId(motelId);
    }

    @Override
    public Mono<Room> updateRoom(Long id, Room room) {
        return roomRepositoryPort.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Habitación", id)))
                .flatMap(existingRoom -> {
                    Room updatedRoom = new Room(
                            id,
                            existingRoom.motelId(), // No se puede cambiar el motel
                            room.number(),
                            room.roomType(),
                            room.price(),
                            room.description(),
                            room.isAvailable(),
                            room.imageUrls()
                    );
                    return validateRoom(updatedRoom)
                            .then(Mono.defer(() -> roomRepositoryPort.update(updatedRoom)));
                });
    }

    @Override
    public Mono<Void> deleteRoom(Long id) {
        return roomRepositoryPort.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new ResourceNotFoundException("Habitación", id));
                    }
                    return roomRepositoryPort.deleteById(id);
                });
    }

    /**
     * Validaciones de negocio para una habitación
     */
    private Mono<Void> validateRoom(Room room) {
        if (room.number() == null || room.number().trim().isEmpty()) {
            return Mono.error(new ValidationException("El número de habitación es requerido"));
        }
        if (room.roomType() == null || room.roomType().trim().isEmpty()) {
            return Mono.error(new ValidationException("El tipo de habitación es requerido"));
        }
        if (room.price() == null || room.price() <= 0) {
            return Mono.error(new ValidationException("El precio debe ser mayor que cero"));
        }
        if (room.imageUrls() != null && room.imageUrls().size() > 15) {
            return Mono.error(new ValidationException("No se pueden agregar más de 15 imágenes"));
        }
        return Mono.empty();
    }
}