package com.example.parcialarqui.producto

import com.example.parcialarqui.ApiGateway

class ObtenerProductosPorCategoria(private val categoriaId: Int) : ProductoStrategy {

    override fun obtenerProductos(
        api: ApiGateway,
        callback: ApiGateway.ApiCallback<List<Producto>>
    ) {
        api.obtenerProductosPorCategoria(categoriaId, callback)
    }
}
