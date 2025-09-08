package com.example.parcialarqui.producto

data class Producto(
    val id: Int,
    val nombre: String,
    val precio: Double,
    val descripcion: String,
    val stock: Int,
    val imagen: String,
    val categoriaId: Int
)