package com.example.parcialarqui.cliente

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

class ClienteAdapter(
    private val lista: MutableList<Cliente>,
    private val onRefresh: () -> Unit
) : RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder>() {

    class ClienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvTelefono: TextView = itemView.findViewById(R.id.tvTelefono)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val tvMetodoPago: TextView = itemView.findViewById(R.id.tvMetodoPago)
        val btnEditar: ImageView = itemView.findViewById(R.id.btnEditar)
        val btnEliminar: ImageView = itemView.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cliente, parent, false)
        return ClienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        val cliente = lista[position]

        holder.tvNombre.text = cliente.nombre
        holder.tvTelefono.text = "Tel: ${cliente.telefono}"
        holder.tvEmail.text = cliente.email
        holder.tvMetodoPago.text = "Método: ${cliente.metodoPagoNombre}"

        // Botón editar
        holder.btnEditar.setOnClickListener {
            (holder.itemView.context as ClienteActivity)
                .mostrarDialogActualizarCliente(cliente)
        }

        // Botón eliminar
        holder.btnEliminar.setOnClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Confirmar eliminación")
            builder.setMessage("¿Estás seguro de que quieres eliminar a ${cliente.nombre}?")

            builder.setPositiveButton("Eliminar") { _, _ ->
                val apiGateway = ApiGateway()
                apiGateway.eliminarCliente(cliente.id, object : ApiGateway.ApiCallback<Boolean> {
                    override fun onSuccess(data: Boolean) {
                        onRefresh()
                        Toast.makeText(
                            holder.itemView.context,
                            "Cliente eliminado",
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