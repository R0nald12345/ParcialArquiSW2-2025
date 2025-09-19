package com.example.parcialarqui.cliente

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
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
import com.example.parcialarqui.MainActivity
import com.example.parcialarqui.R
import com.example.parcialarqui.metodopago.MetodoPago
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
    private val metodosPago = mutableListOf<MetodoPago>()
    private lateinit var adapter: ClienteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cliente)

        // Configurar drawer y toolbar
        configurarDrawerYToolbar()

        // Inicializar vistas y configurar
        inicializarVistas()
        configurarRecyclerView()
        cargarMetodosPago()
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
                    val intent = Intent(this, RepartidorActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_clientes -> {
                    // Ya estás en Clientes
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
        adapter = ClienteAdapter(clientes) {
            cargarClientes() // Recargar cuando se edite o elimine
        }
        recyclerView.adapter = adapter
    }

    private fun cargarMetodosPago() {
        apiGateway.obtenerMetodosPago(object : ApiGateway.ApiCallback<List<MetodoPago>> {
            override fun onSuccess(data: List<MetodoPago>) {
                runOnUiThread {
                    metodosPago.clear()
                    metodosPago.addAll(data)
                }
            }

            override fun onError(error: String) {
                runOnUiThread {
                    Toast.makeText(
                        this@ClienteActivity,
                        "Error al cargar métodos de pago: $error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
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
                    Toast.makeText(
                        this@ClienteActivity,
                        "Error: $error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
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
        val etCoordenadaX = layout.findViewById<EditText>(R.id.etCoordenadaX)
        val etCoordenadaY = layout.findViewById<EditText>(R.id.etCoordenadaY)
        val spinnerMetodoPago = layout.findViewById<Spinner>(R.id.spinnerMetodoPago)

        // Configurar spinner con métodos de pago
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            metodosPago.map { it.nombre }
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMetodoPago.adapter = spinnerAdapter

        builder.setPositiveButton("Guardar") { _, _ ->
            val selectedMetodoPago = metodosPago[spinnerMetodoPago.selectedItemPosition]

            val nuevo = Cliente(
                id = 0,
                nombre = etNombre.text.toString(),
                telefono = etTelefono.text.toString(),
                email = etEmail.text.toString(),
                direccion = etDireccion.text.toString(),
                coordenadaX = etCoordenadaX.text.toString().toDoubleOrNull() ?: 0.0,
                coordenadaY = etCoordenadaY.text.toString().toDoubleOrNull() ?: 0.0,
                fechaRegistro = "", // El servidor lo asignará
                metodoPagoId = selectedMetodoPago.id,
                metodoPagoNombre = selectedMetodoPago.nombre
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
                        Toast.makeText(
                            this@ClienteActivity,
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

    fun mostrarDialogActualizarCliente(cliente: Cliente) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Editar Cliente")

        val layout = layoutInflater.inflate(R.layout.dialog_cliente, null)
        builder.setView(layout)

        val etNombre = layout.findViewById<EditText>(R.id.etNombre)
        val etTelefono = layout.findViewById<EditText>(R.id.etTelefono)
        val etEmail = layout.findViewById<EditText>(R.id.etEmail)
        val etDireccion = layout.findViewById<EditText>(R.id.etDireccion)
        val etCoordenadaX = layout.findViewById<EditText>(R.id.etCoordenadaX)
        val etCoordenadaY = layout.findViewById<EditText>(R.id.etCoordenadaY)
        val spinnerMetodoPago = layout.findViewById<Spinner>(R.id.spinnerMetodoPago)

        // Llenar con datos actuales
        etNombre.setText(cliente.nombre)
        etTelefono.setText(cliente.telefono)
        etEmail.setText(cliente.email)
        etDireccion.setText(cliente.direccion)
        etCoordenadaX.setText(cliente.coordenadaX.toString())
        etCoordenadaY.setText(cliente.coordenadaY.toString())

        // Configurar spinner con métodos de pago
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            metodosPago.map { it.nombre }
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMetodoPago.adapter = spinnerAdapter

        // Seleccionar método de pago actual
        val currentMethodIndex = metodosPago.indexOfFirst { it.id == cliente.metodoPagoId }
        if (currentMethodIndex >= 0) {
            spinnerMetodoPago.setSelection(currentMethodIndex)
        }

        builder.setPositiveButton("Actualizar") { _, _ ->
            val selectedMetodoPago = metodosPago[spinnerMetodoPago.selectedItemPosition]

            val actualizado = Cliente(
                id = cliente.id,
                nombre = etNombre.text.toString(),
                telefono = etTelefono.text.toString(),
                email = etEmail.text.toString(),
                direccion = etDireccion.text.toString(),
                coordenadaX = etCoordenadaX.text.toString().toDoubleOrNull() ?: 0.0,
                coordenadaY = etCoordenadaY.text.toString().toDoubleOrNull() ?: 0.0,
                fechaRegistro = cliente.fechaRegistro, // Mantener fecha original
                metodoPagoId = selectedMetodoPago.id,
                metodoPagoNombre = selectedMetodoPago.nombre
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
                        Toast.makeText(
                            this@ClienteActivity,
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