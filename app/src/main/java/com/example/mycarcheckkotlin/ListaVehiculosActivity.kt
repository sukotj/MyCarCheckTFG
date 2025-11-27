package com.example.mycarcheckkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class ListaVehiculosActivity : AppCompatActivity() {

    private lateinit var rvVehiculos: RecyclerView
    private lateinit var baseDeDatos: BaseDeDatos
    private lateinit var btnVolverInicio: MaterialButton
    private lateinit var btnAgregarRepostaje: MaterialButton
    private lateinit var adapter: VehiculoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_vehiculos)

        rvVehiculos = findViewById(R.id.rvVehiculos)
        btnVolverInicio = findViewById(R.id.btnVolverInicio)
        btnAgregarRepostaje = findViewById(R.id.btnAgregarRepostaje)
        baseDeDatos = BaseDeDatos(this)

        //configuramos el RecyclerView con un layout vertical
        rvVehiculos.layoutManager = LinearLayoutManager(this)

        //cargamos los vehiculos al iniciar la actividad
        cargarVehiculos()

        //boton para volver al menu principal
        btnVolverInicio.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        //boton para registrar un repostaje
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

    //obtiene el id del usuario actual desde SharedPreferences
    private fun obtenerIdUsuarioActual(): Int {
        val prefs = getSharedPreferences("MyCarCheckPrefs", MODE_PRIVATE)
        return prefs.getInt("id_usuario", -1)
    }

    //carga los vehiculos del usuario y configura el adaptador
    private fun cargarVehiculos() {
        val idUsuario = obtenerIdUsuarioActual()
        val vehiculos = baseDeDatos.getVehiculosPorUsuario(idUsuario)

        if (vehiculos.isEmpty()) {
            Toast.makeText(this, "No tienes vehículos registrados", Toast.LENGTH_SHORT).show()
            return
        }

        //adaptador con tres acciones: ver repostajes, editar y eliminar
        adapter = VehiculoAdapter(
            vehiculos,
            onClickVerRepostajes = { vehiculo ->
                val intent = Intent(this, HistorialRepostajesActivity::class.java)
                intent.putExtra("idVehiculo", vehiculo.idVehiculo)
                startActivity(intent)
            },
            onClickEditar = { vehiculo ->
                val intent = Intent(this, EditarVehiculoActivity::class.java)
                intent.putExtra("idVehiculo", vehiculo.idVehiculo)
                startActivity(intent)
            },
            onClickEliminar = { vehiculo ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminar vehículo")
                    .setMessage("¿Seguro que quieres eliminar el vehículo ${vehiculo.matricula}? Esta acción eliminará también sus repostajes.")
                    .setPositiveButton("Eliminar") { _, _ ->
                        baseDeDatos.eliminarVehiculo(vehiculo.idVehiculo)
                        Toast.makeText(this, "Vehículo eliminado", Toast.LENGTH_SHORT).show()
                        cargarVehiculos() //recargamos la lista tras eliminar
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )

        rvVehiculos.adapter = adapter
    }

}
