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

import android.os.Handler
import android.os.Looper

class ClienteAdapter(
    private val lista: MutableList<Cliente>,
    private val onRefresh: () -> Unit
) : RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder>() {

    class ClienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvTelefono: TextView = itemView.findViewById(R.id.tvTelefono)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val tvDireccion: TextView = itemView.findViewById(R.id.tvDireccion)
        val tvCoordenadas: TextView = itemView.findViewById(R.id.tvCoordenadas)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
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
        holder.tvDireccion.text = "Dir: ${cliente.direccion}"
        holder.tvCoordenadas.text = "Coord: ${cliente.coordenadaX}, ${cliente.coordenadaY}"
        holder.tvFecha.text = "Registro: ${cliente.fechaRegistro}"

        holder.btnEditar.setOnClickListener {
            (holder.itemView.context as ClienteActivity).mostrarDialogActualizarCliente(cliente)
        }

        holder.btnEliminar.setOnClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Confirmar eliminación")
            builder.setMessage("¿Eliminar a ${cliente.nombre}?")

            builder.setPositiveButton("Eliminar") { _, _ ->
                val apiGateway = ApiGateway()
                apiGateway.eliminarCliente(cliente.id, object : ApiGateway.ApiCallback<Boolean> {
                    override fun onSuccess(data: Boolean) {
                        Handler(Looper.getMainLooper()).post {
                            onRefresh()
                            Toast.makeText(holder.itemView.context, "Cliente eliminado", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onError(error: String) {
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(holder.itemView.context, "Error: $error", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }

            builder.setNegativeButton("Cancelar", null)
            builder.show()
        }
    }

    override fun getItemCount(): Int = lista.size
}
