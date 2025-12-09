# 🧪 API Testing - CURL Commands for Motel Management Service

Este archivo contiene comandos CURL completos para probar todos los endpoints CRUD del microservicio de gestión de moteles, habitaciones y reservas.

## 📋 Configuración Inicial

**Base URL:** `http://localhost:8084`

**Headers comunes:**
- `Content-Type: application/json`
- `Accept: application/json`

---

## 🏨 MOTEL - Operaciones CRUD

### 1. Crear Motel (POST)

```bash
curl -X POST http://localhost:8084/api/motels \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Motel Paradise",
    "address": "Av. Principal 123",
    "phoneNumber": "+1234567890",
    "description": "Un motel acogedor con todas las comodidades",
    "city": "Lima",
    "propertyId": 1,
    "imageUrls": [
      "https://example.com/images/motel1.jpg",
      "https://example.com/images/motel2.jpg"
    ]
  }'
```

### 2. Crear Segundo Motel (POST) - Para testing adicional

```bash
curl -X POST http://localhost:8084/api/motels \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Motel Sunset View",
    "address": "Calle Los Pinos 456",
    "phoneNumber": "+1987654321",
    "description": "Hermosa vista al atardecer con piscina",
    "city": "Cusco",
    "propertyId": 2,
    "imageUrls": [
      "https://example.com/images/sunset1.jpg",
      "https://example.com/images/sunset2.jpg",
      "https://example.com/images/sunset3.jpg"
    ]
  }'
```

### 3. Obtener Motel por ID (GET)

```bash
curl -X GET http://localhost:8084/api/motels/1 \
  -H "Accept: application/json"
```

### 4. Obtener Todos los Moteles (GET)

```bash
curl -X GET http://localhost:8084/api/motels \
  -H "Accept: application/json"
```

### 5. Obtener Moteles por Ciudad (GET)

```bash
curl -X GET http://localhost:8084/api/motels/city/Lima \
  -H "Accept: application/json"
```

```bash
curl -X GET http://localhost:8084/api/motels/city/Cusco \
  -H "Accept: application/json"
```

### 6. Actualizar Motel (PUT)

```bash
curl -X PUT http://localhost:8084/api/motels/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Motel Paradise Deluxe",
    "address": "Av. Principal 123 - Actualizada",
    "phoneNumber": "+1234567890",
    "description": "Un motel acogedor con todas las comodidades - Renovado 2024",
    "city": "Lima",
    "imageUrls": [
      "https://example.com/images/motel1-updated.jpg",
      "https://example.com/images/motel2-updated.jpg",
      "https://example.com/images/motel3-new.jpg"
    ]
  }'
```

### 7. Eliminar Motel (DELETE)

```bash
# NOTA: Ejecutar esto al final de las pruebas
curl -X DELETE http://localhost:8084/api/motels/2 \
  -H "Accept: application/json"
```

---

## 🛏️ ROOM (Habitaciones) - Operaciones CRUD

### 1. Crear Habitación Simple (POST)

```bash
curl -X POST http://localhost:8084/api/rooms \
  -H "Content-Type: application/json" \
  -d '{
    "motelId": 1,
    "number": "101",
    "roomType": "Simple",
    "price": 50.00,
    "description": "Habitación simple con cama individual",
    "imageUrls": [
      "https://example.com/images/room101-1.jpg",
      "https://example.com/images/room101-2.jpg"
    ]
  }'
```

### 2. Crear Habitación Doble (POST)

```bash
curl -X POST http://localhost:8084/api/rooms \
  -H "Content-Type: application/json" \
  -d '{
    "motelId": 1,
    "number": "102",
    "roomType": "Doble",
    "price": 80.00,
    "description": "Habitación doble con dos camas matrimoniales",
    "imageUrls": [
      "https://example.com/images/room102-1.jpg",
      "https://example.com/images/room102-2.jpg",
      "https://example.com/images/room102-3.jpg"
    ]
  }'
```

### 3. Crear Habitación Suite (POST)

```bash
curl -X POST http://localhost:8084/api/rooms \
  -H "Content-Type: application/json" \
  -d '{
    "motelId": 1,
    "number": "201",
    "roomType": "Suite",
    "price": 150.00,
    "description": "Suite de lujo con jacuzzi y sala de estar",
    "imageUrls": [
      "https://example.com/images/suite201-1.jpg",
      "https://example.com/images/suite201-2.jpg",
      "https://example.com/images/suite201-3.jpg",
      "https://example.com/images/suite201-4.jpg"
    ]
  }'
```

### 4. Crear Habitación en Segundo Motel (POST)

```bash
curl -X POST http://localhost:8084/api/rooms \
  -H "Content-Type: application/json" \
  -d '{
    "motelId": 2,
    "number": "301",
    "roomType": "Doble",
    "price": 90.00,
    "description": "Habitación doble con vista al mar",
    "imageUrls": [
      "https://example.com/images/room301-1.jpg"
    ]
  }'
```

### 5. Obtener Habitación por ID (GET)

```bash
curl -X GET http://localhost:8084/api/rooms/1 \
  -H "Accept: application/json"
```

### 6. Obtener Todas las Habitaciones (GET)

```bash
curl -X GET http://localhost:8084/api/rooms \
  -H "Accept: application/json"
```

### 7. Obtener Habitaciones por Motel ID (GET)

```bash
curl -X GET http://localhost:8084/api/rooms/motel/1 \
  -H "Accept: application/json"
```

### 8. Obtener Habitaciones Disponibles por Motel ID (GET)

```bash
curl -X GET http://localhost:8084/api/rooms/motel/1/available \
  -H "Accept: application/json"
```

### 9. Actualizar Habitación (PUT)

```bash
curl -X PUT http://localhost:8084/api/rooms/1 \
  -H "Content-Type: application/json" \
  -d '{
    "number": "101",
    "roomType": "Simple Renovada",
    "price": 55.00,
    "description": "Habitación simple renovada con TV Smart",
    "isAvailable": true
  }'
```

### 10. Marcar Habitación como No Disponible (PUT)

```bash
curl -X PUT http://localhost:8084/api/rooms/2 \
  -H "Content-Type: application/json" \
  -d '{
    "number": "102",
    "roomType": "Doble",
    "price": 80.00,
    "description": "Habitación doble con dos camas matrimoniales",
    "isAvailable": false
  }'
```

### 11. Eliminar Habitación (DELETE)

```bash
# NOTA: Ejecutar esto al final de las pruebas
curl -X DELETE http://localhost:8084/api/rooms/4 \
  -H "Accept: application/json"
```

---

## 📅 RESERVATION (Reservas) - Operaciones CRUD

> **⚠️ NOTA IMPORTANTE sobre fechas:** Las fechas en los ejemplos de reservas (2026-01-15, etc.) son para demostración. 
> Para pruebas reales, actualice las fechas a valores futuros según la fecha actual, ya que el sistema valida que 
> las fechas de check-in y check-out deben estar en el futuro. Use el formato ISO 8601: `YYYY-MM-DDTHH:mm:ss`

### 1. Crear Reserva (POST)

```bash
curl -X POST http://localhost:8084/api/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": 1,
    "userId": 100,
    "checkInDate": "2026-01-15T14:00:00",
    "checkOutDate": "2026-01-17T12:00:00",
    "totalPrice": 100.00,
    "specialRequests": "Cama extra y desayuno incluido"
  }'
```

### 2. Crear Segunda Reserva (POST)

```bash
curl -X POST http://localhost:8084/api/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": 3,
    "userId": 101,
    "checkInDate": "2026-01-20T15:00:00",
    "checkOutDate": "2026-01-25T11:00:00",
    "totalPrice": 750.00,
    "specialRequests": "Luna de miel - decoración romántica"
  }'
```

### 3. Crear Tercera Reserva (POST) - Mismo Usuario

```bash
curl -X POST http://localhost:8084/api/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": 2,
    "userId": 100,
    "checkInDate": "2026-01-28T14:00:00",
    "checkOutDate": "2026-01-30T12:00:00",
    "totalPrice": 160.00,
    "specialRequests": "Vista al jardín"
  }'
```

### 4. Obtener Reserva por ID (GET)

```bash
curl -X GET http://localhost:8084/api/reservations/1 \
  -H "Accept: application/json"
```

### 5. Obtener Todas las Reservas (GET)

```bash
curl -X GET http://localhost:8084/api/reservations \
  -H "Accept: application/json"
```

### 6. Obtener Reservas por Habitación (GET)

```bash
curl -X GET http://localhost:8084/api/reservations/room/1 \
  -H "Accept: application/json"
```

### 7. Obtener Reservas por Usuario (GET)

```bash
curl -X GET http://localhost:8084/api/reservations/user/100 \
  -H "Accept: application/json"
```

### 8. Obtener Reservas Activas por Habitación (GET)

```bash
curl -X GET http://localhost:8084/api/reservations/room/1/active \
  -H "Accept: application/json"
```

### 9. Obtener Reservas por Estado (GET)

Estados disponibles: `PENDING`, `CONFIRMED`, `CANCELLED`, `CHECKED_IN`, `CHECKED_OUT`, `COMPLETED`

```bash
# Reservas pendientes
curl -X GET http://localhost:8084/api/reservations/status/PENDING \
  -H "Accept: application/json"
```

```bash
# Reservas confirmadas
curl -X GET http://localhost:8084/api/reservations/status/CONFIRMED \
  -H "Accept: application/json"
```

```bash
# Reservas canceladas
curl -X GET http://localhost:8084/api/reservations/status/CANCELLED \
  -H "Accept: application/json"
```

### 10. Verificar Disponibilidad de Habitación (GET)

```bash
curl -X GET "http://localhost:8084/api/reservations/room/1/available?checkIn=2026-01-10T14:00:00&checkOut=2026-01-12T12:00:00" \
  -H "Accept: application/json"
```

### 11. Actualizar Reserva (PUT)

```bash
curl -X PUT http://localhost:8084/api/reservations/1 \
  -H "Content-Type: application/json" \
  -d '{
    "checkInDate": "2026-01-16T14:00:00",
    "checkOutDate": "2026-01-18T12:00:00",
    "totalPrice": 110.00,
    "specialRequests": "Cama extra, desayuno incluido y late check-out"
  }'
```

### 12. Confirmar Reserva (PATCH)

```bash
curl -X PATCH http://localhost:8084/api/reservations/1/confirm \
  -H "Accept: application/json"
```

### 13. Cancelar Reserva (PATCH)

```bash
curl -X PATCH http://localhost:8084/api/reservations/3/cancel \
  -H "Accept: application/json"
```

### 14. Realizar Check-in (PATCH)

```bash
curl -X PATCH http://localhost:8084/api/reservations/1/checkin \
  -H "Accept: application/json"
```

### 15. Realizar Check-out (PATCH)

```bash
curl -X PATCH http://localhost:8084/api/reservations/1/checkout \
  -H "Accept: application/json"
```

### 16. Eliminar Reserva (DELETE)

```bash
# NOTA: Solo se pueden eliminar reservas canceladas
curl -X DELETE http://localhost:8084/api/reservations/3 \
  -H "Accept: application/json"
```

---

## 🔄 Flujo de Prueba Completo Recomendado

### Paso 1: Preparar Base de Datos
1. Asegúrese de que el servicio esté ejecutándose en `http://localhost:8084`
2. Verifique que la base de datos PostgreSQL esté funcionando

### Paso 2: Crear Datos de Prueba (en orden)

```bash
# 1. Crear moteles
# Ejecutar: Crear Motel (sección Motel #1)
# Ejecutar: Crear Segundo Motel (sección Motel #2)

# 2. Crear habitaciones
# Ejecutar: Crear Habitación Simple (sección Room #1)
# Ejecutar: Crear Habitación Doble (sección Room #2)
# Ejecutar: Crear Habitación Suite (sección Room #3)

# 3. Crear reservas
# Ejecutar: Crear Reserva (sección Reservation #1)
# Ejecutar: Crear Segunda Reserva (sección Reservation #2)
```

### Paso 3: Probar Operaciones de Lectura

```bash
# Obtener todos los moteles
# Obtener moteles por ciudad
# Obtener todas las habitaciones
# Obtener habitaciones por motel
# Obtener todas las reservas
# Obtener reservas por usuario
```

### Paso 4: Probar Operaciones de Actualización

```bash
# Actualizar motel
# Actualizar habitación
# Confirmar reserva
# Realizar check-in
```

### Paso 5: Probar Operaciones de Estado (Reservation)

```bash
# Obtener reservas por estado
# Verificar disponibilidad
# Cancelar reserva
```

### Paso 6: Probar Operaciones de Eliminación

```bash
# Eliminar reserva cancelada
# Eliminar habitación
# Eliminar motel
```

---

## 📝 Notas Importantes

### Validaciones a Tener en Cuenta:

1. **Motel:**
   - Campo `name` (nombre): debe tener entre 3 y 100 caracteres
   - Campo `city` (ciudad): es requerido, máximo 100 caracteres
   - Campo `address` (dirección): es requerido, máximo 255 caracteres
   - Campo `imageUrls`: máximo 10 imágenes permitidas

2. **Room:**
   - Campo `motelId`: debe existir antes de crear la habitación
   - Campo `price` (precio): debe ser mayor que cero
   - Campo `roomType` (tipo): es requerido, máximo 50 caracteres
   - Campo `number` (número): es requerido, máximo 20 caracteres
   - Campo `imageUrls`: máximo 15 imágenes permitidas

3. **Reservation:**
   - Campos `checkInDate` y `checkOutDate`: deben estar en el futuro (formato ISO 8601)
   - El check-out debe ser después del check-in
   - Campo `totalPrice`: debe ser mayor que cero
   - Solo se pueden eliminar reservas canceladas
   - Transiciones de estado válidas:
     - PENDING → CONFIRMED → CHECKED_IN → CHECKED_OUT → COMPLETED
     - PENDING/CONFIRMED → CANCELLED

### Códigos de Estado HTTP Esperados:

- `201 Created` - Recurso creado exitosamente (POST)
- `200 OK` - Operación exitosa (GET, PUT, PATCH)
- `204 No Content` - Eliminación exitosa (DELETE)
- `400 Bad Request` - Error de validación
- `404 Not Found` - Recurso no encontrado
- `500 Internal Server Error` - Error del servidor

---

## 🔧 Importar a Postman

Para importar estos comandos a Postman:

1. Cree una nueva colección en Postman llamada "Motel Management API"
2. Cree tres carpetas dentro: "Motels", "Rooms", "Reservations"
3. Para cada comando CURL:
   - Copie el comando completo
   - En Postman: Click en "Import" → "Raw text" → Pegue el comando CURL
   - El comando será convertido automáticamente a una petición de Postman
4. Configure una variable de entorno `baseUrl` con valor `http://localhost:8084`
5. Reemplace las URLs en las peticiones por `{{baseUrl}}/api/...`

### Variables de Entorno Sugeridas para Postman:

```json
{
  "baseUrl": "http://localhost:8084",
  "motelId": "1",
  "roomId": "1",
  "reservationId": "1",
  "userId": "100"
}
```

---

## 🐛 Solución de Problemas

### Error: Connection refused
- Verifique que el servicio esté ejecutándose: `mvn spring-boot:run`
- Confirme que el puerto 8084 esté disponible

### Error: Base de datos no disponible
- Inicie PostgreSQL: `./start-database.sh`
- Verifique la conexión en `application.yml`

### Error 404: Not Found
- Verifique que el ID del recurso exista
- Confirme la URL del endpoint

### Error 400: Bad Request
- Revise el formato JSON del request body
- Verifique que todos los campos requeridos estén presentes
- Confirme que las fechas estén en formato ISO 8601: `YYYY-MM-DDTHH:mm:ss`

---

## 📚 Referencias

- Documentación del servicio: `/docs`
- Testing general: `TESTING.md`
- Testing del gateway: `TESTING_MOTEL_GATEWAY.md`
- Arquitectura: Hexagonal (Ports & Adapters)
- Framework: Spring Boot WebFlux (Reactive)

---

**Última actualización:** 2025-12-09
**Versión del servicio:** 1.0.0
**Puerto del servicio:** 8084
