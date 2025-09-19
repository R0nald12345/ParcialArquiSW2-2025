package com.example.parcialarqui.repartidor

import android.content.Intent
import android.os.Bundle
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
import com.example.parcialarqui.MainActivity
import com.example.parcialarqui.R
import com.example.parcialarqui.metodopago.MetodoPagoActivity
import com.google.android.material.navigation.NavigationView

class RepartidorActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var etBuscar: EditText
    private lateinit var tvTitulo: TextView
    private lateinit var btnAgregar: ImageView

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    private val apiGateway = ApiGateway()
    private val repartidores = mutableListOf<Repartidor>()
    private lateinit var adapter: RepartidorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repartidor)

        // Configurar drawer y toolbar
        configurarDrawerYToolbar()

        // Inicializar vistas y configurar
        inicializarVistas()
        configurarRecyclerView()
        cargarRepartidores()
    }

    private fun configurarDrawerYToolbar() {
        drawerLayout = findViewById(R.id.drawerLayout)
        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.navigationView)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Navegación del drawer
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
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
                    // Ya estás en Repartidores
                    drawerLayout.closeDrawers()
                    true
                }
                else -> false
            }
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    private fun inicializarVistas() {
        recyclerView = findViewById(R.id.recyclerViewRepartidores)
        etBuscar = findViewById(R.id.etBuscar)
        tvTitulo = findViewById(R.id.tvTitulo)
        btnAgregar = findViewById(R.id.btnAgregar)

        tvTitulo.text = "Repartidores"

        btnAgregar.setOnClickListener {
            mostrarDialogCrearRepartidor()
        }
    }

    private fun configurarRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RepartidorAdapter(repartidores) {
            cargarRepartidores() // Recargar cuando se edite o elimine
        }
        recyclerView.adapter = adapter
    }

    private fun cargarRepartidores() {
        apiGateway.obtenerRepartidores(object : ApiGateway.ApiCallback<List<Repartidor>> {
            override fun onSuccess(data: List<Repartidor>) {
                runOnUiThread {
                    repartidores.clear()
                    repartidores.addAll(data)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onError(error: String) {
                runOnUiThread {
                    Toast.makeText(
                        this@RepartidorActivity,
                        "Error: $error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    private fun mostrarDialogCrearRepartidor() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Nuevo Repartidor")

        val layout = layoutInflater.inflate(R.layout.dialog_repartidor, null)
        builder.setView(layout)

        val etNombre = layout.findViewById<EditText>(R.id.etNombre)
        val etCi = layout.findViewById<EditText>(R.id.etCi)
        val etPlaca = layout.findViewById<EditText>(R.id.etPlaca)
        val etCelular = layout.findViewById<EditText>(R.id.etCelular)

        builder.setPositiveButton("Guardar") { _, _ ->
            val nuevo = Repartidor(
                id = 0,
                nombre = etNombre.text.toString(),
                ci = etCi.text.toString(),
                placa = etPlaca.text.toString(),
                celular = etCelular.text.toString()
            )

            apiGateway.crearRepartidor(nuevo, object : ApiGateway.ApiCallback<Boolean> {
                override fun onSuccess(data: Boolean) {
                    runOnUiThread {
                        cargarRepartidores()
                        Toast.makeText(this@RepartidorActivity, "Repartidor creado", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(error: String) {
                    runOnUiThread {
                        Toast.makeText(this@RepartidorActivity, "Error: $error", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }

        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    fun mostrarDialogActualizarRepartidor(repartidor: Repartidor) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Editar Repartidor")

        val layout = layoutInflater.inflate(R.layout.dialog_repartidor, null)
        builder.setView(layout)

        val etNombre = layout.findViewById<EditText>(R.id.etNombre)
        val etCi = layout.findViewById<EditText>(R.id.etCi)
        val etPlaca = layout.findViewById<EditText>(R.id.etPlaca)
        val etCelular = layout.findViewById<EditText>(R.id.etCelular)

        // Llenar con datos actuales
        etNombre.setText(repartidor.nombre)
        etCi.setText(repartidor.ci)
        etPlaca.setText(repartidor.placa)
        etCelular.setText(repartidor.celular)

        builder.setPositiveButton("Actualizar") { _, _ ->
            val actualizado = Repartidor(
                id = repartidor.id,
                nombre = etNombre.text.toString(),
                ci = etCi.text.toString(),
                placa = etPlaca.text.toString(),
                celular = etCelular.text.toString()
            )

            apiGateway.actualizarRepartidor(actualizado, object : ApiGateway.ApiCallback<Boolean> {
                override fun onSuccess(data: Boolean) {
                    runOnUiThread {
                        cargarRepartidores()
                        Toast.makeText(this@RepartidorActivity, "Repartidor actualizado", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(error: String) {
                    runOnUiThread {
                        Toast.makeText(
                            this@RepartidorActivity,
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
