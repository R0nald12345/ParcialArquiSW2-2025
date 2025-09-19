package com.example.parcialarqui.categoria

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parcialarqui.ApiGateway
import com.example.parcialarqui.R
import com.example.parcialarqui.cliente.ClienteActivity
import com.example.parcialarqui.metodopago.MetodoPagoActivity
import com.example.parcialarqui.pedido.PedidoActivity
import com.example.parcialarqui.producto.ProductosActivity
import com.example.parcialarqui.repartidor.RepartidorActivity
import com.google.android.material.navigation.NavigationView

class CategoriaActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var etBuscar: EditText
    private lateinit var tvTitulo: TextView
    private lateinit var btnAgregar: ImageView

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navigationView: NavigationView

    private val apiGateway = ApiGateway()
    private val categorias = mutableListOf<Categoria>()
    private lateinit var adapter: CategoriaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categoria)

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
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_metodo_pago -> {
                    val intent = Intent(this, MetodoPagoActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_repartidores -> {
                    val intent = Intent(this, RepartidorActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_clientes -> {
                    val intent = Intent(this, ClienteActivity::class.java)
                    startActivity(intent)
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

        // 👉 estas 3 llamadas faltaban
        inicializarVistas()
        configurarRecyclerView()
        cargarCategorias()
    }


    private fun configurarBottomNav() {
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_home // 👉 marcar "casa" activo

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Ya estás en Categorías
                    true
                }
                R.id.nav_car -> {
                    val intent = Intent(this, ProductosActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }


    private fun inicializarVistas() {
        recyclerView = findViewById(R.id.recyclerView)
        etBuscar = findViewById(R.id.etBuscar)
        tvTitulo = findViewById(R.id.tvTitulo)
        btnAgregar = findViewById(R.id.btnAgregar)

        tvTitulo.text = "Categorías"

        btnAgregar.setOnClickListener {
            mostrarDialogCrearCategoria()
        }
    }

    private fun configurarRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = CategoriaAdapter(
            lista = categorias,
            onItemClick = { categoria ->
                // Abrir productos de la categoría seleccionada
                val intent = Intent(this, ProductosActivity::class.java)
                intent.putExtra("categoria_id", categoria.id)
                intent.putExtra("categoria_nombre", categoria.nombre)
                startActivity(intent)
            },
            onRefresh = {
                cargarCategorias() // Recargar cuando se edite o elimine
            }
        )

        recyclerView.adapter = adapter
    }

    fun cargarCategorias() { // lo dejo público para el adapter si lo necesite
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
                    Toast.makeText(
                        this@CategoriaActivity,
                        "Error: $error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    private fun mostrarDialogCrearCategoria() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Nueva Categoría")

        val layout = layoutInflater.inflate(R.layout.dialog_categoria, null)
        builder.setView(layout)

        val etNombre = layout.findViewById<EditText>(R.id.etNombre)
        val etDescripcion = layout.findViewById<EditText>(R.id.etDescripcion)

        builder.setPositiveButton("Guardar") { _, _ ->
            val nueva = Categoria(
                id = 0,
                nombre = etNombre.text.toString(),
                descripcion = etDescripcion.text.toString(),
            )
            apiGateway.crearCategoria(nueva, object : ApiGateway.ApiCallback<Boolean> {
                override fun onSuccess(data: Boolean) {
                    runOnUiThread { cargarCategorias() }
                }

                override fun onError(error: String) {
                    runOnUiThread {
                        Toast.makeText(
                            this@CategoriaActivity,
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
