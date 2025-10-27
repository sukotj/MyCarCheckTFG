package com.example.mycarcheckkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ListaRepostajesActivity : AppCompatActivity() {

    private lateinit var listaRepostajes: ListView
    private lateinit var baseDeDatos: BaseDeDatos
    private lateinit var btnVolverInicio: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_repostajes)

        listaRepostajes = findViewById(R.id.listaRepostajes)
        btnVolverInicio = findViewById(R.id.btnVolverInicio)
        baseDeDatos = BaseDeDatos(this)

        val idUsuario = obtenerIdUsuarioActual()
        val vehiculos = baseDeDatos.getVehiculosPorUsuario(idUsuario)

        val todosLosRepostajes = mutableListOf<String>()
        for (vehiculo in vehiculos) {
            val repostajes = baseDeDatos.getRepostajesPorVehiculo(vehiculo.idVehiculo)
            for (r in repostajes) {
                todosLosRepostajes.add("Vehículo: ${vehiculo.matricula} | ${r.fecha} - ${r.litros}L @ ${r.precioLitro}€/L - ${r.kmActuales} km")
            }
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, todosLosRepostajes)
        listaRepostajes.adapter = adapter

        btnVolverInicio.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    private fun obtenerIdUsuarioActual(): Int {
        // Sustituye esto por SharedPreferences si ya lo usas
        return 1
    }
}
