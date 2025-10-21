package com.example.mycarcheckkotlin


import AgregarVehiculoActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Clase principal de la pantalla de inicio
class MainActivity : AppCompatActivity() {

    // Declaramos variables para los elementos de la interfaz
    private lateinit var rvVehiculos: RecyclerView
    private lateinit var tvBienvenida: TextView
    private lateinit var btnAgregarVehiculo: Button
    private lateinit var btnCerrarSesion: Button
    private lateinit var db: BaseDeDatos // Clase que gestiona la base de datos SQLite
    private var usuario: Usuario? = null // Usuario actual (puede ser null si no se encuentra)

    // Método que se ejecuta al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Cargamos el diseño XML

        // Enlazamos las vistas con sus IDs
        rvVehiculos = findViewById(R.id.rvVehiculos)
        tvBienvenida = findViewById(R.id.tvBienvenida)
        btnAgregarVehiculo = findViewById(R.id.btnAgregarVehiculo)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)
        db = BaseDeDatos(this) // Inicializamos la base de datos

        // Obtenemos el ID del usuario guardado en SharedPreferences
        val idUsuario = obtenerIdUsuario()

        // Si no hay usuario guardado, mostramos mensaje y ocultamos el saludo
        if (idUsuario == -1) {
            Toast.makeText(this, "Usuario no identificado.", Toast.LENGTH_SHORT).show()
            tvBienvenida.visibility = View.GONE
            return // Salimos del método
        }

        // Recuperamos el nombre del usuario desde SharedPreferences
        val prefs = getSharedPreferences("MyCarCheckPrefs", MODE_PRIVATE)
        val nombreUsuario = prefs.getString("nombre_usuario", null)

        // Mostramos el saludo personalizado
        tvBienvenida.text = nombreUsuario?.let { "Bienvenido, $it" } ?: "Bienvenido"


        // Mostramos los vehículos del usuario
        mostrarVehiculos(idUsuario)

        // Configuramos el botón para ir a la pantalla de agregar vehículo
        btnAgregarVehiculo.setOnClickListener {
            startActivity(Intent(this, AgregarVehiculoActivity::class.java))
        }

        btnCerrarSesion.setOnClickListener {
            val prefs = getSharedPreferences("MyCarCheckPrefs", MODE_PRIVATE)
            prefs.edit().remove("id_usuario").apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    // Método que se ejecuta al volver a esta pantalla (por ejemplo, tras agregar un vehículo)
    override fun onResume() {
        super.onResume()
        val idUsuario = obtenerIdUsuario()
        if (idUsuario == -1) {
            Toast.makeText(this, "Usuario no identificado.", Toast.LENGTH_SHORT).show()
            tvBienvenida.visibility = View.GONE
            return
        }

    }

    // Función auxiliar para obtener el ID del usuario desde SharedPreferences
    private fun obtenerIdUsuario(): Int {
        val prefs = getSharedPreferences("MyCarCheckPrefs", MODE_PRIVATE)
        return prefs.getInt("id_usuario", -1)
    }

    // Función que muestra los vehículos del usuario en pantalla
    private fun mostrarVehiculos(idUsuario: Int) {
        val vehiculos = db.getVehiculosPorUsuario(idUsuario)

        if (vehiculos.isEmpty()) {
            Toast.makeText(this, "No tienes vehículos registrados.", Toast.LENGTH_SHORT).show()
            return
        }

        rvVehiculos.layoutManager = LinearLayoutManager(this)
        rvVehiculos.adapter = VehiculoAdapter(vehiculos) { vehiculo ->
            val intent = Intent(this, HistorialRepostajesActivity::class.java)
            intent.putExtra("id_vehiculo", vehiculo.idVehiculo)
            startActivity(intent)
        }
    }

}
