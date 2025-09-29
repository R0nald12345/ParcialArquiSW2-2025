package com.example.parcialarqui.pedido

data class PedidoCompleto(
    val id: Int,
    val fecha: String,
    val monto: Double,
    val clienteId: Int,
    val repartidorId: Int,
    val clienteNombre: String,
    val clienteTelefono: String,
    val metodoPagoId: Int,
    val repartidorNombre: String,
    val repartidorPlaca: String,
    val detalles: List<DetallePedido> = emptyList(),
    val metodoPagoNombre: String = ""

)