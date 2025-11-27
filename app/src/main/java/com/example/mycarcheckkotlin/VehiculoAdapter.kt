package com.example.mycarcheckkotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import androidx.recyclerview.widget.RecyclerView

//adaptador para mostrar la lista de vehiculos en un recyclerview
//recibe una lista de vehiculo y tres lambdas para los botones del item
class VehiculoAdapter(
    private val lista: List<Vehiculo>,                              //lista de vehiculos a mostrar
    private val onClickVerRepostajes: (Vehiculo) -> Unit,           //accion al pulsar "Ver repostajes"
    private val onClickEditar: (Vehiculo) -> Unit,                  //accion al pulsar "Editar"
    private val onClickEliminar: (Vehiculo) -> Unit                 //accion al pulsar "Eliminar"
) : RecyclerView.Adapter<VehiculoAdapter.ViewHolder>() {

    //contiene las referencias a los elementos del layout
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvInfo: TextView = itemView.findViewById(R.id.tvInfoVehiculo)
        val tvKmActuales: TextView = itemView.findViewById(R.id.tvKmActuales)
        val btnVerRepostajes: MaterialButton = itemView.findViewById(R.id.btnVerRepostajes)
        val btnEditarVehiculo: MaterialButton = itemView.findViewById(R.id.btnEditarVehiculo)
        val btnEliminarVehiculo: MaterialButton = itemView.findViewById(R.id.btnEliminarVehiculo)
    }

    //infla el layout de cada item y crea el ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vehiculo, parent, false)
        return ViewHolder(vista)
    }

    //vincula los datos de un vehiculo con los elementos visuales del ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val v = lista[position]   // obtenemos el vehículo en la posición actual

        //mostramos la informacion principal del vehiculo
        holder.tvInfo.text = """
            Matrícula: ${v.matricula}
            Marca: ${v.marca}
            Modelo: ${v.modelo}
            Año: ${v.anoMatriculacion}
        """.trimIndent()

        //mostramos los km actuales
        holder.tvKmActuales.text = "Km: ${v.kmActuales}"

        //acciones de los botones del item
        holder.btnVerRepostajes.setOnClickListener { onClickVerRepostajes(v) }
        holder.btnEditarVehiculo.setOnClickListener { onClickEditar(v) }
        holder.btnEliminarVehiculo.setOnClickListener { onClickEliminar(v) }
    }

    //devuelve la cantidad de vehiculos en la lista
    override fun getItemCount(): Int = lista.size
}
