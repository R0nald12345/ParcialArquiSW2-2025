package com.example.parcialarqui.metodopago

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.parcialarqui.ApiGateway
import com.example.parcialarqui.R

class MetodoPagoAdapter(
    private val lista: List<MetodoPago>,
    private val onRefresh: () -> Unit
) : RecyclerView.Adapter<MetodoPagoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreMetodoPago)
        val btnEliminar: ImageView = view.findViewById(R.id.btnEliminarMetodoPago)
        val btnEditar: ImageView = view.findViewById(R.id.btnEditarMetodoPago)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_metodopago, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val metodoPago = lista[position]
        holder.tvNombre.text = metodoPago.nombre

        // Eliminar
        holder.btnEliminar.setOnClickListener {
            val ctx = holder.itemView.context
            AlertDialog.Builder(ctx)
                .setTitle("Eliminar Método de Pago")
                .setMessage("¿Seguro que deseas eliminar ${metodoPago.nombre}?")
                .setPositiveButton("Sí") { _, _ ->
                    val api = ApiGateway()
                    api.eliminarMetodoPago(metodoPago.id, object : ApiGateway.ApiCallback<Boolean> {
                        override fun onSuccess(data: Boolean) {
                            onRefresh()
                        }

                        override fun onError(error: String) {
                            Log.e("MetodoPagoAdapter", "Error al eliminar: $error")
                        }
                    })
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // Editar
        holder.btnEditar.setOnClickListener {
            val ctx = holder.itemView.context
            if (ctx is MetodoPagoActivity) {
                ctx.mostrarDialogActualizarMetodoPago(metodoPago)
            }
        }
    }


    override fun getItemCount(): Int = lista.size
}
