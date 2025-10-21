package com.example.mycarcheckkotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RepostajeAdapter(private val lista: List<Repostaje>) :
    RecyclerView.Adapter<RepostajeAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        val tvLitros: TextView = itemView.findViewById(R.id.tvLitros)
        val tvCoste: TextView = itemView.findViewById(R.id.tvCoste)
        val tvConsumo: TextView = itemView.findViewById(R.id.tvConsumo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_repostaje, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val r = lista[position]
        holder.tvFecha.text = "Fecha: ${r.fecha}"
        holder.tvLitros.text = "Litros: ${r.litros} L"
        holder.tvCoste.text = "Coste total: %.2f €".format(r.precioTotal)
        holder.tvConsumo.text = "Consumo medio: %.2f L/100km".format(r.consumoMedio)
    }

    override fun getItemCount(): Int = lista.size
}
