package com.example.mycarcheckkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ListaVehiculosActivity : AppCompatActivity() {

    private lateinit var listaVehiculos: ListView
    private lateinit var baseDeDatos: BaseDeDatos
    private lateinit var btnVolverInicio: MaterialButton
    private lateinit var btnAgregarRepostaje: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_vehiculos)

        listaVehiculos = findViewById(R.id.listaVehiculos)
        btnVolverInicio = findViewById(R.id.btnVolverInicio)
        btnAgregarRepostaje = findViewById(R.id.btnAgregarRepostaje)
        baseDeDatos = BaseDeDatos(this)

        cargarVehiculos()

        btnVolverInicio.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        btnAgregarRepostaje.setOnClickListener {
            val vehiculos = baseDeDatos.getVehiculosPorUsuario(obtenerIdUsuarioActual())
            if (vehiculos.isEmpty()) {
                Toast.makeText(this, "No tienes vehículos registrados", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nombresVehiculos = vehiculos.map { "${it.matricula} - ${it.marca}" }.toTypedArray()

            AlertDialog.Builder(this)
                .setTitle("Selecciona un vehículo")
                .setItems(nombresVehiculos) { _, which ->
                    val vehiculoSeleccionado = vehiculos[which]
                    val intent = Intent(this, AgregarRepostajeActivity::class.java)
                    intent.putExtra("idVehiculo", vehiculoSeleccionado.idVehiculo)
                    startActivity(intent)
                }
                .show()
        }
    }

    private fun obtenerIdUsuarioActual(): Int {
        val prefs = getSharedPreferences("MyCarCheckPrefs", MODE_PRIVATE)
        return prefs.getInt("id_usuario", -1)
    }

    private fun cargarVehiculos() {
        val idUsuario = obtenerIdUsuarioActual()
        val vehiculos = baseDeDatos.getVehiculosPorUsuario(idUsuario)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            vehiculos.map { v -> "${v.matricula} - ${v.marca} ${v.modelo} (${v.kmActuales} km)" }
        )

        listaVehiculos.adapter = adapter

        listaVehiculos.setOnItemClickListener { _, _, position, _ ->
            val vehiculoSeleccionado = vehiculos[position]

            val opciones = arrayOf("Ver historial", "Editar vehículo", "Eliminar vehículo")
            AlertDialog.Builder(this)
                .setTitle("Acciones para ${vehiculoSeleccionado.matricula}")
                .setItems(opciones) { _, which ->
                    when (which) {
                        0 -> {
                            val intent = Intent(this, HistorialRepostajesActivity::class.java)
                            intent.putExtra("idVehiculo", vehiculoSeleccionado.idVehiculo)
                            startActivity(intent)
                        }

                        1 -> {
                            val intent = Intent(this, EditarVehiculoActivity::class.java)
                            intent.putExtra("idVehiculo", vehiculoSeleccionado.idVehiculo)
                            startActivity(intent)
                        }

                        2 -> {
                            AlertDialog.Builder(this)
                                .setTitle("Eliminar vehículo")
                                .setMessage("¿Seguro que quieres eliminar el vehículo ${vehiculoSeleccionado.matricula}? Esta acción eliminará también sus repostajes.")
                                .setPositiveButton("Eliminar") { _, _ ->
                                    baseDeDatos.eliminarVehiculo(vehiculoSeleccionado.idVehiculo)
                                    Toast.makeText(this, "Vehículo eliminado", Toast.LENGTH_SHORT)
                                        .show()
                                    cargarVehiculos()
                                }
                                .setNegativeButton("Cancelar", null)
                                .show()
                        }
                    }
                }
                .show()
        }
    }
}
