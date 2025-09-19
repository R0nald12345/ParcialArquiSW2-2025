package com.example.parcialarqui.pedido

data class Pedido(
    val id: Int,
    val fecha: String,
    val monto: Double,
    val clienteId: Int,
    val repartidorId: Int,
    val detalles: List<DetallePedido> = emptyList()
)
