package com.example.parcialarqui.pedido

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.parcialarqui.R
import com.example.parcialarqui.producto.Producto

class DetalleAdapter(
    private val detalles: MutableList<DetallePedido>,
    private val productos: List<Producto>,
    private val onEliminarClick: (DetallePedido, Int) -> Unit
) : RecyclerView.Adapter<DetalleAdapter.DetalleViewHolder>() {

    class DetalleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProducto: TextView = itemView.findViewById(R.id.tvProducto)
        val tvCantidad: TextView = itemView.findViewById(R.id.tvCantidad)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        val btnEliminarDetalle: ImageButton = itemView.findViewById(R.id.btnEliminarDetalle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetalleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_detalle_pedido, parent, false)
        return DetalleViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetalleViewHolder, position: Int) {
        val detalle = detalles[position]
        val producto = productos.find { it.id == detalle.productoId }

        holder.tvProducto.text = producto?.nombre ?: "Producto ${detalle.productoId}"
        holder.tvCantidad.text = detalle.cantidad.toString()
        holder.tvPrecio.text = "Bs. ${String.format("%.2f", detalle.precio)}"

        holder.btnEliminarDetalle.setOnClickListener {
            onEliminarClick(detalle, position)
        }
    }

    override fun getItemCount(): Int = detalles.size

    fun eliminarItem(position: Int) {
        detalles.removeAt(position)
        notifyItemRemoved(position)
    }

    fun agregarDetalle(detalle: DetallePedido) {
        detalles.add(detalle)
        notifyItemInserted(detalles.size - 1)
    }
}