## 🏗️ Patrón Builder - Implementación en Cliente

### 📊 Estructura del Patrón (Similar al Diagrama Genérico)

```
┌─────────────────────────────────────────────────────────────────┐
│                        ClienteActivity                          │
│                    (Cliente/Consumidor)                         │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         │ crea y usa
                         ▼
        ┌────────────────────────────────┐
        │   ClienteDirector              │
        │  (Director)                    │
        ├────────────────────────────────┤
        │ + construirClienteNuevo()      │
        │ + construirClienteExistente()  │
        │ + obtenerCliente()             │
        └────────────────┬───────────────┘
                         │
                         │ orquesta
                         ▼
        ┌────────────────────────────────┐
        │  ClienteBuilder (Interfaz)     │
        │  (Builder)                     │
        ├────────────────────────────────┤
        │ + buildNombre()                │
        │ + buildTelefono()              │
        │ + buildEmail()                 │
        │ + buildDireccion()             │
        │ + buildCoordenadas()           │
        │ + buildFechaRegistro()         │
        │ + getCliente()                 │
        └────────────────────────────────┘
                    ▲       ▲
         implementa │       │ implementa
                    │       │
        ┌───────────┴─┐   ┌─┴──────────────┐
        │ ClienteNuevo│   │ClienteExistente│
        │  Builder    │   │   Builder      │
        │(Concrete)   │   │  (Concrete)    │
        └─────────────┘   └────────────────┘
                │                │
                └────┬───────────┘
                     │ crean y retornan
                     ▼
        ┌────────────────────────────────┐
        │         Cliente                │
        │       (Product)                │
        ├────────────────────────────────┤
        │ + id: Int                      │
        │ + nombre: String               │
        │ + telefono: String             │
        │ + email: String                │
        │ + direccion: String            │
        │ + coordenadaX: Double          │
        │ + coordenadaY: Double          │
        │ + fechaRegistro: String        │
        └────────────────────────────────┘
```

### 📁 Archivos Creados

1. **ClienteBuilder.kt** (Interfaz/Builder)
   - Define los métodos para construir partes del Cliente
   - Similar a `ComputerBuilder` en el ejemplo

2. **ClienteNuevoBuilder.kt** (ConcreteBuilder)
   - Implementa la construcción de un Cliente nuevo
   - Similar a `HighEndComputerBuilder`, `LowEndComputerBuilder`, etc.

3. **ClienteExistenteBuilder.kt** (ConcreteBuilder)
   - Implementa la construcción de un Cliente existente
   - Mantiene el ID y fecha de registro original

4. **ClienteDirector.kt** (Director)
   - Orquesta los pasos de construcción
   - Define el orden de los pasos
   - Similar a `Director` en el ejemplo

5. **Cliente.kt** (Product)
   - El objeto final construido
   - Similar a `HighEndComputer`, `LowEndComputer`, `MidRangeComputer`

### 🔄 Flujo de Uso

#### Crear un Cliente Nuevo:
```kotlin
// 1. Crear el builder concreto
val clienteBuilder = ClienteNuevoBuilder()

// 2. Crear el director
val director = ClienteDirector(clienteBuilder)

// 3. Orquestar la construcción
val nuevoCliente = director.construirClienteNuevo(
    nombre = "Juan",
    telefono = "123456",
    email = "juan@email.com",
    direccion = "Calle 1",
    coordenadaX = 10.5,
    coordenadaY = 20.3
)
```

#### Actualizar un Cliente Existente:
```kotlin
// 1. Crear el builder concreto con el cliente existente
val clienteBuilder = ClienteExistenteBuilder(clienteExistente)

// 2. Crear el director
val director = ClienteDirector(clienteBuilder)

// 3. Orquestar la actualización
val clienteActualizado = director.construirClienteExistente(
    nombre = "Maria",
    telefono = "654321",
    email = "maria@email.com",
    direccion = "Calle 2",
    coordenadaX = 15.5,
    coordenadaY = 25.3,
    fechaRegistro = clienteExistente.fechaRegistro
)
```

### ✅ Ventajas de esta Implementación

1. **Separación de responsabilidades**: Cada clase tiene un propósito específico
2. **Flexibilidad**: Fácil agregar nuevos tipos de builders
3. **Reutilización**: El Director orquesta el proceso de forma genérica
4. **Validación centralizada**: En el método `getCliente()`
5. **Inmutabilidad**: Usa `data class` y `copy()` de Kotlin
6. **Similar al diagrama**: Sigue la estructura del ejemplo PHP proporcionado

### 🔍 Comparación con el Ejemplo

| Elemento | Ejemplo (PHP) | Implementación (Android) |
|----------|---------------|--------------------------|
| Interfaz Builder | `ComputerBuilder` | `ClienteBuilder` |
| Builder Concreto 1 | `HighEndComputerBuilder` | `ClienteNuevoBuilder` |
| Builder Concreto 2 | `LowEndComputerBuilder` | `ClienteExistenteBuilder` |
| Builder Concreto 3 | `MidRangeComputerBuilder` | - (Extensible) |
| Director | `Director` | `ClienteDirector` |
| Producto | `Computer` (HighEnd/LowEnd/MidRange) | `Cliente` |
| Cliente | `index.php` | `ClienteActivity` |
