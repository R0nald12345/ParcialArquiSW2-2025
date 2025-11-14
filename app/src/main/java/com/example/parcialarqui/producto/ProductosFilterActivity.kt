package com.example.parcialarqui.producto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parcialarqui.ApiGateway
import com.example.parcialarqui.GeneradorCatalogo.CatalogoPdfGenerator
import com.example.parcialarqui.GeneradorCatalogo.WhatsAppHelper
import com.example.parcialarqui.R
import com.example.parcialarqui.categoria.Categoria
import com.example.parcialarqui.categoria.CategoriaActivity
import com.example.parcialarqui.cliente.ClienteActivity
import com.example.parcialarqui.metodopago.MetodoPagoActivity
import com.example.parcialarqui.pedido.PedidoActivity
import com.example.parcialarqui.repartidor.RepartidorActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import android.widget.EditText
import java.io.File

class ProductosFilterActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var spinnerCategorias: Spinner
    private lateinit var tvTitulo: TextView

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navigationView: NavigationView
    private lateinit var fabAgregar: FloatingActionButton
    private lateinit var fabGenerarPdf: FloatingActionButton

    private val apiGateway = ApiGateway()
    private val productos = mutableListOf<Producto>()
    private val categorias = mutableListOf<Categoria>()
    private lateinit var adapter: ProductoAdapter

    // ⭐ Variables para la estrategia
    private lateinit var strategy: ProductoStrategy
    private var categoriaSeleccionada: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos_filter)

        drawerLayout = findViewById(R.id.drawerLayout)
        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.navigationView)

        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.app_name, R.string.app_name
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    startActivity(Intent(this, CategoriaActivity::class.java))
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_productos -> {
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_metodo_pago -> {
                    startActivity(Intent(this, MetodoPagoActivity::class.java))
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_repartidores -> {
                    startActivity(Intent(this, RepartidorActivity::class.java))
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_clientes -> {
                    startActivity(Intent(this, ClienteActivity::class.java))
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_pedidos -> {
                    startActivity(Intent(this, PedidoActivity::class.java))
                    drawerLayout.closeDrawers()
                    true
                }
                else -> false
            }
        }

        inicializarVistas()
        configurarRecyclerView()
        cargarCategorias()
        cargarProductos()
    }

    private fun inicializarVistas() {
        recyclerView = findViewById(R.id.recyclerView)
        spinnerCategorias = findViewById(R.id.spinnerCategorias)
        tvTitulo = findViewById(R.id.tvTitulo)
        fabAgregar = findViewById(R.id.fabAgregar)
        fabGenerarPdf = findViewById(R.id.fabGenerarPdf)

        // ⭐ Click en FAB para agregar producto
        fabAgregar.setOnClickListener {
            mostrarDialogCrearProducto()
        }

        // ⭐ Click en FAB para generar PDF
        fabGenerarPdf.setOnClickListener {
            generarYCompartirPdf()
        }

        // ⭐ Configurar spinner
        spinnerCategorias.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                if (position == 0) {
                    // "Todos los productos"
                    categoriaSeleccionada = 0
                    tvTitulo.text = "Todos los Productos"
                    strategy = ObtenerTodosProductos()
                } else {
                    // Categoría específica
                    val categoria = categorias[position - 1]
                    categoriaSeleccionada = categoria.id
                    tvTitulo.text = "Productos - ${categoria.nombre}"
                    strategy = ObtenerProductosPorCategoria(categoria.id)
                }
                cargarProductos()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun cargarCategorias() {
        apiGateway.obtenerCategorias(object : ApiGateway.ApiCallback<List<Categoria>> {
            override fun onSuccess(data: List<Categoria>) {
                runOnUiThread {
                    categorias.clear()
                    categorias.addAll(data)

                    // ⭐ Crear lista para el spinner con "Todos" al inicio
                    val items = mutableListOf<String>("Todos los Productos")
                    items.addAll(categorias.map { it.nombre })

                    val spinnerAdapter = ArrayAdapter(
                        this@ProductosFilterActivity,
                        android.R.layout.simple_spinner_item,
                        items
                    )
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerCategorias.adapter = spinnerAdapter
                }
            }

            override fun onError(error: String) {
                runOnUiThread {
                    Toast.makeText(this@ProductosFilterActivity, "Error: $error", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun configurarRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ProductoAdapter(
            productos,
            onItemClick = { producto ->
                Toast.makeText(this, "Producto: ${producto.nombre}", Toast.LENGTH_SHORT).show()
            },
            onRefresh = { cargarProductos() }
        )
        recyclerView.adapter = adapter
    }

    fun cargarProductos() {
        Log.d("ProductosFilterActivity", "Cargando productos (Strategy aplicada)...")

        // ⭐ La estrategia se asigna en el spinner
        // Si es primera vez, inicializar con "todos"
        if (!::strategy.isInitialized) {
            strategy = ObtenerTodosProductos()
        }

        // ⭐ Obtener la estrategia actual (importante para editar/eliminar)
        val estrategiaActual = if (::strategy.isInitialized) strategy else ObtenerTodosProductos()

        estrategiaActual.obtenerProductos(apiGateway, object : ApiGateway.ApiCallback<List<Producto>> {
            override fun onSuccess(data: List<Producto>) {
                runOnUiThread {
                    productos.clear()
                    productos.addAll(data)
                    adapter.notifyDataSetChanged()
                    Log.d("ProductosFilterActivity", "Productos cargados: ${data.size}")
                }
            }

            override fun onError(error: String) {
                runOnUiThread {
                    Toast.makeText(this@ProductosFilterActivity, "Error: $error", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun mostrarDialogCrearProducto() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Nuevo Producto")

        val layout = layoutInflater.inflate(R.layout.dialog_producto, null)
        builder.setView(layout)

        val etNombre = layout.findViewById<EditText>(R.id.etNombre)
        val etPrecio = layout.findViewById<EditText>(R.id.etPrecio)
        val etDescripcion = layout.findViewById<EditText>(R.id.etDescripcion)
        val etStock = layout.findViewById<EditText>(R.id.etStock)

        // ⭐ Crear spinner de categorías dentro del dialog
        val spinnerCategorias = Spinner(this)
        val categoriasNames = categorias.map { it.nombre }.toMutableList()
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoriasNames)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategorias.adapter = spinnerAdapter

        // ⭐ Agregar el spinner al layout del dialog
        val container = layout as? android.widget.LinearLayout
        container?.addView(spinnerCategorias, 4)

        builder.setPositiveButton("Guardar") { _, _ ->
            val posicion = spinnerCategorias.selectedItemPosition
            if (posicion >= 0 && posicion < categorias.size) {
                val categoriaSeleccionada = categorias[posicion].id

                val nuevo = Producto(
                    id = 0,
                    nombre = etNombre.text.toString(),
                    precio = etPrecio.text.toString().toDouble(),
                    descripcion = etDescripcion.text.toString(),
                    stock = etStock.text.toString().toInt(),
                    categoriaId = categoriaSeleccionada
                )
                apiGateway.crearProducto(nuevo, object : ApiGateway.ApiCallback<Boolean> {
                    override fun onSuccess(data: Boolean) {
                        runOnUiThread {
                            Toast.makeText(this@ProductosFilterActivity, "Producto creado", Toast.LENGTH_SHORT).show()
                            cargarProductos()
                        }
                    }

                    override fun onError(error: String) {
                        runOnUiThread {
                            Toast.makeText(
                                this@ProductosFilterActivity,
                                "Error: $error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
            } else {
                Toast.makeText(this@ProductosFilterActivity, "Selecciona una categoría", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun generarYCompartirPdf() {
        if (productos.isEmpty()) {
            Toast.makeText(this, "No hay productos para generar catálogo", Toast.LENGTH_SHORT).show()
            return
        }

        val pdfGenerator = CatalogoPdfGenerator()
        
        // ⭐ Si está en "Todos los productos", generar catálogo general
        val titulo = if (categoriaSeleccionada == 0) {
            "Catálogo General"
        } else {
            val categoriaActual = categorias.find { it.id == categoriaSeleccionada }
            categoriaActual?.nombre ?: "Productos"
        }

        pdfGenerator.generarCatalogoPdf(this, titulo, productos) { rutaPdf ->
            runOnUiThread {
                if (rutaPdf != null) {
                    Toast.makeText(this, "PDF generado", Toast.LENGTH_SHORT).show()
                    mostrarDialogoCompartir(rutaPdf)
                } else {
                    Toast.makeText(this, "Error al generar PDF", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun mostrarDialogoCompartir(rutaPdf: String) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Catálogo generado")
        builder.setMessage("PDF creado exitosamente")

        builder.setPositiveButton("Enviar a WhatsApp") { _, _ ->
            WhatsAppHelper.compartirPdfPorWhatsApp(this, rutaPdf)
        }

        builder.setNeutralButton("Compartir con otras apps") { _, _ ->
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                val uri = androidx.core.content.FileProvider.getUriForFile(
                    this@ProductosFilterActivity,
                    "com.example.parcialarqui.fileprovider",
                    File(rutaPdf)
                )
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "Catálogo de productos")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Compartir catálogo"))
        }

        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }
}
