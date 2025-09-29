package com.example.parcialarqui.repartidor

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.parcialarqui.ApiGateway
import com.example.parcialarqui.R

class RepartidorAdapter(
    private val lista: MutableList<Repartidor>,
    private val onRefresh: () -> Unit
) : RecyclerView.Adapter<RepartidorAdapter.RepartidorViewHolder>() {

    class RepartidorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvCelular: TextView = itemView.findViewById(R.id.tvCelular)
        val tvPlaca: TextView = itemView.findViewById(R.id.tvPlaca)
        val tvCi: TextView = itemView.findViewById(R.id.tvCi)
        val btnEditar: ImageView = itemView.findViewById(R.id.btnEditar)
        val btnEliminar: ImageView = itemView.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepartidorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_repartidor, parent, false)
        return RepartidorViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepartidorViewHolder, position: Int) {
        val repartidor = lista[position]

        holder.tvNombre.text = repartidor.nombre
        holder.tvCelular.text = "Cel: ${repartidor.celular}"
        holder.tvPlaca.text = "Placa: ${repartidor.placa}"
        holder.tvCi.text = "CI: ${repartidor.ci}"


        // Botón editar
        holder.btnEditar.setOnClickListener {
            (holder.itemView.context as RepartidorActivity)
                .mostrarDialogActualizarRepartidor(repartidor)
        }

        // Botón eliminar
        holder.btnEliminar.setOnClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Confirmar eliminación")
            builder.setMessage("¿Estás seguro de que quieres eliminar a ${repartidor.nombre}?")

            builder.setPositiveButton("Eliminar") { _, _ ->
                val apiGateway = ApiGateway()
                apiGateway.eliminarRepartidor(repartidor.id, object : ApiGateway.ApiCallback<Boolean> {
                    override fun onSuccess(data: Boolean) {
                        onRefresh() // Recargar la lista
                        Toast.makeText(
                            holder.itemView.context,
                            "Repartidor eliminado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onError(error: String) {
                        Toast.makeText(
                            holder.itemView.context,
                            "Error: $error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }

            builder.setNegativeButton("Cancelar", null)
            builder.show()
        }
    }

    override fun getItemCount(): Int = lista.size
}