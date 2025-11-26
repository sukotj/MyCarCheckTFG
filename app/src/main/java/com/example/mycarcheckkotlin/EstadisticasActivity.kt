package com.example.mycarcheckkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class EstadisticasActivity : AppCompatActivity() {

    //elementos visuales del xml
    private lateinit var tvConsumoMedio: TextView
    private lateinit var tvCosteTotal: TextView
    private lateinit var tvLitrosTotales: TextView
    private lateinit var tvKmTotales: TextView
    private lateinit var btnVolverInicio: MaterialButton
    private lateinit var baseDeDatos: BaseDeDatos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas)

        //inicializamos los datos del xml
        tvConsumoMedio = findViewById(R.id.tvConsumoMedio)
        tvCosteTotal = findViewById(R.id.tvCosteTotal)
        tvLitrosTotales = findViewById(R.id.tvLitrosTotales)
        tvKmTotales = findViewById(R.id.tvKmTotales)
        btnVolverInicio = findViewById(R.id.btnVolverInicio)
        baseDeDatos = BaseDeDatos(this)

        //obtenemos el id del usuario actual
        val idUsuario = obtenerIdUsuarioActual()

        //obtenemos todos los vehículos del usuario
        val vehiculos = baseDeDatos.getVehiculosPorUsuario(idUsuario)

        //variables para acumular los datos
        var totalLitros = 0.0
        var totalCoste = 0.0
        var totalKm = 0

        //recorremos cada vehículo y sus repostajes
        for (vehiculo in vehiculos) {
            val repostajes = baseDeDatos.getRepostajesPorVehiculo(vehiculo.idVehiculo)
            for (r in repostajes) {
                val kmRecorridos = r.kmActuales - r.kmAnterior
                if (kmRecorridos > 0) {
                    totalLitros += r.litros
                    totalCoste += r.litros * r.precioLitro
                    totalKm += kmRecorridos
                }
            }
        }

        //calculamos el consumo medio
        val consumoMedioFinal = if (totalKm > 0) (totalLitros / totalKm) * 100 else 0.0

        //mostramos los resultados en pantalla
        tvConsumoMedio.text = "Consumo medio: %.2f L/100km".format(consumoMedioFinal)
        tvCosteTotal.text = "Coste total: %.2f €".format(totalCoste)
        tvLitrosTotales.text = "Litros totales: %.2f L".format(totalLitros)
        tvKmTotales.text = "Km recorridos: $totalKm km"

        //botón para volver al menú principal
        btnVolverInicio.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    //función para obtener el id del usuario desde shared preferences
    private fun obtenerIdUsuarioActual(): Int {
        val prefs = getSharedPreferences("MyCarCheckPrefs", MODE_PRIVATE)
        return prefs.getInt("id_usuario", -1)
    }
}
