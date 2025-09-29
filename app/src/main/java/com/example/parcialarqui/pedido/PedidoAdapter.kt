package com.example.parcialarqui.pedido

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.parcialarqui.R
import java.text.SimpleDateFormat
import java.util.*

class PedidoAdapter(
    private val lista: MutableList<PedidoCompleto>,
    private val onEditarClick: (PedidoCompleto) -> Unit,
    private val onEliminarClick: (PedidoCompleto) -> Unit,
    private val onCompartirClick: (PedidoCompleto) -> Unit
) : RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvId: TextView = itemView.findViewById(R.id.tvId)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        val tvCliente: TextView = itemView.findViewById(R.id.tvCliente)
        val tvRepartidor: TextView = itemView.findViewById(R.id.tvRepartidor)
        val tvMonto: TextView = itemView.findViewById(R.id.tvMonto)
        val tvMetodoPago: TextView = itemView.findViewById(R.id.tvMetodoPago)
        val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditar)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)

        val btnCompartir: ImageButton = itemView.findViewById(R.id.btnCompartir)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = lista[position]

        holder.tvId.text = "Pedido #${String.format("%03d", pedido.id)}"
        holder.tvFecha.text = formatearFecha(pedido.fecha)
        holder.tvCliente.text = "${pedido.clienteNombre} - ${pedido.clienteTelefono}"
        holder.tvRepartidor.text = "${pedido.repartidorNombre} - ${pedido.repartidorPlaca}"
        holder.tvMonto.text = "Total: Bs. ${String.format("%.2f", pedido.monto)}"
        holder.tvMetodoPago.text = "Pago: ${pedido.metodoPagoNombre}"

        holder.btnEditar.setOnClickListener { onEditarClick(pedido) }
        holder.btnEliminar.setOnClickListener { onEliminarClick(pedido) }

        holder.btnCompartir.setOnClickListener { onCompartirClick(pedido) }
    }

    private fun formatearFecha(fecha: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val date = inputFormat.parse(fecha)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            fecha
        }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<PedidoCompleto>) {
        lista.clear()
        lista.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    fun eliminarItem(pedido: PedidoCompleto) {
        val position = lista.indexOf(pedido)
        if (position != -1) {
            lista.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
