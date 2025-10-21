package com.example.mycarcheckkotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RepostajeAdapter(
    private val lista: List<Repostaje>,
    private val onEliminar: (Int) -> Unit
) : RecyclerView.Adapter<RepostajeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvLitros: TextView = view.findViewById(R.id.tvLitros)
        val tvCoste: TextView = view.findViewById(R.id.tvCoste)
        val tvConsumo: TextView = view.findViewById(R.id.tvConsumo)
        val btnEliminar: Button = view.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_repostaje, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val r = lista[position]
        val costeTotal = r.litros * r.precioLitro
        val consumoMedio = if (r.litros > 0) (r.kmActuales - r.kmAnterior) / r.litros else 0.0

        holder.tvFecha.text = "Fecha: ${r.fecha}"
        holder.tvLitros.text = "Litros: ${r.litros}"
        holder.tvCoste.text = "Coste: ${"%.2f".format(costeTotal)} €"
        holder.tvConsumo.text = "Consumo medio: ${"%.2f".format(consumoMedio)} km/l"

        holder.btnEliminar.setOnClickListener {
            onEliminar(r.idRepostaje)
        }
    }

    override fun getItemCount(): Int = lista.size
}
