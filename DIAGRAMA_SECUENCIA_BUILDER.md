## 🔄 Diagrama de Secuencia - Patrón Builder Cliente


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
