package com.example.parcialarqui.categoria

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.parcialarqui.ApiGateway
import com.example.parcialarqui.R
import com.example.parcialarqui.producto.ProductosActivity

class CategoriaAdapter(
    private val lista: List<Categoria>,
    private val onRefresh: () -> Unit // callback para recargar categorías
) : RecyclerView.Adapter<CategoriaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val ivImagen: ImageView = view.findViewById(R.id.ivImagen)
        val btnEditar: ImageView = view.findViewById(R.id.btnEditar)
        val btnEliminar: ImageView = view.findViewById(R.id.btnEliminar)
        val btnVerProductos: TextView = view.findViewById(R.id.btnVerProductos) // 👈 corregido
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoria = lista[position]

        holder.tvNombre.text = categoria.nombre
        holder.tvDescripcion.text = categoria.descripcion
        holder.ivImagen.setImageResource(R.drawable.ic_category)

        //  Botón "Ver productos"
        holder.btnVerProductos.setOnClickListener {
            val ctx = holder.itemView.context
            val intent = Intent(ctx, ProductosActivity::class.java).apply {
                putExtra("categoria_id", categoria.id)
                putExtra("categoria_nombre", categoria.nombre)
            }
            ctx.startActivity(intent)
        }

        //  Editar
        holder.btnEditar.setOnClickListener {
            val ctx = holder.itemView.context
            val builder = android.app.AlertDialog.Builder(ctx)
            builder.setTitle("Editar Categoría")

            val layout = LayoutInflater.from(ctx).inflate(R.layout.dialog_categoria, null)
            builder.setView(layout)

            val etNombre = layout.findViewById<EditText>(R.id.etNombre)
            val etDescripcion = layout.findViewById<EditText>(R.id.etDescripcion)

            // Prellenar con valores existentes
            etNombre.setText(categoria.nombre)
            etDescripcion.setText(categoria.descripcion)

            builder.setPositiveButton("Actualizar") { _, _ ->
                val editada = categoria.copy(
                    nombre = etNombre.text.toString(),
                    descripcion = etDescripcion.text.toString(),
                )
                val api = ApiGateway()
                api.actualizarCategoria(editada, object : ApiGateway.ApiCallback<Boolean> {
                    override fun onSuccess(data: Boolean) {
                        (ctx as? CategoriaActivity)?.runOnUiThread {
                            onRefresh()
                        }
                    }

                    override fun onError(error: String) {
                        Log.e("CategoriaAdapter", "Error al actualizar: $error")
                    }
                })
            }

            builder.setNegativeButton("Cancelar", null)
            builder.show()
        }

        //  Eliminar
        holder.btnEliminar.setOnClickListener {
            val ctx = holder.itemView.context
            android.app.AlertDialog.Builder(ctx)
                .setTitle("Eliminar Categoría")
                .setMessage("¿Seguro que quieres eliminar ${categoria.nombre}?")
                .setPositiveButton("Sí") { _, _ ->
                    val api = ApiGateway()
                    api.eliminarCategoria(categoria.id, object : ApiGateway.ApiCallback<Boolean> {
                        override fun onSuccess(data: Boolean) {
                            (ctx as? CategoriaActivity)?.runOnUiThread {
                                onRefresh()
                            }
                        }

                        override fun onError(error: String) {
                            Log.e("CategoriaAdapter", "Error al eliminar: $error")
                        }
                    })
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    override fun getItemCount(): Int = lista.size
}
