package com.example.parcialarqui.producto

import com.example.parcialarqui.ApiGateway

class ObtenerTodosProductos : ProductoStrategy {

    override fun obtenerProductos(
        api: ApiGateway,
        callback: ApiGateway.ApiCallback<List<Producto>>
    ) {
        api.obtenerProductos(callback)
    }
}
