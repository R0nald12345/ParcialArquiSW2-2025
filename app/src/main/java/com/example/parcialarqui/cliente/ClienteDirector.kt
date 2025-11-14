package com.example.parcialarqui.cliente

/**
 * Director: Encargado de definir los pasos de construcción del Cliente
 * Orquesta el proceso de construcción usando un ClienteBuilder
 */
class ClienteDirector(private val builder: ClienteBuilder) {

    /**
     * Construye un cliente nuevo con los pasos requeridos
     */
    fun construirClienteNuevo(
        nombre: String,
        telefono: String,
        email: String,
        direccion: String,
        coordenadaX: Double,
        coordenadaY: Double
    ): Cliente {
        builder.buildNombre(nombre)
        builder.buildTelefono(telefono)
        builder.buildEmail(email)
        builder.buildDireccion(direccion)
        builder.buildCoordenadas(coordenadaX, coordenadaY)
        builder.buildFechaRegistro("") // Lo asigna el servidor

        return builder.getCliente()
    }

    /**
     * Construye un cliente existente actualizando sus datos
     */
    fun construirClienteExistente(
        nombre: String,
        telefono: String,
        email: String,
        direccion: String,
        coordenadaX: Double,
        coordenadaY: Double,
        fechaRegistro: String
    ): Cliente {
        builder.buildNombre(nombre)
        builder.buildTelefono(telefono)
        builder.buildEmail(email)
        builder.buildDireccion(direccion)
        builder.buildCoordenadas(coordenadaX, coordenadaY)
        builder.buildFechaRegistro(fechaRegistro)

        return builder.getCliente()
    }

    /**
     * Obtiene el cliente construido
     */
    fun obtenerCliente(): Cliente {
        return builder.getCliente()
    }
}
