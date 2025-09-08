package com.example.parcialarqui.producto

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.parcialarqui.R

class ProductoAdapter(
    private val lista: List<Producto>,
    private val onItemClick: (Producto) -> Unit
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

        // Por ahora usamos drawable por defecto
        holder.ivImagen.setImageResource(R.drawable.ic_producto_default)

        // Click en toda la tarjeta
        holder.itemView.setOnClickListener {
            onItemClick(producto)
        }

        // Botones de acción
        holder.btnEditar.setOnClickListener {
            // TODO: Implementar editar
            Log.d("ProductoAdapter", "Editar producto: ${producto.nombre}")
        }

        holder.btnEliminar.setOnClickListener {
            // TODO: Implementar eliminar
            Log.d("ProductoAdapter", "Eliminar producto: ${producto.nombre}")
        }

        holder.btnInfo.setOnClickListener {
            onItemClick(producto)
        }
    }

    override fun getItemCount(): Int = lista.size
}