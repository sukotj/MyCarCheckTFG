package com.example.mycarcheckkotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VehiculoAdapter(
    private val lista: List<Vehiculo>,
    private val onClickRepostajes: (Vehiculo) -> Unit
) : RecyclerView.Adapter<VehiculoAdapter.ViewHolder>() {

    // ViewHolder: conecta los elementos visuales del item_vehiculo.xml
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvInfo: TextView = itemView.findViewById(R.id.tvInfoVehiculo)
        val btnRepostajes: Button = itemView.findViewById(R.id.btnVerRepostajes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vehiculo, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val v = lista[position]

        // Mostramos la información del vehículo
        holder.tvInfo.text = """
            Matrícula: ${v.matricula}
            Marca: ${v.marca}
            Modelo: ${v.modelo}
            Año: ${v.anoMatriculacion}
        """.trimIndent()

        // Acción del botón "Ver repostajes"
        holder.btnRepostajes.setOnClickListener {
            onClickRepostajes(v)
        }
    }

    override fun getItemCount(): Int = lista.size
}
