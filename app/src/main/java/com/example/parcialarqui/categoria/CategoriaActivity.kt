package com.example.parcialarqui.categoria

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
import com.example.parcialarqui.R
import com.example.parcialarqui.producto.ProductosActivity

class CategoriaActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var etBuscar: EditText
    private lateinit var tvTitulo: TextView
    private val apiGateway = ApiGateway()
    private val categorias = mutableListOf<Categoria>()
    private lateinit var adapter: CategoriaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categoria)

        inicializarVistas()
        configurarRecyclerView()
        cargarCategorias()
    }

    private fun inicializarVistas() {
        recyclerView = findViewById(R.id.recyclerView)
        etBuscar = findViewById(R.id.etBuscar)
        tvTitulo = findViewById(R.id.tvTitulo)

        tvTitulo.text = "Categorías"
    }

    private fun configurarRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = CategoriaAdapter(categorias) { categoria ->
            // Al hacer click en una categoría, abrir ProductosActivity
            val intent = Intent(this, ProductosActivity::class.java)
            intent.putExtra("categoria_id", categoria.id)
            intent.putExtra("categoria_nombre", categoria.nombre)
            startActivity(intent)
        }

        recyclerView.adapter = adapter
    }

    private fun cargarCategorias() {
        Log.d("CategoriaActivity", "Cargando categorías...")

        apiGateway.obtenerCategorias(object : ApiGateway.ApiCallback<List<Categoria>> {
            override fun onSuccess(data: List<Categoria>) {
                Log.d("CategoriaActivity", "Categorías cargadas: ${data.size}")

                runOnUiThread {
                    categorias.clear()
                    categorias.addAll(data)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onError(error: String) {
                Log.e("CategoriaActivity", "Error: $error")
                runOnUiThread {
                    Toast.makeText(this@CategoriaActivity, "Error: $error", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}
