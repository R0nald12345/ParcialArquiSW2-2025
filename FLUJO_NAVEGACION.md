/**
 * FLUJO DE NAVEGACIÓN CON EL PATRÓN STRATEGY
 * 
 * ┌─────────────────────────────────────────────────────────────────┐
 * │                          MainActivity                           │
 * │                      (Punto de entrada)                        │
 * └────────────────────┬─────────────────────────────────────────┘
 *                      │
 *                      ▼
 * ┌─────────────────────────────────────────────────────────────────┐
 * │                     CategoriaActivity                           │
 * │  (Muestra todas las categorías - Nav Drawer)                   │
 * │                                                                  │
 * │  Menu "nav_inicio" → ProductosGeneralActivity ─────────────┐  │
 * │  Menu "nav_metodo_pago" → MetodoPagoActivity             │  │
 * │  Menu "nav_repartidores" → RepartidorActivity            │  │
 * │  Menu "nav_clientes" → ClienteActivity                   │  │
 * │  Menu "nav_pedidos" → PedidoActivity                     │  │
 * │                                                                  │
 * │  Click en Item Categoría ↓                                     │
 * │                                                                  │
 * │  ┌──────────────────────────────────────────────────────────┐  │
 * │  │ productosActivity (categoriaId != 0)                      │  │
 * │  │ Strategy: ObtenerProductosPorCategoria                   │  │
 * │  │ (Muestra productos de ESA categoría)                     │  │
 * │  └──────────────────────────────────────────────────────────┘  │
 * └─────────────────────────────────────────────────────────────────┘
 *                      ▲
 *                      │
 *                      └───────────────────────────────────┐
 *                                                          │
 * ┌───────────────────────────────────────────────────────┴──────────┐
 * │              ⭐ ProductosGeneralActivity                         │
 * │  (Muestra TODOS los productos - Nav Drawer)                    │
 * │                                                                  │
 * │  Menu "nav_inicio" → CategoriaActivity ──────────────────────┐ │
 * │  Menu "nav_metodo_pago" → MetodoPagoActivity               │ │
 * │  Menu "nav_repartidores" → RepartidorActivity              │ │
 * │  Menu "nav_clientes" → ClienteActivity                     │ │
 * │  Menu "nav_pedidos" → PedidoActivity                       │ │
 * │                                                                  │
 * │  Strategy: ObtenerTodosProductos (categoriaId = 0)            │
 * │  (Obtiene TODOS los productos de TODAS las categorías)        │
 * └──────────────────────────────────────────────────────────────┘
 * 
 * 
 * ═══════════════════════════════════════════════════════════════════
 * ESTRATEGIAS UTILIZADAS:
 * ═══════════════════════════════════════════════════════════════════
 * 
 * 1. ObtenerTodosProductos (ProductosGeneralActivity)
 *    └─ api.obtenerProductos(callback)
 *    └─ Retorna: List<Producto> de TODAS las categorías
 * 
 * 2. ObtenerProductosPorCategoria(categoriaId) (ProductosActivity)
 *    └─ api.obtenerProductosPorCategoria(categoriaId, callback)
 *    └─ Retorna: List<Producto> de esa categoría específica
 * 
 * ═══════════════════════════════════════════════════════════════════
 * VENTAJAS:
 * ═══════════════════════════════════════════════════════════════════
 * 
 * ✅ Separación de responsabilidades
 * ✅ Fácil de mantener y extender
 * ✅ Reutilización del mismo adapter y layout
 * ✅ Cambio de comportamiento sin modificar código existente
 * ✅ Cumple con SOLID (Open/Closed Principle)
 */
