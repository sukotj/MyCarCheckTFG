package com.example.mycarcheckkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
            val intent = Intent(this, HistorialRepostajesActivity::class.java)
            intent.putExtra("idVehiculo", vehiculoSeleccionado.idVehiculo)
            startActivity(intent)
        }

        listaVehiculos.setOnItemLongClickListener { _, _, position, _ ->
            val vehiculo = vehiculos[position]

            AlertDialog.Builder(this)
                .setTitle("Eliminar vehículo")
                .setMessage("¿Seguro que quieres eliminar el vehículo ${vehiculo.matricula}?")
                .setPositiveButton("Eliminar") { _, _ ->
                    baseDeDatos.eliminarVehiculo(vehiculo.idVehiculo)
                    Toast.makeText(this, "Vehículo eliminado", Toast.LENGTH_SHORT).show()
                    cargarVehiculos() // recarga la lista
                }
                .setNegativeButton("Cancelar", null)
                .show()

            true
        }


        //boton para volver al inicio
        btnVolverInicio.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        listaVehiculos.setOnItemClickListener { _, _, position, _ ->
            val vehiculoSeleccionado = vehiculos[position]

            val opciones = arrayOf("Ver historial", "Editar vehículo")
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
                    }
                }
                .show()
        }

        //ELIMINAR??
        //cambio de pantala a agregar repostaje
        btnAgregarRepostaje.setOnClickListener {
            Toast.makeText(this, "Selecciona un vehículo tocando en la lista", Toast.LENGTH_SHORT)
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

        // Opcional: puedes volver a asignar los listeners si lo necesitas tras recargar
        listaVehiculos.setOnItemClickListener { _, _, position, _ ->
            val vehiculoSeleccionado = vehiculos[position]
            val intent = Intent(this, HistorialRepostajesActivity::class.java)
            intent.putExtra("idVehiculo", vehiculoSeleccionado.idVehiculo)
            startActivity(intent)
        }

        listaVehiculos.setOnItemLongClickListener { _, _, position, _ ->
            val vehiculo = vehiculos[position]

            AlertDialog.Builder(this)
                .setTitle("Eliminar vehículo")
                .setMessage("¿Seguro que quieres eliminar el vehículo ${vehiculo.matricula}?")
                .setPositiveButton("Eliminar") { _, _ ->
                    baseDeDatos.eliminarVehiculo(vehiculo.idVehiculo)
                    Toast.makeText(this, "Vehículo eliminado", Toast.LENGTH_SHORT).show()
                    cargarVehiculos()
                }
                .setNegativeButton("Cancelar", null)
                .show()

            true
        }
    }

}
