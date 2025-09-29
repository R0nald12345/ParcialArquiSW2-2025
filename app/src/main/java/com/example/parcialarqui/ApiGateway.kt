package com.example.parcialarqui

import android.util.Log
import com.example.parcialarqui.categoria.Categoria
import com.example.parcialarqui.cliente.Cliente
import com.example.parcialarqui.pedido.DetallePedido
import com.example.parcialarqui.pedido.Pedido
import com.example.parcialarqui.producto.Producto
import com.example.parcialarqui.repartidor.Repartidor
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

import com.example.parcialarqui.pedido.PedidoCompleto
import org.json.JSONObject

class ApiGateway {
    private val client = OkHttpClient()

    // Servicios separados por puerto
    private val BASE_URL_CATEGORIA = "http://10.0.2.2:8080"
    private val BASE_URL_PRODUCTO = "http://10.0.2.2:8081"

    private val BASE_URL_METODOPAGO = "http://10.0.2.2:8082"

    private val BASE_URL_CLIENTE = "http://10.0.2.2:8083"

    private val BASE_URL_PEDIDO = "http://10.0.2.2:8084"

    // AGREGAR ESTA LÍNEA CON LAS OTRAS URLs BASE:
    private val BASE_URL_REPARTIDOR = "http://10.0.2.2:8085"

    interface ApiCallback<T> {
        fun onSuccess(data: T)
        fun onError(error: String)
    }

    //==============================================================================================
    // ===== SERVICIOS DE CATEGORIA =====
    //==============================================================================================
    fun obtenerCategorias(callback: ApiCallback<List<Categoria>>) {
        val request = Request.Builder()
            .url("$BASE_URL_CATEGORIA/categorias")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("Error de conexión: ${e.message}")
            }
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback.onError("Error del servidor: ${response.code}")
                    return
                }
                response.body?.let { rb ->
                    try {
                        val jsonArray = JSONArray(rb.string())
                        val categorias = mutableListOf<Categoria>()
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            categorias.add(
                                Categoria(
                                    id = obj.getInt("id"),
                                    nombre = obj.getString("nombre"),
                                    descripcion = obj.getString("descripcion")
                                )
                            )
                        }
                        callback.onSuccess(categorias)
                    } catch (e: Exception) {
                        callback.onError("Error procesando datos")
                    }
                }
            }
        })
    }

    fun crearCategoria(categoria: Categoria, callback: ApiCallback<Boolean>) {
        val json = """
            {
              "nombre":"${categoria.nombre}",
              "descripcion":"${categoria.descripcion}"
            }
        """.trimIndent()
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("$BASE_URL_CATEGORIA/categorias")
            .post(body)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback.onError(e.message ?: "Error") }
            override fun onResponse(call: Call, response: Response) { callback.onSuccess(response.isSuccessful) }
        })
    }

    fun actualizarCategoria(categoria: Categoria, callback: ApiCallback<Boolean>) {
        val json = """
            {
              "id": ${categoria.id},
              "nombre":"${categoria.nombre}",
              "descripcion":"${categoria.descripcion}"
            }
        """.trimIndent()
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("$BASE_URL_CATEGORIA/categorias/${categoria.id}")
            .put(body)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback.onError(e.message ?: "Error") }
            override fun onResponse(call: Call, response: Response) { callback.onSuccess(response.isSuccessful) }
        })
    }

    fun eliminarCategoria(id: Int, callback: ApiCallback<Boolean>) {
        val request = Request.Builder()
            .url("$BASE_URL_CATEGORIA/categorias/$id")
            .delete()
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback.onError(e.message ?: "Error") }
            override fun onResponse(call: Call, response: Response) { callback.onSuccess(response.isSuccessful) }
        })
    }

    //==============================================================================================
    // S E R V I C I O S   D E   P R O D U C T O
    //==============================================================================================
    fun obtenerProductos(callback: ApiCallback<List<Producto>>) {
        val request = Request.Builder()
            .url("$BASE_URL_PRODUCTO/productos")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback.onError("Error de conexión: ${e.message}") }
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback.onError("Error del servidor: ${response.code}")
                    return
                }
                response.body?.let { rb ->
                    try {
                        val jsonArray = JSONArray(rb.string())
                        val productos = mutableListOf<Producto>()
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            productos.add(
                                Producto(
                                    id = obj.getInt("id"),
                                    nombre = obj.getString("nombre"),
                                    precio = obj.getDouble("precio"),
                                    descripcion = obj.getString("descripcion"),
                                    stock = obj.getInt("stock"),
                                    categoriaId = obj.getInt("categoriaId")
                                )
                            )
                        }
                        callback.onSuccess(productos)
                    } catch (e: Exception) {
                        callback.onError("Error procesando datos")
                    }
                }
            }
        })
    }


    fun crearProducto(producto: Producto, callback: ApiCallback<Boolean>) {
        val json = """
        {
          "nombre":"${producto.nombre}",
          "precio":${producto.precio},
          "descripcion":"${producto.descripcion}",
          "stock":${producto.stock},
          "categoriaId":${producto.categoriaId}
        }
    """.trimIndent()

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = json.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$BASE_URL_PRODUCTO/productos")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e.message ?: "Error de red")
            }
            override fun onResponse(call: Call, response: Response) {
                callback.onSuccess(response.isSuccessful)
            }
        })
    }

    fun actualizarProducto(producto: Producto, callback: ApiCallback<Boolean>) {
        val json = """
        {
          "id":${producto.id},
          "nombre":"${producto.nombre}",
          "precio":${producto.precio},
          "descripcion":"${producto.descripcion}",
          "stock":${producto.stock},
          "categoriaId":${producto.categoriaId}
        }
    """.trimIndent()

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = json.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$BASE_URL_PRODUCTO/productos/${producto.id}")
            .put(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e.message ?: "Error de red")
            }
            override fun onResponse(call: Call, response: Response) {
                callback.onSuccess(response.isSuccessful)
            }
        })
    }

    fun eliminarProducto(id: Int, callback: ApiCallback<Boolean>) {
        val request = Request.Builder()
            .url("$BASE_URL_PRODUCTO/productos/$id")
            .delete()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e.message ?: "Error de red")
            }
            override fun onResponse(call: Call, response: Response) {
                callback.onSuccess(response.isSuccessful)
            }
        })
    }

    fun obtenerProductosPorCategoria(categoriaId: Int, callback: ApiCallback<List<Producto>>) {
        val request = Request.Builder()
            .url("$BASE_URL_PRODUCTO/productos/categoria/$categoriaId")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("Error de conexión: ${e.message}")
            }
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback.onError("Error del servidor: ${response.code}")
                    return
                }
                response.body?.let { rb ->
                    try {
                        val bodyString = rb.string()
                        Log.d("ApiGateway", "JSON productos recibido: $bodyString") // 👈 imprime respuesta completa

                        val jsonArray = JSONArray(bodyString)
                        val productos = mutableListOf<Producto>()
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            productos.add(
                                Producto(
                                    id = obj.optInt("id", 0),
                                    nombre = obj.optString("nombre", ""),
                                    precio = obj.optDouble("precio", 0.0),
                                    descripcion = obj.optString("descripcion", ""),
                                    stock = obj.optInt("stock", 0),
                                    categoriaId = if (obj.has("categoriaId"))
                                        obj.optInt("categoriaId", 0)
                                    else
                                        obj.optInt("categoria_id", 0) // soporte snake_case
                                )
                            )
                        }
                        callback.onSuccess(productos)
                    } catch (e: Exception) {
                        Log.e("ApiGateway", "Error parseando productos: ${e.message}")
                        callback.onError("Error procesando datos")
                    }
                }
            }
        })
    }

    //==============================================================================================
    // S E R V I C I O S   D E   M É T O D O   D E   P A G O
    //==============================================================================================
    fun obtenerMetodosPago(callback: ApiCallback<List<com.example.parcialarqui.metodopago.MetodoPago>>) {
        val request = Request.Builder()
            .url("$BASE_URL_METODOPAGO/metodospago")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("Error de conexión: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback.onError("Error del servidor: ${response.code}")
                    return
                }
                response.body?.let { rb ->
                    try {
                        val jsonArray = JSONArray(rb.string())
                        val lista = mutableListOf<com.example.parcialarqui.metodopago.MetodoPago>()
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            lista.add(
                                com.example.parcialarqui.metodopago.MetodoPago(
                                    id = obj.getInt("id"),
                                    nombre = obj.getString("nombre")
                                )
                            )
                        }
                        callback.onSuccess(lista)
                    } catch (e: Exception) {
                        callback.onError("Error procesando datos: ${e.message}")
                    }
                }
            }
        })
    }

    fun crearMetodoPago(metodoPago: com.example.parcialarqui.metodopago.MetodoPago, callback: ApiCallback<Boolean>) {
        val json = """
        {
          "nombre":"${metodoPago.nombre}"
        }
    """.trimIndent()

        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("$BASE_URL_METODOPAGO/metodospago")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback.onError(e.message ?: "Error") }
            override fun onResponse(call: Call, response: Response) { callback.onSuccess(response.isSuccessful) }
        })
    }

    fun actualizarMetodoPago(metodoPago: com.example.parcialarqui.metodopago.MetodoPago, callback: ApiCallback<Boolean>) {
        val json = """
        {
          "id": ${metodoPago.id},
          "nombre":"${metodoPago.nombre}"
        }
    """.trimIndent()

        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("$BASE_URL_METODOPAGO/metodospago/${metodoPago.id}")
            .put(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback.onError(e.message ?: "Error") }
            override fun onResponse(call: Call, response: Response) { callback.onSuccess(response.isSuccessful) }
        })
    }

    fun eliminarMetodoPago(id: Int, callback: ApiCallback<Boolean>) {
        val request = Request.Builder()
            .url("$BASE_URL_METODOPAGO/metodospago/$id")
            .delete()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback.onError(e.message ?: "Error") }
            override fun onResponse(call: Call, response: Response) { callback.onSuccess(response.isSuccessful) }
        })
    }

    //==============================================================================================
    // R E P A R T I D O R E S
    //==============================================================================================

    fun obtenerRepartidores(callback: ApiCallback<List<Repartidor>>) {
        val request = Request.Builder()
            .url("$BASE_URL_REPARTIDOR/repartidores")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("Error de conexión: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseBody = response.body?.string()
                    if (response.isSuccessful && responseBody != null) {
                        val jsonArray = JSONArray(responseBody)
                        val lista = mutableListOf<Repartidor>()

                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            lista.add(
                                Repartidor(
                                    id = obj.getInt("id"),
                                    nombre = obj.getString("nombre"),
                                    ci = obj.getString("ci"),
                                    placa = obj.getString("placa"),
                                    celular = obj.getString("celular")
                                )
                            )
                        }
                        callback.onSuccess(lista)
                    } else {
                        callback.onError("Error del servidor: ${response.code}")
                    }
                } catch (e: Exception) {
                    callback.onError("Error al procesar datos: ${e.message}")
                }
            }
        })
    }

    fun crearRepartidor(repartidor: Repartidor, callback: ApiCallback<Boolean>) {
        val json = """
        {
            "nombre": "${repartidor.nombre}",
            "ci": "${repartidor.ci}",
            "placa": "${repartidor.placa}",
            "celular": "${repartidor.celular}"
        }
    """.trimIndent()

        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("$BASE_URL_REPARTIDOR/repartidores")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("Error de conexión: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                callback.onSuccess(response.isSuccessful)
            }
        })
    }

    fun actualizarRepartidor(repartidor: Repartidor, callback: ApiCallback<Boolean>) {
        val json = """
        {
            "id": ${repartidor.id},
            "nombre": "${repartidor.nombre}",
            "ci": "${repartidor.ci}",
            "placa": "${repartidor.placa}",
            "celular": "${repartidor.celular}"
        }
    """.trimIndent()

        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("$BASE_URL_REPARTIDOR/repartidores/${repartidor.id}")
            .put(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("Error de conexión: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                callback.onSuccess(response.isSuccessful)
            }
        })
    }

    fun eliminarRepartidor(id: Int, callback: ApiCallback<Boolean>) {
        val request = Request.Builder()
            .url("$BASE_URL_REPARTIDOR/repartidores/$id")
            .delete()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("Error de conexión: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                callback.onSuccess(response.isSuccessful)
            }
        })
    }

    //==============================================================================================
    //  C L I E N T E
    //==============================================================================================

    fun obtenerClientes(callback: ApiCallback<List<Cliente>>) {
        val request = Request.Builder()
            .url("$BASE_URL_CLIENTE/clientes")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("Error de conexión: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseBody = response.body?.string()
                    if (response.isSuccessful && responseBody != null) {
                        val jsonArray = JSONArray(responseBody)
                        val lista = mutableListOf<Cliente>()

                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            lista.add(
                                Cliente(
                                    id = obj.getInt("id"),
                                    nombre = obj.getString("nombre"),
                                    telefono = obj.getString("telefono"),
                                    email = obj.getString("email"),
                                    direccion = obj.getString("direccion"),
                                    coordenadaX = obj.getDouble("coordenadaX"),
                                    coordenadaY = obj.getDouble("coordenadaY"),
                                    fechaRegistro = obj.getString("fechaRegistro"),
                                )
                            )
                        }
                        callback.onSuccess(lista)
                    } else {
                        callback.onError("Error del servidor: ${response.code}")
                    }
                } catch (e: Exception) {
                    callback.onError("Error al procesar datos: ${e.message}")
                }
            }
        })
    }

    fun crearCliente(cliente: Cliente, callback: ApiCallback<Boolean>) {
        val json = """
        {
            "nombre": "${cliente.nombre}",
            "telefono": "${cliente.telefono}",
            "email": "${cliente.email}",
            "direccion": "${cliente.direccion}",
            "coordenadaX": ${cliente.coordenadaX},
            "coordenadaY": ${cliente.coordenadaY}
        }
    """.trimIndent()
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("$BASE_URL_CLIENTE/clientes")
            .post(body)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback.onError("Error de conexión: ${e.message}") }
            override fun onResponse(call: Call, response: Response) { callback.onSuccess(response.isSuccessful) }
        })
    }

    fun actualizarCliente(cliente: Cliente, callback: ApiCallback<Boolean>) {
        val json = """
        {
            "id": ${cliente.id},
            "nombre": "${cliente.nombre}",
            "telefono": "${cliente.telefono}",
            "email": "${cliente.email}",
            "direccion": "${cliente.direccion}",
            "coordenadaX": ${cliente.coordenadaX},
            "coordenadaY": ${cliente.coordenadaY}
        }
    """.trimIndent()
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("$BASE_URL_CLIENTE/clientes/${cliente.id}")
            .put(body)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback.onError("Error de conexión: ${e.message}") }
            override fun onResponse(call: Call, response: Response) { callback.onSuccess(response.isSuccessful) }
        })
    }

    fun eliminarCliente(id: Int, callback: ApiCallback<Boolean>) {
        val request = Request.Builder()
            .url("$BASE_URL_CLIENTE/clientes/$id")
            .delete()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("Error de conexión: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                callback.onSuccess(response.isSuccessful)
            }
        })
    }

    //==============================================================================================
    // MÉTODOS PARA PEDIDOS:
    //==============================================================================================

    fun obtenerPedidos(callback: ApiCallback<List<PedidoCompleto>>) {
        val request = Request.Builder()
            .url("$BASE_URL_PEDIDO/pedidos")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("Error de conexión: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseBody = response.body?.string()
                    if (response.isSuccessful && responseBody != null) {
                        val jsonArray = JSONArray(responseBody)
                        val pedidosCompletos = mutableListOf<PedidoCompleto>()
                        var pedidosProcesados = 0

                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            val clienteId = obj.getInt("clienteId")
                            val repartidorId = obj.getInt("repartidorId")

                            // Obtener datos del cliente
                            obtenerClientePorId(clienteId) { cliente ->
                                // Obtener datos del repartidor
                                obtenerRepartidorPorId(repartidorId) { repartidor ->
                                    synchronized(pedidosCompletos) {
                                        pedidosCompletos.add(
                                            PedidoCompleto(
                                                id = obj.getInt("id"),
                                                fecha = obj.getString("fecha"),
                                                monto = obj.getDouble("monto"),
                                                clienteId = clienteId,
                                                repartidorId = repartidorId,
                                                clienteNombre = cliente?.nombre ?: "Cliente no encontrado",
                                                clienteTelefono = cliente?.telefono ?: "",
                                                repartidorNombre = repartidor?.nombre ?: "Repartidor no encontrado",
                                                repartidorPlaca = repartidor?.placa ?: ""
                                            )
                                        )
                                        pedidosProcesados++

                                        if (pedidosProcesados == jsonArray.length()) {
                                            callback.onSuccess(pedidosCompletos.sortedBy { it.id })
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        callback.onError("Error del servidor: ${response.code}")
                    }
                } catch (e: Exception) {
                    callback.onError("Error al procesar datos: ${e.message}")
                }
            }
        })
    }

    private fun obtenerClientePorId(id: Int, callback: (Cliente?) -> Unit) {
        val request = Request.Builder()
            .url("$BASE_URL_CLIENTE/clientes/$id")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseBody = response.body?.string()
                    if (response.isSuccessful && responseBody != null) {
                        val obj = JSONObject(responseBody)
                        val cliente = Cliente(
                            id = obj.getInt("id"),
                            nombre = obj.getString("nombre"),
                            telefono = obj.getString("telefono"),
                            email = obj.getString("email"),
                            direccion = obj.getString("direccion"),
                            coordenadaX = obj.getDouble("coordenadaX"),
                            coordenadaY = obj.getDouble("coordenadaY"),
                            fechaRegistro = obj.getString("fechaRegistro"),
                        )
                        callback(cliente)
                    } else {
                        callback(null)
                    }
                } catch (e: Exception) {
                    callback(null)
                }
            }
        })
    }

    private fun obtenerRepartidorPorId(id: Int, callback: (Repartidor?) -> Unit) {
        val request = Request.Builder()
            .url("$BASE_URL_REPARTIDOR/repartidores/$id")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseBody = response.body?.string()
                    if (response.isSuccessful && responseBody != null) {
                        val obj = JSONObject(responseBody)
                        val repartidor = Repartidor(
                            id = obj.getInt("id"),
                            nombre = obj.getString("nombre"),
                            ci = obj.getString("ci"),
                            placa = obj.getString("placa"),
                            celular = obj.getString("celular")
                        )
                        callback(repartidor)
                    } else {
                        callback(null)
                    }
                } catch (e: Exception) {
                    callback(null)
                }
            }
        })



    }

    //==============================================================================================
    //  P E D I D O
    //==============================================================================================

    fun crearPedido(fecha: String, monto: Double, clienteId: Int, repartidorId: Int, detalles: List<DetallePedido>, callback: ApiCallback<Boolean>) {
        val detallesJson = detalles.joinToString(",") { detalle ->
            """{"productoId":${detalle.productoId},"cantidad":${detalle.cantidad},"precio":${detalle.precio}}"""
        }

        // Si repartidorId es 0, significa "Sin repartidor"
        val repartidorField = if (repartidorId == 0) "null" else repartidorId.toString()

        val json = """
    {
        "monto": $monto,
        "clienteId": $clienteId,
        "repartidorId": $repartidorField,
        "detalles": [$detallesJson]
    }
    """.trimIndent()

        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("$BASE_URL_PEDIDO/pedidos")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("Error de conexión: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                callback.onSuccess(response.isSuccessful)
            }
        })
    }

    fun actualizarPedido(id: Int, fecha: String, monto: Double, clienteId: Int, repartidorId: Int, detalles: List<DetallePedido>, callback: ApiCallback<Boolean>) {
        val detallesJson = detalles.joinToString(",") { detalle ->
            """{"productoId":${detalle.productoId},"cantidad":${detalle.cantidad},"precio":${detalle.precio}}"""
        }

        // Si repartidorId es 0, significa "Sin repartidor"
        val repartidorField = if (repartidorId == 0) "null" else repartidorId.toString()

        val json = """
    {
        "id": $id,
        "monto": $monto,
        "clienteId": $clienteId,
        "repartidorId": $repartidorField,
        "detalles": [$detallesJson]
    }
    """.trimIndent()

        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("$BASE_URL_PEDIDO/pedidos/$id")
            .put(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("Error de conexión: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                callback.onSuccess(response.isSuccessful)
            }
        })
    }

    fun eliminarPedido(id: Int, callback: ApiCallback<Boolean>) {
        val request = Request.Builder()
            .url("$BASE_URL_PEDIDO/pedidos/$id")
            .delete()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("Error de conexión: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                callback.onSuccess(response.isSuccessful)
            }
        })
    }

}
