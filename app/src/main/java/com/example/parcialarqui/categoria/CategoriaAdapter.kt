package com.example.parcialarqui.categoria

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.parcialarqui.R

class CategoriaAdapter(
    private val lista: List<Categoria>,
    private val onItemClick: (Categoria) -> Unit
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

        holder.itemView.setOnClickListener { onItemClick(categoria) }
        holder.btnExpandir.setOnClickListener { onItemClick(categoria) }

        holder.btnEditar.setOnClickListener {
            Log.d("CategoriaAdapter", "Editar: ${categoria.nombre}")
        }

        holder.btnEliminar.setOnClickListener {
            Log.d("CategoriaAdapter", "Eliminar: ${categoria.nombre}")
        }
    }

    override fun getItemCount(): Int = lista.size
}