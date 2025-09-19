package com.example.parcialarqui.producto

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parcialarqui.ApiGateway
import com.example.parcialarqui.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProductosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var etBuscar: EditText
    private lateinit var tvTitulo: TextView
    private lateinit var fabAgregar: FloatingActionButton
    private lateinit var bottomNav: BottomNavigationView

    private val apiGateway = ApiGateway()
    private val productos = mutableListOf<Producto>()
    private lateinit var adapter: ProductoAdapter

    private var categoriaId: Int = 0
    private var categoriaNombre: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)

        obtenerDatosIntent()
        inicializarVistas()
        configurarRecyclerView()
        cargarProductos()

        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)

        // 👇 marcar "Inicio" aunque estés viendo productos
        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    finish() // vuelve a Categorías
                    true
                }
                R.id.nav_car -> {
                    // si algún día quieres que abra DeliveryActivity
                    // val intent = Intent(this, DeliveryActivity::class.java)
                    // startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }


    private fun obtenerDatosIntent() {
        categoriaId = intent.getIntExtra("categoria_id", 0)
        categoriaNombre = intent.getStringExtra("categoria_nombre") ?: ""
    }

    private fun inicializarVistas() {
        recyclerView = findViewById(R.id.recyclerView)
        etBuscar = findViewById(R.id.etBuscar)
        tvTitulo = findViewById(R.id.tvTitulo)
        fabAgregar = findViewById(R.id.fabAgregar)
        bottomNav = findViewById(R.id.bottomNav) // 👈 inicializar aquí

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

    private fun configurarBottomNav() {
        bottomNav.selectedItemId = R.id.nav_car // marcar activo

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    finish() // vuelve a Categorías
                    true
                }
                R.id.nav_car -> true // ya estás aquí
                else -> false
            }
        }
    }

    fun cargarProductos() {
        Log.d("ProductosActivity", "Cargando productos para categoría: $categoriaId")

        val callback = object : ApiGateway.ApiCallback<List<Producto>> {
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
        }

        if (categoriaId == 0) {
            apiGateway.obtenerProductos(callback)
        } else {
            apiGateway.obtenerProductosPorCategoria(categoriaId, callback)
        }
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
