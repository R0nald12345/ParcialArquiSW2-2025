# 📊 DIAGRAMA VISUAL DEL FLUJO

## 🏗️ ARQUITECTURA DEL SISTEMA

```
╔════════════════════════════════════════════════════════════════════════════╗
║                         APLICACIÓN COMPLETA                               ║
╚════════════════════════════════════════════════════════════════════════════╝

                              ┌──────────────┐
                              │  MainActivity │
                              └───────┬──────┘
                                      │
                              ┌───────▼──────────┐
                              │ CategoriaActivity │
                              │   (Drawer Menu)   │
                              └───┬──────────────┬─────┐
                                  │              │     │
                    ┌─────────────┼──────────────┘     │
                    │             │                     │
         (nav_inicio│           (Click             (nav_pedidos)
          Menu)     │          Categoría)          (nav_clientes)
                    │             │                   (nav_repartidores)
                    │             │                   (nav_metodo_pago)
                    │             │                     │
        ┌───────────▼──────┐  ┌──▼─────────────────┐  │
        │ ProductosGeneral  │  │ ProductosActivity  │  │
        │   Activity ⭐     │  │ (Por Categoría)    │  │
        │                   │  │                    │  │
        │ Strategy:         │  │ Strategy:          │  │
        │ ObtenerTodos      │  │ ObtenerPorCat      │  │
        │                   │  │                    │  │
        │ TODOS productos   │  │ Productos ESA cat  │  │
        └───────────────────┘  └────────────────────┘  │
                 │                      │                │
                 │ (nav_inicio)         │ (Menu)         │
                 └──────────────────────┴────────────────┤
                                                         │
                          ┌──────────────┬───────────────┘
                          │              │
                     ┌────▼─────┐  ┌─────▼─────────┐
                     │ Otros...  │  │ Otras vistas  │
                     │ Pedidos   │  │ (Menu)        │
                     │ Clientes  │  └───────────────┘
                     │           │
                     └───────────┘
```

---

## 🔄 SECUENCIA DE INTERACCIONES

### Caso 1: Ver TODOS los productos

```
Usuario
   │
   └─→ Menu: Click "Inicio"
          │
          └─→ [CategoriaActivity]
                 │
                 └─→ startActivity(ProductosGeneralActivity)
                         │
                         └─→ [ProductosGeneralActivity] ⭐
                                │
                                └─→ strategy = ObtenerTodosProductos()
                                       │
                                       └─→ api.obtenerProductos(callback)
                                              │
                                              └─→ Retorna List<Producto> (TODOS)
                                                     │
                                                     └─→ adapter.notifyDataSetChanged()
                                                            │
                                                            └─→ RecyclerView muestra TODOS
```

### Caso 2: Ver productos de una categoría

```
Usuario
   │
   └─→ [CategoriaActivity] - Lista de categorías
          │
          └─→ Click en item (ej: "Comida Rápida")
                 │
                 └─→ startActivity(ProductosActivity)
                         │ (pass: categoriaId = 3)
                         │
                         └─→ [ProductosActivity]
                                │
                                └─→ strategy = ObtenerProductosPorCategoria(3)
                                       │
                                       └─→ api.obtenerProductosPorCategoria(3, callback)
                                              │
                                              └─→ Retorna List<Producto> (de esa cat)
                                                     │
                                                     └─→ adapter.notifyDataSetChanged()
                                                            │
                                                            └─→ RecyclerView muestra de la cat
```

---

## 📐 PATRÓN STRATEGY EN ACCIÓN

```
┌─────────────────────────────────────────────────────────────┐
│                  ProductoStrategy (Interface)               │
│                                                             │
│  fun obtenerProductos(api, callback)                       │
└─────────────────────────────────────────────────────────────┘
         ▲                              ▲
         │                              │
         │ implements                   │ implements
         │                              │
┌────────┴──────────────┐    ┌─────────┴────────────────────┐
│ ObtenerTodosProductos │    │ ObtenerProductosPorCategoria │
│                       │    │ (categoriaId: Int)           │
├───────────────────────┤    ├──────────────────────────────┤
│ override fun obtener  │    │ override fun obtener         │
│   Productos(...)  {   │    │   Productos(...) {           │
│                       │    │                              │
│ api.obtenerProductos( │    │ api.obtenerProductosPor      │
│    callback)          │    │   Categoria(                 │
│                       │    │   categoriaId,               │
│ // Retorna TODOS      │    │   callback)                  │
│ }                     │    │                              │
│                       │    │ // Retorna por categoría     │
│                       │    │ }                            │
└────────────┬──────────┘    └─────────────┬────────────────┘
             │                             │
             │ Usado en                    │ Usado en
             │                             │
    ┌────────▼───────────────┐   ┌────────▼──────────────────┐
    │ProductosGeneralActivity│   │ ProductosActivity         │
    │                        │   │                           │
    │ strategy =             │   │ strategy =                │
    │ ObtenerTodosProductos()│   │ ObtenerProductosPor       │
    │                        │   │ Categoria(categoriaId)    │
    └────────────────────────┘   └───────────────────────────┘
```

---

## 🎯 DECISIÓN DE ESTRATEGIA

```
┌─────────────────────────────────────────┐
│      ¿Cuál estrategia usar?             │
└────┬──────────────────────────────┬─────┘
     │                              │
     │ categoriaId = 0              │ categoriaId > 0
     │ (no especificada)            │ (especificada)
     │                              │
     ▼                              ▼
┌─────────────────────────┐  ┌─────────────────────────┐
│ ProductosGeneralActivity│  │ ProductosActivity       │
│                         │  │                         │
│ ObtenerTodosProductos() │  │ ObtenerProductosPor     │
│                         │  │ Categoria(id)           │
│ Obtiene: TODOS          │  │ Obtiene: POR CATEGORÍA  │
│ Acceso: Menu "Inicio"   │  │ Acceso: Click Categoría │
└─────────────────────────┘  └─────────────────────────┘
```

---

## 🗂️ ESTRUCTURA DE CARPETAS

```
app/src/main/
│
├── java/com/example/parcialarqui/
│   ├── producto/
│   │   ├── ProductoStrategy.kt             (Interfaz)
│   │   ├── ObtenerTodosProductos.kt        (Estrategia 1)
│   │   ├── ObtenerProductosPorCategoria.kt (Estrategia 2)
│   │   ├── ProductosActivity.kt            (Vista 1)
│   │   ├── ProductosGeneralActivity.kt ⭐   (Vista 2 - NUEVA)
│   │   ├── ProductoAdapter.kt              (Adapter compartido)
│   │   └── Producto.kt                     (Data class)
│   │
│   ├── categoria/
│   │   ├── CategoriaActivity.kt ⭐ (MODIFICADO)
│   │   └── ...
│   │
│   └── ...
│
└── res/layout/
    ├── activity_productos.xml              (Layout 1)
    ├── activity_productos_general.xml ⭐   (Layout 2 - NUEVO)
    └── ...
```

---

## 🔗 RELACIONES ENTRE COMPONENTES

```
CategoriaActivity
    │
    ├─→ Carga categorías
    │   └─→ RecyclerView (CategoriaAdapter)
    │
    ├─→ Menu drawer
    │   ├─→ nav_inicio ──→ ProductosGeneralActivity ⭐
    │   ├─→ nav_metodo_pago ──→ MetodoPagoActivity
    │   ├─→ nav_clientes ──→ ClienteActivity
    │   ├─→ nav_repartidores ──→ RepartidorActivity
    │   └─→ nav_pedidos ──→ PedidoActivity
    │
    └─→ Click en categoría item
        └─→ ProductosActivity
            └─→ Carga productos de esa categoría

ProductosGeneralActivity ⭐
    │
    ├─→ Carga TODOS los productos
    │   └─→ Strategy: ObtenerTodosProductos()
    │       └─→ api.obtenerProductos(callback)
    │
    ├─→ Menu drawer
    │   ├─→ nav_inicio ──→ CategoriaActivity
    │   └─→ (otros items)
    │
    └─→ RecyclerView (ProductoAdapter - compartido)

ProductosActivity
    │
    ├─→ Carga productos de categoría
    │   └─→ Strategy: ObtenerProductosPorCategoria(id)
    │       └─→ api.obtenerProductosPorCategoria(id, callback)
    │
    └─→ RecyclerView (ProductoAdapter - compartido)
```

---

## 📊 FLUJO DE DATOS

```
Api Backend
    │
    ├─→ obtenerProductos()
    │   │
    │   └─→ Retorna: List<Producto> (TODOS)
    │       │
    │       └─→ ObtenerTodosProductos strategy
    │           │
    │           └─→ ProductosGeneralActivity
    │               └─→ Muestra en RecyclerView
    │
    └─→ obtenerProductosPorCategoria(categoriaId)
        │
        └─→ Retorna: List<Producto> (de esa categoría)
            │
            └─→ ObtenerProductosPorCategoria strategy
                │
                └─→ ProductosActivity
                    └─→ Muestra en RecyclerView
```

---

## ✅ CHECKLIST DE IMPLEMENTACIÓN

```
[✅] Interfaz ProductoStrategy creada
[✅] Estrategia ObtenerTodosProductos implementada
[✅] Estrategia ObtenerProductosPorCategoria implementada
[✅] ProductosActivity usa Strategy (categoría)
[✅] ProductosGeneralActivity creada ⭐ (NUEVA)
[✅] ProductosGeneralActivity usa Strategy (todos)
[✅] Layout activity_productos_general.xml creado ⭐ (NUEVO)
[✅] CategoriaActivity modificado con nav_inicio correcto
[✅] ProductosGeneralActivity navegación configurada
[✅] AndroidManifest.xml actualizado
[✅] Reutilización de ProductoAdapter
[✅] SOLID principles respetados
```

