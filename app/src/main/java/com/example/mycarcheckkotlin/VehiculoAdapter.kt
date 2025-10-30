package com.example.mycarcheckkotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VehiculoAdapter(
    private val lista: List<Vehiculo>,
    private val onClickRepostajes: (Vehiculo) -> Unit //accion al pulsar ver repotajes
) : RecyclerView.Adapter<VehiculoAdapter.ViewHolder>() {

    //inicializamos los datos del xml
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

        //mostramos la información del vehiculo
        holder.tvInfo.text = """
            Matrícula: ${v.matricula}
            Marca: ${v.marca}
            Modelo: ${v.modelo}
            Año: ${v.anoMatriculacion}
        """.trimIndent()

        //boton para ver el historial de reposjtaes
        holder.btnRepostajes.setOnClickListener {
            onClickRepostajes(v)
        }
    }

    //devuelve la cantidad de items que muestra el recycler
    override fun getItemCount(): Int = lista.size
}