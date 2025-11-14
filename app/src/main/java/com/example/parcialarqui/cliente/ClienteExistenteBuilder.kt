package com.example.parcialarqui.cliente

/**
 * ConcreteBuilder: Implementación concreta para actualizar un Cliente existente
 * Responsable de mantener el ID y la fecha de registro original
 */
class ClienteExistenteBuilder(clienteExistente: Cliente) : ClienteBuilder {
    private var cliente: Cliente = clienteExistente

    override fun buildNombre(nombre: String) {
        cliente = cliente.copy(nombre = nombre.trim())
    }

    override fun buildTelefono(telefono: String) {
        cliente = cliente.copy(telefono = telefono.trim())
    }

    override fun buildEmail(email: String) {
        cliente = cliente.copy(email = email.trim())
    }

    override fun buildDireccion(direccion: String) {
        cliente = cliente.copy(direccion = direccion.trim())
    }

    override fun buildCoordenadas(x: Double, y: Double) {
        cliente = cliente.copy(coordenadaX = x, coordenadaY = y)
    }

    override fun buildFechaRegistro(fecha: String) {
        cliente = cliente.copy(fechaRegistro = fecha)
    }

    override fun getCliente(): Cliente {
        // Validaciones antes de retornar
        require(cliente.nombre.isNotBlank()) { "El nombre del cliente es requerido" }
        require(cliente.telefono.isNotBlank()) { "El teléfono es requerido" }
        require(cliente.email.isNotBlank()) { "El email es requerido" }
        require(cliente.direccion.isNotBlank()) { "La dirección es requerida" }

        return cliente
    }
}
