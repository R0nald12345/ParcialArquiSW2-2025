# 📱 COMPONENTES PRINCIPALES DEL SISTEMA

## 🎯 PatrÓN STRATEGY APLICADO

### Interfaz:
```kotlin
interface ProductoStrategy {
    fun obtenerProductos(
        api: ApiGateway,
        callback: ApiGateway.ApiCallback<List<Producto>>
    )
}
```

### Estrategia 1: Todos los Productos
```kotlin
class ObtenerTodosProductos : ProductoStrategy {
    override fun obtenerProductos(api, callback) {
        api.obtenerProductos(callback) // ← Retorna TODOS
    }
}
```

### Estrategia 2: Productos por Categoría
```kotlin
class ObtenerProductosPorCategoria(categoriaId: Int) : ProductoStrategy {
    override fun obtenerProductos(api, callback) {
        api.obtenerProductosPorCategoria(categoriaId, callback) // ← Retorna por categoría
    }
}
```

---

## 📱 VISTAS Y SUS ESTRATEGIAS

### 1. ProductosGeneralActivity (⭐ NUEVA)
```kotlin
class ProductosGeneralActivity : AppCompatActivity() {
    // ✅ Estrategia para TODOS los productos
    private lateinit var strategy: ProductoStrategy = ObtenerTodosProductos()
    
    // ✅ Navega desde el menú "nav_inicio"
    // ✅ Muestra todos los productos de TODAS las categorías
}
```

**Estructura:**
```
ProductosGeneralActivity
├── DrawerLayout (Menú)
├── Toolbar
├── EditText (Buscar)
└── RecyclerView (Productos)
```

---

### 2. ProductosActivity (EXISTENTE)
```kotlin
class ProductosActivity : AppCompatActivity() {
    // ✅ Estrategia por categoría específica
    private lateinit var strategy: ProductoStrategy = 
        ObtenerProductosPorCategoria(categoriaId)
    
    // ✅ Navega desde click en CategoriaActivity
    // ✅ Muestra productos de ESA categoría
}
```

**Estructura:**
```
ProductosActivity
├── Toolbar (Volver)
├── TextView (Título)
├── EditText (Buscar)
└── RecyclerView (Productos)
```

---

### 3. CategoriaActivity (MODIFICADA)
```kotlin
class CategoriaActivity : AppCompatActivity() {
    // ✅ Menu "nav_inicio" ahora lleva a ProductosGeneralActivity
    
    navigationView.setNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_inicio -> {
                // 🔴 ANTES: No hacía nada
                // 🟢 AHORA: Navega a ProductosGeneralActivity
                startActivity(Intent(this, ProductosGeneralActivity::class.java))
            }
        }
    }
}
```

**Estructura:**
```
CategoriaActivity
├── DrawerLayout (Menú)
├── Toolbar
├── EditText (Buscar)
├── RecyclerView
│   └── Click en item → ProductosActivity
└── Botón Agregar
```

---

## 🔄 FLUJO COMPLETO

### 🚀 Inicio de la App
```
MainActivity
    ↓
CategoriaActivity (se muestra la lista de categorías)
```

### 📋 Acciones Posibles desde CategoriaActivity

**Opción 1: Ver TODOS los productos**
```
Menu "Inicio" (nav_inicio)
    ↓
ProductosGeneralActivity ⭐
    ├── Strategy: ObtenerTodosProductos()
    ├── Obtiene: Todos los productos
    └── Muestra: En RecyclerView
```

**Opción 2: Ver productos de una categoría**
```
Click en item de categoría
    ↓
ProductosActivity
    ├── Strategy: ObtenerProductosPorCategoria(id)
    ├── Obtiene: Productos de esa categoría
    └── Muestra: En RecyclerView
```

**Opción 3: Navegar a otros módulos**
```
Menu → Métodos Pago / Repartidores / Clientes / Pedidos
    ↓
RespectiveActivity
```

---

## 📊 LLAMADAS A API

### ProductosGeneralActivity - Obtiene TODOS
```kotlin
api.obtenerProductos(callback) // ← Sin filtro
```

### ProductosActivity - Obtiene POR CATEGORÍA
```kotlin
api.obtenerProductosPorCategoria(categoriaId, callback) // ← Con filtro
```

---

## 🎨 REUTILIZACIÓN DE CÓDIGO

### Adapter (MISMO en ambas vistas)
```kotlin
ProductoAdapter(
    lista = productos,
    onItemClick = { producto -> ... },
    onRefresh = { cargarProductos() }
)
```

### Layout (SIMILAR en ambas vistas)
```
activity_productos.xml         (sin menu drawer)
activity_productos_general.xml (con menu drawer)
```

### Lógica (IGUAL en ambas vistas)
```kotlin
strategy.obtenerProductos(apiGateway, callback)
```

---

## ✅ CHECKLIST DE ARCHIVOS MODIFICADOS

- [x] Creado: `ProductosGeneralActivity.kt`
- [x] Creado: `activity_productos_general.xml`
- [x] Modificado: `CategoriaActivity.kt` (import + nav_inicio logic)
- [x] Modificado: `ProductosGeneralActivity.kt` (nav_inicio logic)
- [x] Modificado: `AndroidManifest.xml` (registro de actividad)

