package com.example.parcialarqui.producto

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.parcialarqui.ApiGateway
import com.example.parcialarqui.R

class ProductoAdapter(
    private val lista: List<Producto>,
    private val onItemClick: (Producto) -> Unit,
    private val onRefresh: () -> Unit
) : RecyclerView.Adapter<ProductoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecio)
        val ivImagen: ImageView = view.findViewById(R.id.ivImagen)
        val btnEditar: ImageView = view.findViewById(R.id.btnEditar)
        val btnEliminar: ImageView = view.findViewById(R.id.btnEliminar)
        val btnInfo: ImageView = view.findViewById(R.id.btnInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = lista[position]

        holder.tvNombre.text = producto.nombre
        holder.tvPrecio.text = "Bs. ${producto.precio}"
        holder.ivImagen.setImageResource(R.drawable.ic_producto_default)

        holder.itemView.setOnClickListener { onItemClick(producto) }
        holder.btnInfo.setOnClickListener { onItemClick(producto) }

        // EDITAR
        holder.btnEditar.setOnClickListener {
            val ctx = holder.itemView.context
            val builder = android.app.AlertDialog.Builder(ctx)
            builder.setTitle("Editar Producto")

            val layout = LayoutInflater.from(ctx).inflate(R.layout.dialog_producto, null)
            builder.setView(layout)

            val etNombre = layout.findViewById<EditText>(R.id.etNombre)
            val etPrecio = layout.findViewById<EditText>(R.id.etPrecio)
            val etDescripcion = layout.findViewById<EditText>(R.id.etDescripcion)
            val etStock = layout.findViewById<EditText>(R.id.etStock)

            etNombre.setText(producto.nombre)
            etPrecio.setText(producto.precio.toString())
            etDescripcion.setText(producto.descripcion)
            etStock.setText(producto.stock.toString())

            builder.setPositiveButton("Actualizar") { _, _ ->
                val editado = producto.copy(
                    nombre = etNombre.text.toString(),
                    precio = etPrecio.text.toString().toDouble(),
                    descripcion = etDescripcion.text.toString(),
                    stock = etStock.text.toString().toInt()
                )
                val api = ApiGateway()
                api.actualizarProducto(editado, object : ApiGateway.ApiCallback<Boolean> {
                    override fun onSuccess(data: Boolean) {
                        if (ctx is AppCompatActivity) {
                            ctx.runOnUiThread { 
                                onRefresh()
                            }
                        } else if (ctx is android.app.Activity) {
                            (ctx as android.app.Activity).runOnUiThread { 
                                onRefresh()
                            }
                        }
                    }

                    override fun onError(error: String) {
                        Log.e("ProductoAdapter", "Error: $error")
                    }
                })
            }

            builder.setNegativeButton("Cancelar", null)
            builder.show()
        }

        // ELIMINAR
        holder.btnEliminar.setOnClickListener {
            val ctx = holder.itemView.context
            android.app.AlertDialog.Builder(ctx)
                .setTitle("Eliminar Producto")
                .setMessage("¿Seguro que deseas eliminar ${producto.nombre}?")
                .setPositiveButton("Sí") { _, _ ->
                    val api = ApiGateway()
                    api.eliminarProducto(producto.id, object : ApiGateway.ApiCallback<Boolean> {
                        override fun onSuccess(data: Boolean) {
                            if (ctx is AppCompatActivity) {
                                ctx.runOnUiThread { 
                                    onRefresh()
                                }
                            } else if (ctx is android.app.Activity) {
                                (ctx as android.app.Activity).runOnUiThread { 
                                    onRefresh()
                                }
                            }
                        }
                        override fun onError(error: String) {
                            Log.e("ProductoAdapter", "Error: $error")
                        }
                    })
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    override fun getItemCount(): Int = lista.size
}
