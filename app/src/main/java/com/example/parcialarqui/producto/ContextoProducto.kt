package com.example.parcialarqui.producto

import com.example.parcialarqui.ApiGateway

// ⭐ CLASE CONTEXT DEL PATRÓN STRATEGY
class ContextoProducto {
    
    private lateinit var strategy: ProductoStrategy
    
    /**
     * Establece la estrategia que se utilizará
     * Similar a: context.setStrategy(new SerieA())
     */
    fun setStrategy(strategy: ProductoStrategy) {
        this.strategy = strategy
    }
    
    /**
     * Ejecuta la estrategia actual
     */
    fun obtenerProductos(
        api: ApiGateway,
        callback: ApiGateway.ApiCallback<List<Producto>>
    ) {
        if (::strategy.isInitialized) {
            strategy.obtenerProductos(api, callback)
        }
    }
}
