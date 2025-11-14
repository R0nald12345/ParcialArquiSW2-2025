## 🔄 Diagrama de Secuencia - Patrón Builder Cliente

### 1️⃣ Crear Cliente Nuevo

```mermaid
sequenceDiagram
    participant CA as ClienteActivity
    participant CNB as ClienteNuevoBuilder
    participant CD as ClienteDirector
    participant CB as ClienteBuilder<br/>(interfaz)
    participant C as Cliente<br/>(Product)
    participant AG as ApiGateway

    CA->>CNB: new ClienteNuevoBuilder()
    CNB-->>CA: Instancia del builder

    CA->>CD: new ClienteDirector(clienteBuilder)
    CD-->>CA: Instancia del director

    CA->>CD: construirClienteNuevo(nombre, telefono, email, direccion, coordenadaX, coordenadaY)
    
    CD->>CB: buildNombre(nombre)
    CB->>CNB: buildNombre(nombre)
    CNB->>C: cliente.copy(nombre = nombre.trim())
    C-->>CNB: Cliente actualizado

    CD->>CB: buildTelefono(telefono)
    CB->>CNB: buildTelefono(telefono)
    CNB->>C: cliente.copy(telefono = telefono.trim())
    C-->>CNB: Cliente actualizado

    CD->>CB: buildEmail(email)
    CB->>CNB: buildEmail(email)
    CNB->>C: cliente.copy(email = email.trim())
    C-->>CNB: Cliente actualizado

    CD->>CB: buildDireccion(direccion)
    CB->>CNB: buildDireccion(direccion)
    CNB->>C: cliente.copy(direccion = direccion.trim())
    C-->>CNB: Cliente actualizado

    CD->>CB: buildCoordenadas(coordenadaX, coordenadaY)
    CB->>CNB: buildCoordenadas(coordenadaX, coordenadaY)
    CNB->>C: cliente.copy(coordenadaX = x, coordenadaY = y)
    C-->>CNB: Cliente actualizado

    CD->>CB: buildFechaRegistro("")
    CB->>CNB: buildFechaRegistro("")
    CNB->>C: cliente.copy(fechaRegistro = "")
    C-->>CNB: Cliente actualizado

    CD->>CB: getCliente()
    CB->>CNB: getCliente()
    CNB->>CNB: Validar datos
    CNB->>C: return cliente
    C-->>CNB: Cliente construido
    CNB-->>CD: Cliente construido
    CD-->>CA: Cliente construido

    CA->>AG: crearCliente(nuevo, callback)
    AG->>AG: Enviar a API (puerto 8083)
    AG-->>CA: onSuccess() / onError()
    CA->>CA: Mostrar Toast
    CA->>CA: cargarClientes()
```

---

### 2️⃣ Actualizar Cliente Existente

```mermaid
sequenceDiagram
    participant CA as ClienteActivity
    participant CEB as ClienteExistenteBuilder
    participant CD as ClienteDirector
    participant CB as ClienteBuilder<br/>(interfaz)
    participant C as Cliente<br/>(Product)
    participant AG as ApiGateway

    CA->>CEB: new ClienteExistenteBuilder(clienteExistente)
    Note over CEB: Se pasa el Cliente actual<br/>que ya tiene ID y fecha
    CEB-->>CA: Instancia del builder

    CA->>CD: new ClienteDirector(clienteBuilder)
    CD-->>CA: Instancia del director

    CA->>CD: construirClienteExistente(nombre, telefono, email, direccion, coordenadaX, coordenadaY, fechaRegistro)
    
    CD->>CB: buildNombre(nombre)
    CB->>CEB: buildNombre(nombre)
    CEB->>C: cliente.copy(nombre = nombre.trim())
    C-->>CEB: Cliente actualizado

    CD->>CB: buildTelefono(telefono)
    CB->>CEB: buildTelefono(telefono)
    CEB->>C: cliente.copy(telefono = telefono.trim())
    C-->>CEB: Cliente actualizado

    CD->>CB: buildEmail(email)
    CB->>CEB: buildEmail(email)
    CEB->>C: cliente.copy(email = email.trim())
    C-->>CEB: Cliente actualizado

    CD->>CB: buildDireccion(direccion)
    CB->>CEB: buildDireccion(direccion)
    CEB->>C: cliente.copy(direccion = direccion.trim())
    C-->>CEB: Cliente actualizado

    CD->>CB: buildCoordenadas(coordenadaX, coordenadaY)
    CB->>CEB: buildCoordenadas(coordenadaX, coordenadaY)
    CEB->>C: cliente.copy(coordenadaX = x, coordenadaY = y)
    C-->>CEB: Cliente actualizado

    CD->>CB: buildFechaRegistro(fechaRegistro)
    CB->>CEB: buildFechaRegistro(fechaRegistro)
    CEB->>C: cliente.copy(fechaRegistro = fechaRegistro)
    C-->>CEB: Cliente actualizado

    CD->>CB: getCliente()
    CB->>CEB: getCliente()
    CEB->>CEB: Validar datos
    CEB->>C: return cliente
    C-->>CEB: Cliente actualizado
    CEB-->>CD: Cliente actualizado
    CD-->>CA: Cliente actualizado

    CA->>AG: actualizarCliente(actualizado, callback)
    AG->>AG: Enviar a API (puerto 8083)
    AG-->>CA: onSuccess() / onError()
    CA->>CA: Mostrar Toast
    CA->>CA: cargarClientes()
```

---

### 3️⃣ Flujo Completo (Visión General)

```mermaid
sequenceDiagram
    participant User as Usuario
    participant CA as ClienteActivity
    participant CNB as ClienteNuevoBuilder<br/>o<br/>ClienteExistenteBuilder
    participant CD as ClienteDirector
    participant AG as ApiGateway
    participant Server as Backend<br/>(puerto 8083)

    User->>CA: Click en "Agregar" o "Editar"
    CA->>CA: Mostrar Dialog con formulario

    User->>CA: Ingresa datos (nombre, teléfono, etc.)
    User->>CA: Click en "Guardar" o "Actualizar"

    CA->>CNB: new ConcreteBuilder()
    CNB-->>CA: Builder creado

    CA->>CD: new ClienteDirector(builder)
    CD-->>CA: Director creado

    CA->>CD: construirCliente...(parámetros)
    
    CD->>CNB: buildNombre()<br/>buildTelefono()<br/>buildEmail()<br/>buildDireccion()<br/>buildCoordenadas()<br/>buildFechaRegistro()
    CNB->>CNB: Actualizar Cliente<br/>paso a paso

    CD->>CNB: getCliente()
    CNB->>CNB: Validar todos los datos
    alt Validación OK
        CNB-->>CD: Cliente construido ✅
    else Validación FALLA
        CNB->>CA: Lanzar IllegalArgumentException
        CA->>User: Toast: "Error de validación"
    end

    CD-->>CA: Cliente listo

    CA->>AG: crearCliente() o<br/>actualizarCliente()
    AG->>Server: POST/PUT JSON al backend
    
    alt Respuesta exitosa
        Server-->>AG: 201/200 OK
        AG-->>CA: onSuccess(true)
        CA->>CA: cargarClientes()
        CA->>User: Toast: "Guardado/Actualizado"
    else Error en el servidor
        Server-->>AG: 400/500 Error
        AG-->>CA: onError(message)
        CA->>User: Toast: "Error: [mensaje]"
    end
```

---

### 4️⃣ Comparación: Antes vs Después del Patrón

```mermaid
graph LR
    subgraph Antes["❌ ANTES (Sin Patrón)"]
        A1["ClienteActivity"]
        A2["new Cliente(id, nombre,<br/>telefono, email, ...)"]
        A3["Cliente creado"]
        A1 -->|Construcción directa| A2
        A2 --> A3
    end

    subgraph Después["✅ DESPUÉS (Con Patrón Builder)"]
        B1["ClienteActivity"]
        B2["ClienteNuevoBuilder"]
        B3["ClienteDirector"]
        B4["ClienteBuilder<br/>Interface"]
        B5["Cliente construido<br/>y validado"]
        B1 -->|crea| B2
        B1 -->|crea| B3
        B3 -->|usa| B4
        B2 -->|implementa| B4
        B3 -->|orquesta| B2
        B2 --> B5
    end

    style Antes fill:#ff6b6b,stroke:#c92a2a,color:#fff
    style Después fill:#51cf66,stroke:#2b8a3e,color:#fff
```

---

### 📝 Notas del Diagrama de Secuencia

**En la creación de un Cliente Nuevo:**
1. Se crea `ClienteNuevoBuilder` (sin parámetros)
2. Se crea `ClienteDirector` con el builder
3. El Director orquesta los pasos de construcción
4. Cada paso actualiza el Cliente usando `copy()`
5. Al final se valida todo en `getCliente()`
6. Se envía la API si la validación es OK

**En la actualización de un Cliente Existente:**
1. Se crea `ClienteExistenteBuilder` pasando el cliente actual
2. Mantiene el `id` y `fechaRegistro` del original
3. Actualiza solo los campos modificados
4. Mantiene la misma orquestación del Director

**Validaciones:**
- Ocurren en el método `getCliente()` de cada builder
- Lanzan `IllegalArgumentException` si hay error
- Se capturan en `ClienteActivity` con try-catch
- Se muestran al usuario como Toast

---

### 🔄 Método Builder (El flujo principal)

```
ClienteActivity
    ↓
[1] Crear Builder Concreto
    ↓
ClienteNuevoBuilder() o ClienteExistenteBuilder()
    ↓
[2] Crear Director
    ↓
ClienteDirector(builder)
    ↓
[3] Orquestar Construcción
    ↓
director.construirClienteNuevo(...) 
director.construirClienteExistente(...)
    ↓
    ├─ buildNombre()
    ├─ buildTelefono()
    ├─ buildEmail()
    ├─ buildDireccion()
    ├─ buildCoordenadas()
    └─ buildFechaRegistro()
    ↓
[4] Validar y Obtener
    ↓
builder.getCliente()
    ↓
[5] Usar Cliente
    ↓
apiGateway.crearCliente() o actualizarCliente()
```

El patrón **está perfecto y completo**. 🚀
