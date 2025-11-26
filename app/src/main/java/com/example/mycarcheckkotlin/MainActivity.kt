package com.example.mycarcheckkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    //elementos visuales del xml
    private lateinit var rvVehiculos: RecyclerView
    private lateinit var tvBienvenida: TextView
    private lateinit var btnAgregarVehiculo: MaterialButton
    private lateinit var btnCerrarSesion: MaterialButton
    private lateinit var btnIrAlInicio: MaterialButton
    private lateinit var db: BaseDeDatos
    private var usuario: Usuario? = null // Usuario actual (puede ser null si no se encuentra)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //inicializamos los datos del xml
        rvVehiculos = findViewById(R.id.rvVehiculos)
        tvBienvenida = findViewById(R.id.tvBienvenida)
        btnAgregarVehiculo = findViewById(R.id.btnAgregarVehiculo)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)
        btnIrAlInicio = findViewById(R.id.btnIrAlInicio)
        db = BaseDeDatos(this)

        //obtenemos el id
        val idUsuario = obtenerIdUsuario()

        //si nos devuelve -1 significa que no hay usuario y nos saca del metodo
        if (idUsuario == -1) {
            Toast.makeText(this, "Usuario no identificado.", Toast.LENGTH_SHORT).show()
            tvBienvenida.visibility = View.GONE
            return
        }

        //recuperamos el nombre del usuario desde sharedpreferences
        val prefs = getSharedPreferences("MyCarCheckPrefs", MODE_PRIVATE)
        val nombreUsuario = prefs.getString("nombre_usuario", null)

        //hacemos un saludo personalizado y mostramos sus vehiculos
        tvBienvenida.text = nombreUsuario?.let { "Bienvenido, $it" } ?: "Bienvenido"
        mostrarVehiculos(idUsuario)

        //boton para agregar vehiculo
        btnAgregarVehiculo.setOnClickListener {
            startActivity(Intent(this, AgregarVehiculoActivity::class.java))
        }

        //boton para cerrar la sesion
        btnCerrarSesion.setOnClickListener {
            val prefs = getSharedPreferences("MyCarCheckPrefs", MODE_PRIVATE)
            prefs.edit().clear().apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        //boton para volver al menú principal
        btnIrAlInicio.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    //se ejecuta al volver a esta pantalla, por ejemplo si metemos un vehiculo
    override fun onResume() {
        super.onResume()
        val idUsuario = obtenerIdUsuario()
        if (idUsuario == -1) {
            Toast.makeText(this, "Usuario no identificado.", Toast.LENGTH_SHORT).show()
            tvBienvenida.visibility = View.GONE
            return
        }

        mostrarVehiculos(idUsuario)
    }

    //obtenemos el usuario desde sharedpreferences
    private fun obtenerIdUsuario(): Int {
        val prefs = getSharedPreferences("MyCarCheckPrefs", MODE_PRIVATE)
        return prefs.getInt("id_usuario", -1)
    }

    //recuperamos los vehiculos del usuario
    private fun mostrarVehiculos(idUsuario: Int) {
        val vehiculos = db.getVehiculosPorUsuario(idUsuario)

        if (vehiculos.isEmpty()) {
            Toast.makeText(this, "No tienes vehículos registrados.", Toast.LENGTH_SHORT).show()
            return
        }

        //mostramos los elementos de manera vertical
        rvVehiculos.layoutManager = LinearLayoutManager(this)

        //creamos el adaptador pasando las dos acciones necesarias
        rvVehiculos.adapter = VehiculoAdapter(
            vehiculos,
            onClickVerRepostajes = { vehiculo ->
                val intent = Intent(this, HistorialRepostajesActivity::class.java)
                intent.putExtra(
                    "idVehiculo",
                    vehiculo.idVehiculo
                )
                startActivity(intent)
            },
            onClickEliminar = { vehiculo ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminar vehículo")
                    .setMessage("¿Seguro que quieres eliminar el vehículo ${vehiculo.matricula}? Esta acción eliminará también sus repostajes.")
                    .setPositiveButton("Eliminar") { _, _ ->
                        db.eliminarVehiculo(vehiculo.idVehiculo)
                        Toast.makeText(this, "Vehículo eliminado", Toast.LENGTH_SHORT).show()
                        mostrarVehiculos(idUsuario) //recargamos la lista
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )
    }

}
