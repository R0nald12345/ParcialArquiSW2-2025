package com.example.parcialarqui.pedido

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parcialarqui.ApiGateway
import com.example.parcialarqui.R
import com.example.parcialarqui.categoria.CategoriaActivity
import com.example.parcialarqui.cliente.Cliente
import com.example.parcialarqui.cliente.ClienteActivity
import com.example.parcialarqui.metodopago.MetodoPagoActivity
import com.example.parcialarqui.producto.Producto
import com.example.parcialarqui.repartidor.Repartidor
import com.example.parcialarqui.repartidor.RepartidorActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PedidoActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var etBuscar: EditText
    private lateinit var tvTitulo: TextView
    private lateinit var fabAgregar: FloatingActionButton

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    private val apiGateway = ApiGateway()
    private val pedidos = mutableListOf<PedidoCompleto>()
    private val clientes = mutableListOf<Cliente>()
    private val repartidores = mutableListOf<Repartidor>()
    private val productos = mutableListOf<Producto>()
    private val metodosPago = mutableListOf<com.example.parcialarqui.metodopago.MetodoPago>()
    private val detallesTemp = mutableListOf<DetallePedido>()
    private lateinit var adapter: PedidoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedido)

        configurarDrawerYToolbar()
        inicializarVistas()
        configurarRecyclerView()
        cargarDatosIniciales()
        configurarFAB()
    }

    private fun configurarDrawerYToolbar() {
        drawerLayout = findViewById(R.id.drawerLayout)
        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.navigationView)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
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
        recyclerView = findViewById(R.id.recyclerViewPedidos)
        etBuscar = findViewById(R.id.etBuscar)
        tvTitulo = findViewById(R.id.tvTitulo)
        fabAgregar = findViewById(R.id.fabAgregar)
        tvTitulo.text = "Pedidos"
    }

    private fun configurarRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PedidoAdapter(
            lista = pedidos,
            onEditarClick = { pedido -> mostrarDialogPedido(pedido) },
            onEliminarClick = { pedido -> confirmarEliminar(pedido) },
            onCompartirClick = { pedido -> generarYCompartirPedido(pedido) }
        )
        recyclerView.adapter = adapter
    }

    private fun configurarFAB() {
        fabAgregar.setOnClickListener {
            mostrarDialogPedido(null)
        }
    }

    private fun generarYCompartirPedido(pedido: PedidoCompleto) {
        /*val texto = """
        *PEDIDO #${String.format("%03d", pedido.id)}*
        
        📅 Fecha: ${pedido.fecha}
        👤 Cliente: ${pedido.clienteNombre}
        📞 Teléfono: ${pedido.clienteTelefono}
        🚗 Repartidor: ${pedido.repartidorNombre}
        💳 Pago: ${pedido.metodoPagoNombre}
        
        💰 *Total: Bs. ${String.format("%.2f", pedido.monto)}*
    """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, texto)
            setPackage("com.whatsapp")
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "WhatsApp no instalado", Toast.LENGTH_SHORT).show()
        }
        */
        GeneradorPedidoPdf().generarPdf(this, pedido) { path ->
            if (path != null) {
                val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", File(path))
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    setPackage("com.whatsapp")
                }
                startActivity(intent)
            }
        }

    }


    private fun cargarDatosIniciales() {
        cargarPedidos()
        cargarClientes()
        cargarRepartidores()
        cargarProductos()
        cargarMetodosPago()
    }

    private fun cargarPedidos() {
        apiGateway.obtenerPedidos(object : ApiGateway.ApiCallback<List<PedidoCompleto>> {
            override fun onSuccess(data: List<PedidoCompleto>) {
                runOnUiThread {
                    adapter.actualizarLista(data) // ✅ usar método del adapter
                }
            }

            override fun onError(error: String) {
                runOnUiThread {
                    Toast.makeText(this@PedidoActivity, "Error cargando pedidos: $error", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun cargarClientes() {
        apiGateway.obtenerClientes(object : ApiGateway.ApiCallback<List<Cliente>> {
            override fun onSuccess(data: List<Cliente>) {
                clientes.clear()
                clientes.addAll(data)
            }

            override fun onError(error: String) {
                runOnUiThread {
                    Toast.makeText(this@PedidoActivity, "Error cargando clientes: $error", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun cargarRepartidores() {
        apiGateway.obtenerRepartidores(object : ApiGateway.ApiCallback<List<Repartidor>> {
            override fun onSuccess(data: List<Repartidor>) {
                repartidores.clear()
                repartidores.addAll(data)
            }

            override fun onError(error: String) {
                runOnUiThread {
                    Toast.makeText(this@PedidoActivity, "Error cargando repartidores: $error", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun cargarProductos() {
        apiGateway.obtenerProductos(object : ApiGateway.ApiCallback<List<Producto>> {
            override fun onSuccess(data: List<Producto>) {
                productos.clear()
                productos.addAll(data)
            }

            override fun onError(error: String) {
                runOnUiThread {
                    Toast.makeText(this@PedidoActivity, "Error cargando productos: $error", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun cargarMetodosPago() {
        apiGateway.obtenerMetodosPago(object : ApiGateway.ApiCallback<List<com.example.parcialarqui.metodopago.MetodoPago>> {
            override fun onSuccess(data: List<com.example.parcialarqui.metodopago.MetodoPago>) {
                metodosPago.clear()
                metodosPago.addAll(data)
            }

            override fun onError(error: String) {
                runOnUiThread {
                    Toast.makeText(this@PedidoActivity, "Error cargando métodos de pago: $error", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun mostrarDialogPedido(pedidoExistente: PedidoCompleto?) {
        if (pedidoExistente != null) {
            apiGateway.obtenerPedidoPorId(pedidoExistente.id, object : ApiGateway.ApiCallback<PedidoCompleto> {
                override fun onSuccess(data: PedidoCompleto) {
                    runOnUiThread {
                        mostrarDialogPedidoInterno(data)
                    }
                }
                override fun onError(error: String) {
                    runOnUiThread {
                        Toast.makeText(this@PedidoActivity, "Error: $error", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } else {
            mostrarDialogPedidoInterno(null)
        }
    }

    private fun mostrarDialogPedidoInterno(pedidoExistente: PedidoCompleto?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_pedido, null)

        val actvCliente = dialogView.findViewById<AutoCompleteTextView>(R.id.actvCliente)
        val actvRepartidor = dialogView.findViewById<AutoCompleteTextView>(R.id.actvRepartidor)
        val actvProducto = dialogView.findViewById<AutoCompleteTextView>(R.id.actvProducto)
        val actvMetodoPago = dialogView.findViewById<AutoCompleteTextView>(R.id.actvMetodoPago)
        val etFecha = dialogView.findViewById<EditText>(R.id.etFecha)
        val etMonto = dialogView.findViewById<EditText>(R.id.etMonto)
        val etCantidad = dialogView.findViewById<EditText>(R.id.etCantidad)
        val etPrecio = dialogView.findViewById<EditText>(R.id.etPrecio)
        val btnAgregarDetalle = dialogView.findViewById<Button>(R.id.btnAgregarDetalle)
        val rvDetalles = dialogView.findViewById<RecyclerView>(R.id.rvDetalles)

        detallesTemp.clear()

        val clientesTextos = clientes.map { "${it.nombre} - ${it.telefono}" }
        val repartidoresTextos = repartidores.map { "${it.nombre} - ${it.placa}" }
        val productosTextos = productos.map { "${it.nombre} - Bs. ${String.format("%.2f", it.precio)}" }
        val metodosPagoTextos = metodosPago.map { it.nombre }

        val adapterClientes = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, clientesTextos)
        val adapterRepartidores = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, repartidoresTextos)
        val adapterProductos = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, productosTextos)
        val adapterMetodosPago = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, metodosPagoTextos)

        actvCliente.setAdapter(adapterClientes)
        actvCliente.threshold = 1
        actvCliente.setOnClickListener { actvCliente.showDropDown() }

        actvRepartidor.setAdapter(adapterRepartidores)
        actvRepartidor.threshold = 1
        actvRepartidor.setOnClickListener { actvRepartidor.showDropDown() }

        actvProducto.setAdapter(adapterProductos)
        actvProducto.threshold = 1
        actvProducto.setOnClickListener { actvProducto.showDropDown() }

        actvMetodoPago.setAdapter(adapterMetodosPago)
        actvMetodoPago.threshold = 1
        actvMetodoPago.setOnClickListener { actvMetodoPago.showDropDown() }

        actvProducto.setOnItemClickListener { _, _, position, _ ->
            val productoSeleccionado = productos[position]
            etPrecio.setText(productoSeleccionado.precio.toString())
        }

        lateinit var detalleAdapterLocal: DetalleAdapter
        detalleAdapterLocal = DetalleAdapter(detallesTemp, productos) { _, position ->
            detalleAdapterLocal.eliminarItem(position)
            calcularMontoTotal(etMonto)
        }
        rvDetalles.layoutManager = LinearLayoutManager(this)
        rvDetalles.adapter = detalleAdapterLocal

        btnAgregarDetalle.setOnClickListener {
            val productoSeleccionado = productos.find { "${it.nombre} - Bs. ${String.format("%.2f", it.precio)}" == actvProducto.text.toString() }
            val cantidad = etCantidad.text.toString().toIntOrNull()
            val precio = etPrecio.text.toString().toDoubleOrNull()

            if (productoSeleccionado == null || cantidad == null || precio == null || cantidad <= 0 || precio <= 0) {
                Toast.makeText(this, "Complete todos los campos del detalle correctamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val detalle = DetallePedido(pedidoExistente?.id ?: 0, productoSeleccionado.id, cantidad, precio)
            detalleAdapterLocal.agregarDetalle(detalle)
            calcularMontoTotal(etMonto)

            actvProducto.setText("")
            etCantidad.setText("")
            etPrecio.setText("")
        }

        if (pedidoExistente != null) {
            val clienteSeleccionado = clientes.find { it.id == pedidoExistente.clienteId }
            val repartidorSeleccionado = repartidores.find { it.id == pedidoExistente.repartidorId }
            val metodoPagoSeleccionado = metodosPago.find { it.id == pedidoExistente.metodoPagoId }

            if (clienteSeleccionado != null) {
                actvCliente.setText("${clienteSeleccionado.nombre} - ${clienteSeleccionado.telefono}", false)
            }
            if (repartidorSeleccionado != null) {
                actvRepartidor.setText("${repartidorSeleccionado.nombre} - ${repartidorSeleccionado.placa}", false)
            }
            if (metodoPagoSeleccionado != null) {
                actvMetodoPago.setText(metodoPagoSeleccionado.nombre, false)
            }

            etFecha.setText(pedidoExistente.fecha)
            etMonto.setText(pedidoExistente.monto.toString())

            detallesTemp.addAll(pedidoExistente.detalles)
            detalleAdapterLocal.notifyDataSetChanged()
            calcularMontoTotal(etMonto)
        } else {
            val fechaActual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            etFecha.setText(fechaActual)
            etMonto.setText("0.0")
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(if (pedidoExistente != null) "Editar Pedido" else "Nuevo Pedido")
            .setView(dialogView)
            .setPositiveButton(if (pedidoExistente != null) "Actualizar" else "Crear") { _, _ ->
                val clienteTexto = actvCliente.text.toString()
                val repartidorTexto = actvRepartidor.text.toString()
                val metodoPagoTexto = actvMetodoPago.text.toString()
                val fecha = etFecha.text.toString()
                val montoTexto = etMonto.text.toString()

                if (clienteTexto.isEmpty() || repartidorTexto.isEmpty() || metodoPagoTexto.isEmpty() || fecha.isEmpty() || montoTexto.isEmpty()) {
                    Toast.makeText(this, "Todos los campos principales son obligatorios", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                if (detallesTemp.isEmpty()) {
                    Toast.makeText(this, "Debe agregar al menos un detalle al pedido", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val clienteId = obtenerClienteIdPorTexto(clienteTexto)
                val repartidorId = obtenerRepartidorIdPorTexto(repartidorTexto)
                val metodoPagoId = obtenerMetodoPagoIdPorTexto(metodoPagoTexto)
                val monto = montoTexto.toDoubleOrNull()

                if (clienteId == -1 || repartidorId == -1 || metodoPagoId == -1 || monto == null) {
                    Toast.makeText(this, "Datos inválidos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (pedidoExistente != null) {
                    actualizarPedido(pedidoExistente.id, monto, clienteId, repartidorId, metodoPagoId, detallesTemp.toList())
                } else {
                    crearPedido(monto, clienteId, repartidorId, metodoPagoId, detallesTemp.toList())
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    private fun obtenerClienteIdPorTexto(texto: String): Int {
        return clientes.find { "${it.nombre} - ${it.telefono}" == texto }?.id ?: -1
    }

    private fun obtenerRepartidorIdPorTexto(texto: String): Int {
        return repartidores.find { "${it.nombre} - ${it.placa}" == texto }?.id ?: -1
    }

    private fun obtenerMetodoPagoIdPorTexto(texto: String): Int {
        return metodosPago.find { it.nombre == texto }?.id ?: -1
    }

    private fun calcularMontoTotal(etMonto: EditText) {
        val total = detallesTemp.sumOf { it.cantidad * it.precio }
        etMonto.setText(String.format("%.2f", total))
    }

    private fun crearPedido(monto: Double, clienteId: Int, repartidorId: Int, metodoPagoId: Int, detalles: List<DetallePedido>) {
        apiGateway.crearPedido(monto, clienteId, repartidorId, metodoPagoId, detalles, object : ApiGateway.ApiCallback<Boolean> {
            override fun onSuccess(data: Boolean) {
                runOnUiThread {
                    if (data) {
                        Toast.makeText(this@PedidoActivity, "Pedido creado exitosamente", Toast.LENGTH_SHORT).show()
                        cargarPedidos()
                    } else {
                        Toast.makeText(this@PedidoActivity, "Error al crear pedido", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onError(error: String) {
                runOnUiThread {
                    Toast.makeText(this@PedidoActivity, "Error: $error", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun actualizarPedido(id: Int, monto: Double, clienteId: Int, repartidorId: Int, metodoPagoId: Int, detalles: List<DetallePedido>) {
        apiGateway.actualizarPedido(id, monto, clienteId, repartidorId, metodoPagoId, detalles, object : ApiGateway.ApiCallback<Boolean> {
            override fun onSuccess(data: Boolean) {
                runOnUiThread {
                    if (data) {
                        Toast.makeText(this@PedidoActivity, "Pedido actualizado exitosamente", Toast.LENGTH_SHORT).show()
                        cargarPedidos()
                    } else {
                        Toast.makeText(this@PedidoActivity, "Error al actualizar pedido", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onError(error: String) {
                runOnUiThread {
                    Toast.makeText(this@PedidoActivity, "Error: $error", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun confirmarEliminar(pedido: PedidoCompleto) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Está seguro que desea eliminar el pedido #${String.format("%03d", pedido.id)}?")
            .setPositiveButton("Eliminar") { _, _ -> eliminarPedido(pedido) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarPedido(pedido: PedidoCompleto) {
        apiGateway.eliminarPedido(pedido.id, object : ApiGateway.ApiCallback<Boolean> {
            override fun onSuccess(data: Boolean) {
                runOnUiThread {
                    if (data) {
                        Toast.makeText(this@PedidoActivity, "Pedido eliminado exitosamente", Toast.LENGTH_SHORT).show()
                        adapter.eliminarItem(pedido)
                    } else {
                        Toast.makeText(this@PedidoActivity, "Error al eliminar pedido", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onError(error: String) {
                runOnUiThread {
                    Toast.makeText(this@PedidoActivity, "Error: $error", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}
