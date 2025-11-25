package com.example.mycarcheckkotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import androidx.recyclerview.widget.RecyclerView

class VehiculoAdapter(
    private val lista: List<Vehiculo>,
    private val onClickRepostajes: (Vehiculo) -> Unit //accion al pulsar ver repostajes
) : RecyclerView.Adapter<VehiculoAdapter.ViewHolder>() {

    //inicializamos los datos del xml
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvInfo: TextView = itemView.findViewById(R.id.tvInfoVehiculo)
        val tvKmActuales: TextView = itemView.findViewById(R.id.tvKmActuales)
        val btnRepostajes: MaterialButton = itemView.findViewById(R.id.btnVerRepostajes)
        val btnEliminarVehiculo: MaterialButton = itemView.findViewById(R.id.btnEliminarVehiculo)
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

        holder.tvKmActuales.text = "Km: ${v.kmActuales}"

        //boton para ver el historial de repostajes
        holder.btnRepostajes.setOnClickListener {
            onClickRepostajes(v)
        }

        //boton para eliminar vehículo (si quieres manejarlo desde fuera con otra lambda)
        holder.btnEliminarVehiculo.setOnClickListener {
            // Aquí puedes añadir lógica o exponer otra lambda como hicimos con repostajes
        }
    }

    //devuelve la cantidad de items que muestra el recycler
    override fun getItemCount(): Int = lista.size
}
