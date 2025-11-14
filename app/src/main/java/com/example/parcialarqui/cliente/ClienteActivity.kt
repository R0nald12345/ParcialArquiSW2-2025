package com.example.parcialarqui.cliente

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
import com.example.parcialarqui.repartidor.RepartidorActivity
import com.google.android.material.navigation.NavigationView

class ClienteActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var etBuscar: EditText
    private lateinit var tvTitulo: TextView
    private lateinit var btnAgregar: ImageView

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    private val apiGateway = ApiGateway()
    private val clientes = mutableListOf<Cliente>()
    private lateinit var adapter: ClienteAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cliente)

        configurarDrawerYToolbar()
        inicializarVistas()
        configurarRecyclerView()
        cargarClientes()
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

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    startActivity(Intent(this, MainActivity::class.java))
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
        recyclerView = findViewById(R.id.recyclerViewClientes)
        etBuscar = findViewById(R.id.etBuscar)
        tvTitulo = findViewById(R.id.tvTitulo)
        btnAgregar = findViewById(R.id.btnAgregar)

        tvTitulo.text = "Clientes"

        btnAgregar.setOnClickListener {
            mostrarDialogCrearCliente()
        }
    }

    private fun configurarRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ClienteAdapter(clientes) { cargarClientes() }
        recyclerView.adapter = adapter
    }

    private fun cargarClientes() {
        apiGateway.obtenerClientes(object : ApiGateway.ApiCallback<List<Cliente>> {
            override fun onSuccess(data: List<Cliente>) {
                runOnUiThread {
                    clientes.clear()
                    clientes.addAll(data)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onError(error: String) {
                runOnUiThread {
                    Toast.makeText(this@ClienteActivity, "Error: $error", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    // 👉 función para extraer coordenadas desde un link de Maps
    private fun parseCoordenadas(texto: String): Pair<Double?, Double?> {
        val regex = Regex("(-?\\d+\\.\\d+),\\s*(-?\\d+\\.\\d+)")
        val match = regex.find(texto)

        return if (match != null && match.groupValues.size == 3) {
            val lat = match.groupValues[1].toDoubleOrNull()
            val lng = match.groupValues[2].toDoubleOrNull()
            Pair(lat, lng)
        } else {
            Pair(null, null)
        }
    }

    private fun mostrarDialogCrearCliente() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Nuevo Cliente")

        val layout = layoutInflater.inflate(R.layout.dialog_cliente, null)
        builder.setView(layout)

        val etNombre = layout.findViewById<EditText>(R.id.etNombre)
        val etTelefono = layout.findViewById<EditText>(R.id.etTelefono)
        val etEmail = layout.findViewById<EditText>(R.id.etEmail)
        val etDireccion = layout.findViewById<EditText>(R.id.etDireccion)
        val etUbicacion = layout.findViewById<EditText>(R.id.etUbicacion)

        builder.setPositiveButton("Guardar") { _, _ ->
            try {
                val (lat, lng) = parseCoordenadas(etUbicacion.text.toString())

                // ⭐ PATRÓN BUILDER CON DIRECTOR
                val clienteBuilder = ClienteNuevoBuilder()
                val director = ClienteDirector(clienteBuilder)

                val nuevo = director.construirClienteNuevo(
                    nombre = etNombre.text.toString(),
                    telefono = etTelefono.text.toString(),
                    email = etEmail.text.toString(),
                    direccion = etDireccion.text.toString(),
                    coordenadaX = lat ?: 0.0,
                    coordenadaY = lng ?: 0.0
                )

                apiGateway.crearCliente(nuevo, object : ApiGateway.ApiCallback<Boolean> {
                    override fun onSuccess(data: Boolean) {
                        runOnUiThread {
                            cargarClientes()
                            Toast.makeText(this@ClienteActivity, "Cliente creado", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onError(error: String) {
                        runOnUiThread {
                            Toast.makeText(this@ClienteActivity, "Error: $error", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            } catch (e: IllegalArgumentException) {
                Toast.makeText(this@ClienteActivity, "Validación: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    fun mostrarDialogActualizarCliente(cliente: Cliente) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Editar Cliente")

        val layout = layoutInflater.inflate(R.layout.dialog_cliente, null)
        builder.setView(layout)

        val etNombre = layout.findViewById<EditText>(R.id.etNombre)
        val etTelefono = layout.findViewById<EditText>(R.id.etTelefono)
        val etEmail = layout.findViewById<EditText>(R.id.etEmail)
        val etDireccion = layout.findViewById<EditText>(R.id.etDireccion)
        val etUbicacion = layout.findViewById<EditText>(R.id.etUbicacion)

        etNombre.setText(cliente.nombre)
        etTelefono.setText(cliente.telefono)
        etEmail.setText(cliente.email)
        etDireccion.setText(cliente.direccion)

        // si ya tiene coordenadas, las mostramos como "lat,lng"
        if (cliente.coordenadaX != 0.0 && cliente.coordenadaY != 0.0) {
            etUbicacion.setText("${cliente.coordenadaX},${cliente.coordenadaY}")
        }

        builder.setPositiveButton("Actualizar") { _, _ ->
            try {
                val (lat, lng) = parseCoordenadas(etUbicacion.text.toString())

                // ⭐ PATRÓN BUILDER CON DIRECTOR
                val clienteBuilder = ClienteExistenteBuilder(cliente)
                val director = ClienteDirector(clienteBuilder)

                val actualizado = director.construirClienteExistente(
                    nombre = etNombre.text.toString(),
                    telefono = etTelefono.text.toString(),
                    email = etEmail.text.toString(),
                    direccion = etDireccion.text.toString(),
                    coordenadaX = lat ?: 0.0,
                    coordenadaY = lng ?: 0.0,
                    fechaRegistro = cliente.fechaRegistro
                )

                apiGateway.actualizarCliente(actualizado, object : ApiGateway.ApiCallback<Boolean> {
                    override fun onSuccess(data: Boolean) {
                        runOnUiThread {
                            cargarClientes()
                            Toast.makeText(this@ClienteActivity, "Cliente actualizado", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onError(error: String) {
                        runOnUiThread {
                            Toast.makeText(this@ClienteActivity, "Error: $error", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            } catch (e: IllegalArgumentException) {
                Toast.makeText(this@ClienteActivity, "Validación: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }
}
