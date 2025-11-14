package com.example.parcialarqui.cliente

/**
 * Builder: Interfaz abstracta para todos los builders concretos
 * Define los pasos comunes de construcción del Cliente
 */
interface ClienteBuilder {
    fun buildNombre(nombre: String)
    fun buildTelefono(telefono: String)
    fun buildEmail(email: String)
    fun buildDireccion(direccion: String)
    fun buildCoordenadas(x: Double, y: Double)
    fun buildFechaRegistro(fecha: String)
    fun getCliente(): Cliente
}
