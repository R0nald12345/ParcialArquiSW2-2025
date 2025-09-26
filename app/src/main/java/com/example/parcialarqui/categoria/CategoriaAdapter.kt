package com.example.parcialarqui.categoria

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

class CategoriaAdapter(
    private val lista: List<Categoria>,
    private val onItemClick: (Categoria) -> Unit,
    private val onRefresh: () -> Unit // callback para recargar categorías
) : RecyclerView.Adapter<CategoriaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val ivImagen: ImageView = view.findViewById(R.id.ivImagen)
        val btnEditar: ImageView = view.findViewById(R.id.btnEditar)
        val btnEliminar: ImageView = view.findViewById(R.id.btnEliminar)
        val btnExpandir: ImageView = view.findViewById(R.id.btnExpandir)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoria = lista[position]

        holder.tvNombre.text = categoria.nombre
        holder.ivImagen.setImageResource(R.drawable.ic_categoria_default)

        // Click en la tarjeta o expandir
        holder.itemView.setOnClickListener { onItemClick(categoria) }
        holder.btnExpandir.setOnClickListener { onItemClick(categoria) }

        // EDITAR
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
                            onRefresh() // refresca lista
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

        // ELIMINAR
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
                                onRefresh() // 👈 refresca lista
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
