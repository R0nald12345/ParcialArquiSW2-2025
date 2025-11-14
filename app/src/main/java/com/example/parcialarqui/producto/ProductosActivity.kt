package com.example.parcialarqui.producto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parcialarqui.ApiGateway
import com.example.parcialarqui.GeneradorCatalogo.CatalogoPdfGenerator
import com.example.parcialarqui.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class ProductosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var etBuscar: EditText
    private lateinit var tvTitulo: TextView
    private lateinit var fabAgregar: FloatingActionButton
    private lateinit var btnGenerarCatalogo: FloatingActionButton
    private var categoria: com.example.parcialarqui.categoria.Categoria? = null

    private val apiGateway = ApiGateway()
    private val productos = mutableListOf<Producto>()
    private lateinit var adapter: ProductoAdapter

    private var categoriaId: Int = 0
    private var categoriaNombre: String = ""
    private lateinit var btnVolver: FloatingActionButton

    // ⭐ MODIFICADO: ahora sí tenemos la estrategia del patrón Strategy
    private lateinit var strategy: ProductoStrategy


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)

        btnVolver = findViewById(R.id.btnVolver)
        btnVolver.setOnClickListener {
            finish()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Productos"

        obtenerDatosIntent()
        inicializarVistas()

        // ⭐ MODIFICADO: asignamos strategy ANTES de cargar productos
        strategy = if (categoriaId == 0)
            ObtenerTodosProductos()              // ⭐ MODIFICADO
        else
            ObtenerProductosPorCategoria(categoriaId) // ⭐ MODIFICADO

        configurarRecyclerView()
        cargarProductos() // ahora usa strategy

        btnGenerarCatalogo = findViewById(R.id.btnGenerarCatalogo)

        btnGenerarCatalogo.setOnClickListener {

            // ⭐ MODIFICADO: el PDF solo se genera por categoría válida (no para "todos los productos")
            if (categoriaId == 0) {
                Toast.makeText(
                    this,
                    "El catálogo solo puede generarse por categoría.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (categoria != null && productos.isNotEmpty()) {
                generarYCompartirPdfCategoria()
            } else {
                Toast.makeText(this, "No hay productos para generar catálogo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun generarYCompartirPdfCategoria() {
        val pdfGenerator = CatalogoPdfGenerator()
        categoria?.let { cat ->
            pdfGenerator.generarCatalogoCategoriaPdf(this, cat, productos) { rutaPdf ->
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
    }

    private fun mostrarDialogoCompartir(rutaPdf: String) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Catálogo generado")
        builder.setMessage("PDF creado exitosamente")

        builder.setPositiveButton("Enviar a WhatsApp") { _, _ ->
            com.example.parcialarqui.GeneradorCatalogo.WhatsAppHelper.compartirPdfPorWhatsApp(
                this, rutaPdf
            )
        }

        builder.setNeutralButton("Compartir con otras apps") { _, _ ->
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                val uri = androidx.core.content.FileProvider.getUriForFile(
                    this@ProductosActivity,
                    "com.example.parcialarqui.fileprovider",
                    File(rutaPdf)
                )
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "Catálogo de $categoriaNombre")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Compartir catálogo"))
        }

        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun obtenerDatosIntent() {
        categoriaId = intent.getIntExtra("categoria_id", 0)
        categoriaNombre = intent.getStringExtra("categoria_nombre") ?: ""
        categoria = com.example.parcialarqui.categoria.Categoria(
            id = categoriaId,
            nombre = categoriaNombre,
            descripcion = ""
        )
    }

    private fun inicializarVistas() {
        recyclerView = findViewById(R.id.recyclerView)
        etBuscar = findViewById(R.id.etBuscar)
        tvTitulo = findViewById(R.id.tvTitulo)
        fabAgregar = findViewById(R.id.fabAgregar)

        tvTitulo.text = "Productos ($categoriaNombre)"

        fabAgregar.setOnClickListener { mostrarDialogCrearProducto() }
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
        Log.d("ProductosActivity", "Cargando productos (Strategy aplicada)…")

        // ⭐ MODIFICADO: ahora NO usamos if/else, se delega completamente a Strategy
        strategy.obtenerProductos(apiGateway, object : ApiGateway.ApiCallback<List<Producto>> {
            override fun onSuccess(data: List<Producto>) {
                runOnUiThread {
                    productos.clear()
                    productos.addAll(data)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onError(error: String) {
                runOnUiThread {
                    Toast.makeText(this@ProductosActivity, "Error: $error", Toast.LENGTH_LONG).show()
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

        builder.setPositiveButton("Guardar") { _, _ ->
            val nuevo = Producto(
                id = 0,
                nombre = etNombre.text.toString(),
                precio = etPrecio.text.toString().toDouble(),
                descripcion = etDescripcion.text.toString(),
                stock = etStock.text.toString().toInt(),
                categoriaId = categoriaId
            )
            apiGateway.crearProducto(nuevo, object : ApiGateway.ApiCallback<Boolean> {
                override fun onSuccess(data: Boolean) {
                    runOnUiThread { cargarProductos() }
                }

                override fun onError(error: String) {
                    runOnUiThread {
                        Toast.makeText(
                            this@ProductosActivity,
                            "Error: $error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }

        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }
}
