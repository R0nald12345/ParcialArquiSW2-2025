package com.example.parcialarqui.cliente

data class Cliente(
    val id: Int,
    val nombre: String,
    val telefono: String,
    val email: String,
    val direccion: String,
    val coordenadaX: Double,
    val coordenadaY: Double,
    val fechaRegistro: String
)