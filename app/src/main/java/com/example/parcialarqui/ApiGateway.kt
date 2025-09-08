package com.example.parcialarqui

import android.util.Log
import com.example.parcialarqui.categoria.Categoria
import com.example.parcialarqui.producto.Producto
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class ApiGateway {
    private val client = OkHttpClient()

    // ✅ Único servicio ahora en puerto 8080
    private val BASE_URL = "http://10.0.2.2:8080"

    interface ApiCallback<T> {
        fun onSuccess(data: T)
        fun onError(error: String)
    }

    // ===== SERVICIOS DE CATEGORIA =====
    fun obtenerCategorias(callback: ApiCallback<List<Categoria>>) {
        val request = Request.Builder()
            .url("$BASE_URL/categorias")
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
                        val jsonArray = JSONArray(responseBody.string())
                        val categorias = mutableListOf<Categoria>()

                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            categorias.add(
                                Categoria(
                                    id = obj.getInt("id"),
                                    nombre = obj.getString("nombre"),
                                    descripcion = obj.getString("descripcion"),
                                    imagen = obj.getString("imagen")
                                )
                            )
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
            .url("$BASE_URL/productos")
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
                        val jsonArray = JSONArray(responseBody.string())
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
                                    imagen = obj.getString("imagen"),
                                    categoriaId = obj.getInt("categoriaId")
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

    fun obtenerProductosPorCategoria(categoriaId: Int, callback: ApiCallback<List<Producto>>) {
        obtenerProductos(object : ApiCallback<List<Producto>> {
            override fun onSuccess(data: List<Producto>) {
                callback.onSuccess(data.filter { it.categoriaId == categoriaId })
            }

            override fun onError(error: String) {
                callback.onError(error)
            }
        })
    }
}
