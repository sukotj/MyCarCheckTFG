package com.example.mycarcheckkotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RepostajeAdapter(
    private val lista: List<Repostaje>,
    //usada para eliminar al repostaje fuera del adaptador, funcion lambda
    private val onEliminar: (Int) -> Unit
) : RecyclerView.Adapter<RepostajeAdapter.ViewHolder>() {

    //usamos viewholder en vez de recycled para no tener que buscar por id cada vez que reciclemos la vista
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

    override fun onBindViewHolder(holder: ViewHolder, posicion: Int) {
        val r = lista[posicion]
        val costeTotal = r.precioTotal
        val consumoMedio = r.consumoMedio

        //mostramos los datos en la interfaz
        holder.tvFecha.text = "Fecha: ${r.fecha}"
        holder.tvLitros.text = "Litros: ${r.litros}"
        holder.tvCoste.text = "Coste: ${"%.2f".format(costeTotal)} €"
        holder.tvConsumo.text = "Consumo medio: ${"%.2f".format(consumoMedio)} km/l"

        holder.btnEliminar.setOnClickListener {
            onEliminar(r.idRepostaje)
        }
    }

    //funcion para saber cuanos repostajes tenemos
    override fun getItemCount(): Int = lista.size
}