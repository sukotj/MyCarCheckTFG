package com.example.mycarcheckkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ListaVehiculosActivity : AppCompatActivity() {

    private lateinit var listaVehiculos: ListView
    private lateinit var baseDeDatos: BaseDeDatos
    private lateinit var btnVolverInicio: Button
    private lateinit var btnAgregarRepostaje: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_vehiculos)

        listaVehiculos = findViewById(R.id.listaVehiculos)
        btnVolverInicio = findViewById(R.id.btnVolverInicio)
        btnAgregarRepostaje = findViewById(R.id.btnAgregarRepostaje)
        baseDeDatos = BaseDeDatos(this)

        val idUsuario = obtenerIdUsuarioActual()
        val vehiculos = baseDeDatos.getVehiculosPorUsuario(idUsuario)

        //datos mostrados de nuestro vehiculo cuando entramos a ver vehiculos
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            vehiculos.map { v -> "${v.matricula} - ${v.marca} ${v.modelo} (${v.kmActuales} km)" }
        )

        listaVehiculos.adapter = adapter

        //al tocar en el vehiculo lanzamos el agregar respostaje activity con su ID
        listaVehiculos.setOnItemClickListener { _, _, position, _ ->
            val vehiculoSeleccionado = vehiculos[position]
            val intent = Intent(this, AgregarRepostajeActivity::class.java)
            intent.putExtra("idVehiculo", vehiculoSeleccionado.idVehiculo)
            startActivity(intent)
        }

        //boton para volver al inicio
        btnVolverInicio.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        //cambio de pantala a agregar repostaje
        btnAgregarRepostaje.setOnClickListener {
            Toast.makeText(this, "Selecciona un vehículo tocando en la lista", Toast.LENGTH_SHORT).show()
        }
    }

    private fun obtenerIdUsuarioActual(): Int {
        return 1 //rellenar
    }
}
