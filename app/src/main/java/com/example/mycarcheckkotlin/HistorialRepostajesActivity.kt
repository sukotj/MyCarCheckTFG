package com.example.mycarcheckkotlin

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistorialRepostajesActivity : AppCompatActivity() {

    private lateinit var rvRepostajes: RecyclerView
    private lateinit var baseDeDatos: BaseDeDatos
    private lateinit var adapter: RepostajeAdapter
    private lateinit var tvConsumoTotal: TextView
    private var idVehiculo: Int = 1 // puedes recibirlo por Intent si lo gestionas dinámicamente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_repostajes)

        rvRepostajes = findViewById(R.id.rvRepostajes)
        tvConsumoTotal = findViewById(R.id.tvConsumoTotal)
        baseDeDatos = BaseDeDatos(this)

        cargarRepostajes()
    }

    private fun cargarRepostajes() {
        val lista = baseDeDatos.getRepostajesPorVehiculo(idVehiculo)

        adapter = RepostajeAdapter(lista) { idRepostaje ->
            baseDeDatos.eliminarRepostaje(idRepostaje)
            Toast.makeText(this, "Repostaje eliminado", Toast.LENGTH_SHORT).show()
            cargarRepostajes() // recarga la lista tras eliminar
        }

        rvRepostajes.layoutManager = LinearLayoutManager(this)
        rvRepostajes.adapter = adapter

        // 👉 Aquí va la línea que actualiza el TextView del consumo medio total
        val consumoMedio = baseDeDatos.calcularConsumoMedio(idVehiculo)
        tvConsumoTotal.text = "Consumo medio total: ${"%.2f".format(consumoMedio)} km/l"
    }

}
