package com.example.parcialarqui.producto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
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
import com.example.parcialarqui.categoria.CategoriaActivity
import com.example.parcialarqui.cliente.ClienteActivity
import com.example.parcialarqui.metodopago.MetodoPagoActivity
import com.example.parcialarqui.pedido.PedidoActivity
import com.example.parcialarqui.repartidor.RepartidorActivity
import com.google.android.material.navigation.NavigationView

class ProductosGeneralActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var etBuscar: EditText
    private lateinit var tvTitulo: TextView

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navigationView: NavigationView

    private val apiGateway = ApiGateway()
    private val productos = mutableListOf<Producto>()
    private lateinit var adapter: ProductoAdapter

    // ⭐ Estrategia para obtener TODOS los productos (sin categoría)
    private lateinit var strategy: ProductoStrategy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos_general)

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
                    // ⭐ Navegar a las categorías
                    startActivity(Intent(this, CategoriaActivity::class.java))
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

        // ⭐ IMPORTANTE: Usar estrategia para obtener TODOS los productos
        strategy = ObtenerTodosProductos()

        configurarRecyclerView()
        cargarProductos()
    }

    private fun inicializarVistas() {
        recyclerView = findViewById(R.id.recyclerView)
        etBuscar = findViewById(R.id.etBuscar)
        tvTitulo = findViewById(R.id.tvTitulo)

        tvTitulo.text = "Todos los Productos"
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
        Log.d("ProductosGeneralActivity", "Cargando TODOS los productos...")

        // ⭐ Usa la estrategia para obtener todos los productos
        strategy.obtenerProductos(apiGateway, object : ApiGateway.ApiCallback<List<Producto>> {
            override fun onSuccess(data: List<Producto>) {
                runOnUiThread {
                    productos.clear()
                    productos.addAll(data)
                    adapter.notifyDataSetChanged()
                    Log.d("ProductosGeneralActivity", "Productos cargados: ${data.size}")
                }
            }

            override fun onError(error: String) {
                runOnUiThread {
                    Toast.makeText(this@ProductosGeneralActivity, "Error: $error", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}
