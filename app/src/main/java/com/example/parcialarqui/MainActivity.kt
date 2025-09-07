package com.example.parcialarqui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val client = OkHttpClient()
    private val productos = mutableListOf<Producto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        Log.d("MainActivity", "Iniciando fetchProductos...")
        fetchProductos()
    }

    private fun fetchProductos() {
        //val url = "http://192.168.0.14:8082/productos"
        val url = "http://10.0.2.2:8082/productos"
        Log.d("MainActivity", "URL: $url")

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("MainActivity", "Error en la petición: ${e.message}", e)
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("MainActivity", "Respuesta recibida. Código: ${response.code}")

                if (!response.isSuccessful) {
                    Log.e("MainActivity", "Respuesta no exitosa: ${response.code}")
                    return
                }

                response.body?.let { responseBody ->
                    val jsonString = responseBody.string()
                    Log.d("MainActivity", "JSON recibido: $jsonString")

                    try {
                        val jsonArray = JSONArray(jsonString)
                        Log.d("MainActivity", "Número de productos: ${jsonArray.length()}")

                        productos.clear()
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
                            Log.d("MainActivity", "Producto agregado: ${producto.nombre}")
                        }

                        runOnUiThread {
                            Log.d("MainActivity", "Configurando adapter con ${productos.size} productos")
                            recyclerView.adapter = ProductoAdapter(productos)
                        }
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error parseando JSON: ${e.message}", e)
                    }
                } ?: Log.e("MainActivity", "Response body es null")
            }
        })
    }
}