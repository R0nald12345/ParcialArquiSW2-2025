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

class ProductosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var etBuscar: EditText
    private lateinit var tvTitulo: TextView
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
    }

    private fun obtenerDatosIntent() {
        categoriaId = intent.getIntExtra("categoria_id", 0)
        categoriaNombre = intent.getStringExtra("categoria_nombre") ?: ""
    }

    private fun inicializarVistas() {
        recyclerView = findViewById(R.id.recyclerView)
        etBuscar = findViewById(R.id.etBuscar)
        tvTitulo = findViewById(R.id.tvTitulo)

        tvTitulo.text = "Productos ($categoriaNombre)"
    }

    private fun configurarRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ProductoAdapter(productos) { producto ->
            // Al hacer click en un producto
            Toast.makeText(this, "Producto: ${producto.nombre}", Toast.LENGTH_SHORT).show()
        }

        recyclerView.adapter = adapter
    }

    private fun cargarProductos() {
        Log.d("ProductosActivity", "Cargando productos para categoría: $categoriaId")

        if (categoriaId == 0) {
            // Cargar todos los productos
            apiGateway.obtenerProductos(object : ApiGateway.ApiCallback<List<Producto>> {
                override fun onSuccess(data: List<Producto>) {
                    Log.d("ProductosActivity", "Productos cargados: ${data.size}")

                    runOnUiThread {
                        productos.clear()
                        productos.addAll(data)
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onError(error: String) {
                    Log.e("ProductosActivity", "Error: $error")
                    runOnUiThread {
                        Toast.makeText(this@ProductosActivity, "Error: $error", Toast.LENGTH_LONG).show()
                    }
                }
            })
        } else {
            // Cargar productos por categoría
            apiGateway.obtenerProductosPorCategoria(categoriaId, object : ApiGateway.ApiCallback<List<Producto>> {
                override fun onSuccess(data: List<Producto>) {
                    Log.d("ProductosActivity", "Productos de categoría cargados: ${data.size}")

                    runOnUiThread {
                        productos.clear()
                        productos.addAll(data)
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onError(error: String) {
                    Log.e("ProductosActivity", "Error: $error")
                    runOnUiThread {
                        Toast.makeText(this@ProductosActivity, "Error: $error", Toast.LENGTH_LONG).show()
                    }
                }
            })
        }
    }
}