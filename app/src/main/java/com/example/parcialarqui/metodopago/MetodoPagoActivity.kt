package com.example.parcialarqui.metodopago

import android.app.AlertDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parcialarqui.ApiGateway
import com.example.parcialarqui.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import android.content.Intent
import com.example.parcialarqui.MainActivity
import com.example.parcialarqui.repartidor.RepartidorActivity

class MetodoPagoActivity : AppCompatActivity() {

    private val apiGateway = ApiGateway()
    private val metodosPago = mutableListOf<MetodoPago>()
    private lateinit var adapter: MetodoPagoAdapter

    // Views
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var recyclerViewMetodoPago: RecyclerView
    private lateinit var fabAgregarMetodoPago: FloatingActionButton
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metodopago)

        // Inicializar vistas
        drawerLayout = findViewById(R.id.drawerLayout)
        toolbar = findViewById(R.id.toolbar)
        recyclerViewMetodoPago = findViewById(R.id.recyclerViewMetodoPago)
        fabAgregarMetodoPago = findViewById(R.id.fabAgregarMetodoPago)
        navigationView = findViewById(R.id.navigationView)

        // 👇 CONFIGURAR CORRECTAMENTE EL TOOLBAR Y DRAWER
        setSupportActionBar(toolbar)

        // Habilitar el botón "Up" en el ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        // Crear el toggle para el drawer
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,  // 👈 Cambiar estos strings
            R.string.navigation_drawer_close  // 👈 Cambiar estos strings
        )

        // Configurar el drawer
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState() // 👈 Esto es crucial para sincronizar el estado

        // RecyclerView
        recyclerViewMetodoPago.layoutManager = LinearLayoutManager(this)
        adapter = MetodoPagoAdapter(metodosPago) { cargarMetodosPago() }
        recyclerViewMetodoPago.adapter = adapter

        // Botón agregar
        fabAgregarMetodoPago.setOnClickListener { mostrarDialogCrearMetodoPago() }

        cargarMetodosPago()

        // Listener de navegación
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_metodo_pago -> {
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_repartidores -> {
                    val intent = Intent(this, RepartidorActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawers()
                    true
                }
                else -> false
            }
        }
    }

    // 👇 AGREGAR ESTE MÉTODO PARA MANEJAR LA CONFIGURACIÓN DESPUÉS DE CREAR LA ACTIVIDAD
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    // 👇 AGREGAR ESTE MÉTODO PARA MANEJAR CAMBIOS DE CONFIGURACIÓN
    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    private fun cargarMetodosPago() {
        apiGateway.obtenerMetodosPago(object : ApiGateway.ApiCallback<List<MetodoPago>> {
            override fun onSuccess(data: List<MetodoPago>) {
                runOnUiThread {
                    metodosPago.clear()
                    metodosPago.addAll(data)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onError(error: String) {
                runOnUiThread {
                    Toast.makeText(this@MetodoPagoActivity, error, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun mostrarDialogCrearMetodoPago() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Nuevo Método de Pago")

        val layout = layoutInflater.inflate(R.layout.dialog_metodopago, null)
        builder.setView(layout)

        val etNombre = layout.findViewById<EditText>(R.id.etNombreMetodoPago)

        builder.setPositiveButton("Guardar") { _, _ ->
            val nuevo = MetodoPago(id = 0, nombre = etNombre.text.toString())
            apiGateway.crearMetodoPago(nuevo, object : ApiGateway.ApiCallback<Boolean> {
                override fun onSuccess(data: Boolean) {
                    runOnUiThread { cargarMetodosPago() }
                }

                override fun onError(error: String) {
                    runOnUiThread {
                        Toast.makeText(this@MetodoPagoActivity, error, Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    fun mostrarDialogActualizarMetodoPago(metodoPago: MetodoPago) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar Método de Pago")

        val layout = layoutInflater.inflate(R.layout.dialog_metodopago, null)
        builder.setView(layout)

        val etNombre = layout.findViewById<EditText>(R.id.etNombreMetodoPago)
        etNombre.setText(metodoPago.nombre)

        builder.setPositiveButton("Actualizar") { _, _ ->
            val actualizado = MetodoPago(
                id = metodoPago.id,
                nombre = etNombre.text.toString()
            )
            apiGateway.actualizarMetodoPago(actualizado, object : ApiGateway.ApiCallback<Boolean> {
                override fun onSuccess(data: Boolean) {
                    runOnUiThread { cargarMetodosPago() }
                }

                override fun onError(error: String) {
                    runOnUiThread {
                        Toast.makeText(this@MetodoPagoActivity, error, Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

}