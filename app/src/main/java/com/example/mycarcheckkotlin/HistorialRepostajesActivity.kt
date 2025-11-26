package com.example.mycarcheckkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class HistorialRepostajesActivity : AppCompatActivity() {

    //elementos visuales del xml
    private lateinit var rvRepostajes: RecyclerView
    private lateinit var tvConsumoTotal: TextView
    private lateinit var btnRegistrarRepostaje: MaterialButton
    private lateinit var btnVolver: MaterialButton
    private lateinit var adapter: RepostajeAdapter
    private lateinit var baseDeDatos: BaseDeDatos

    //recibida por el intent
    private var idVehiculo: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_repostajes)

        //inicializamos los datos del xml
        rvRepostajes = findViewById(R.id.rvRepostajes)
        tvConsumoTotal = findViewById(R.id.tvConsumoTotal)
        btnRegistrarRepostaje = findViewById(R.id.btnRegistrarRepostaje)
        btnVolver = findViewById(R.id.btnVolver)
        baseDeDatos = BaseDeDatos(this)

        //obtenemos el id por el intent
        idVehiculo = intent.getIntExtra("idVehiculo", -1)

        //comprobamos que el id es valido
        if (idVehiculo == -1) {
            Toast.makeText(this, "Vehículo no definido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        //cargamos todos los repostajes
        cargarRepostajes()

        //boton para volver atras
        btnVolver.setOnClickListener {
            finish()
        }

        //botón para registrar nuevo repostaje
        btnRegistrarRepostaje.setOnClickListener {
            val intent = Intent(this, AgregarRepostajeActivity::class.java)
            intent.putExtra("idVehiculo", idVehiculo)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarRepostajes()
    }

    //funcion para cargar todos los repostajes y actualizar la vista
    private fun cargarRepostajes() {
        //obtenemos la lista de todos los repostajes por id
        val lista = baseDeDatos.getRepostajesPorVehiculo(idVehiculo)

        //creamos el adaptador y llamamos a la funcion de la bbdd para eliminar un repostaje funcion lambda
        adapter = RepostajeAdapter(
            lista,
            onEliminar = { idRepostaje ->
                baseDeDatos.eliminarRepostaje(idRepostaje)
                Toast.makeText(this, "Repostaje eliminado", Toast.LENGTH_SHORT).show()
                cargarRepostajes()
            },
            onEditar = { idRepostaje ->
                val intent = Intent(this, AgregarRepostajeActivity::class.java)
                intent.putExtra("idRepostaje", idRepostaje)
                intent.putExtra("idVehiculo", idVehiculo)
                startActivity(intent)
            }
        )

        //usamos linear layout para mostrarlos de manera vertical
        rvRepostajes.layoutManager = LinearLayoutManager(this)
        rvRepostajes.adapter = adapter

        //recuperamos la funcion de calcular consumo y lo mostramos
        val consumoMedio = baseDeDatos.calcularConsumoMedio(idVehiculo)
        tvConsumoTotal.text =
            "Consumo medio total: ${"%.2f".format(consumoMedio)} L/100km" //2 cifras y en formato float
    }
}
