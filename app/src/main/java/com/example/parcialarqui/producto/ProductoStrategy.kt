package com.example.parcialarqui.producto

import com.example.parcialarqui.ApiGateway

interface ProductoStrategy {
    fun obtenerProductos(
        api: ApiGateway,
        callback: ApiGateway.ApiCallback<List<Producto>>
    )
}
