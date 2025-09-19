package com.example.parcialarqui.pedido

data class DetallePedido(
    val pedidoId: Int,
    val productoId: Int,
    val cantidad: Int,
    val precio: Double
)