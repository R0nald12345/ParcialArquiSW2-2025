package com.example.parcialarqui

import android.util.Log
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class ApiGateway {

    private val client = OkHttpClient()

    // URLs de los microservicios
    private val CATEGORIA_SERVICE = "http://10.0.2.2:8081"
    private val PRODUCTO_SERVICE = "http://10.0.2.2:8082"

    // Interfaz para callbacks
    interface ApiCallback<T> {
        fun onSuccess(data: T)
        fun onError(error: String)
    }

    // ===== SERVICIOS DE CATEGORIA =====

    fun obtenerCategorias(callback: ApiCallback<List<Categoria>>) {
        val request = Request.Builder()
            .url("$CATEGORIA_SERVICE/categorias")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ApiGateway", "Error categorías: ${e.message}")
                callback.onError("Error de conexión: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback.onError("Error del servidor: ${response.code}")
                    return
                }

                response.body?.let { responseBody ->
                    try {
                        val jsonString = responseBody.string()
                        val jsonArray = JSONArray(jsonString)
                        val categorias = mutableListOf<Categoria>()

                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            val categoria = Categoria(
                                id = obj.getInt("id"),
                                nombre = obj.getString("nombre"),
                                descripcion = obj.getString("descripcion"),
                                imagen = obj.getString("imagen")
                            )
                            categorias.add(categoria)
                        }

                        callback.onSuccess(categorias)
                    } catch (e: Exception) {
                        Log.e("ApiGateway", "Error parseando categorías: ${e.message}")
                        callback.onError("Error procesando datos")
                    }
                }
            }
        })
    }

    // ===== SERVICIOS DE PRODUCTO =====

    fun obtenerProductos(callback: ApiCallback<List<Producto>>) {
        val request = Request.Builder()
            .url("$PRODUCTO_SERVICE/productos")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ApiGateway", "Error productos: ${e.message}")
                callback.onError("Error de conexión: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback.onError("Error del servidor: ${response.code}")
                    return
                }

                response.body?.let { responseBody ->
                    try {
                        val jsonString = responseBody.string()
                        val jsonArray = JSONArray(jsonString)
                        val productos = mutableListOf<Producto>()

                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            val producto = Producto(
                                id = obj.getInt("id"),
                                nombre = obj.getString("nombre"),
                                precio = obj.getDouble("precio"),
                                descripcion = obj.getString("descripcion"),
                                stock = obj.getInt("stock"),
                                imagen = obj.getString("imagen"),
                                categoriaId = obj.getInt("categoriaId")
                            )
                            productos.add(producto)
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

    fun obtenerProductosPorCategoria(categoriaId: Int, callback: ApiCallback<List<Producto>>) {
        // Primero obtenemos todos los productos y luego filtramos
        obtenerProductos(object : ApiCallback<List<Producto>> {
            override fun onSuccess(data: List<Producto>) {
                val productosFiltrados = data.filter { it.categoriaId == categoriaId }
                callback.onSuccess(productosFiltrados)
            }

            override fun onError(error: String) {
                callback.onError(error)
            }
        })
    }
}