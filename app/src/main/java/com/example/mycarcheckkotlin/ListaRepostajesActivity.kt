package com.example.mycarcheckkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ListaRepostajesActivity : AppCompatActivity() {

    //elementos visuales del xml
    private lateinit var listaRepostajes: ListView
    private lateinit var baseDeDatos: BaseDeDatos
    private lateinit var btnVolverInicio: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_repostajes)

        //inicializamos los datos del xml
        listaRepostajes = findViewById(R.id.listaRepostajes)
        btnVolverInicio = findViewById(R.id.btnVolverInicio)
        baseDeDatos = BaseDeDatos(this)

        //obtenemos el usuario y el vehiculo del usuario actual
        val idUsuario = obtenerIdUsuarioActual()
        val vehiculos = baseDeDatos.getVehiculosPorUsuario(idUsuario)

        //creamos una lista de string con los repostajes
        val todosLosRepostajes = mutableListOf<String>()
        for (vehiculo in vehiculos) {
            val repostajes = baseDeDatos.getRepostajesPorVehiculo(vehiculo.idVehiculo)
            for (r in repostajes) {
                todosLosRepostajes.add(
                    "Vehículo: ${vehiculo.matricula} | ${r.fecha} - ${r.litros}L @ ${r.precioLitro}€/L - ${r.kmActuales} km"
                )
            }
        }

        //creamos el adaptador y lo añadimos al listview
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, todosLosRepostajes)
        listaRepostajes.adapter = adapter

        btnVolverInicio.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    //obtenemos el usuario desde sharedpreferences
    private fun obtenerIdUsuarioActual(): Int {
        val prefs = getSharedPreferences("MyCarCheckPrefs", MODE_PRIVATE)
        return prefs.getInt("id_usuario", -1)
    }
}
