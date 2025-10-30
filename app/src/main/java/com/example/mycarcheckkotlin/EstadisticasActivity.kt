package com.example.mycarcheckkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class EstadisticasActivity : AppCompatActivity() {

    private lateinit var tvConsumoMedio: TextView
    private lateinit var tvCosteTotal: TextView
    private lateinit var tvLitrosTotales: TextView
    private lateinit var tvKmTotales: TextView
    private lateinit var btnVolverInicio: Button
    private lateinit var baseDeDatos: BaseDeDatos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas)

        tvConsumoMedio = findViewById(R.id.tvConsumoMedio)
        tvCosteTotal = findViewById(R.id.tvCosteTotal)
        tvLitrosTotales = findViewById(R.id.tvLitrosTotales)
        tvKmTotales = findViewById(R.id.tvKmTotales)
        btnVolverInicio = findViewById(R.id.btnVolverInicio)
        baseDeDatos = BaseDeDatos(this)

        val idUsuario = obtenerIdUsuarioActual()
        val vehiculos = baseDeDatos.getVehiculosPorUsuario(idUsuario)

        var totalLitros = 0.0
        var totalCoste = 0.0
        var totalKm = 0

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

        val consumoMedioFinal = if (totalLitros > 0) totalKm / totalLitros else 0.0

        tvConsumoMedio.text = "Consumo medio: %.2f km/L".format(consumoMedioFinal)
        tvCosteTotal.text = "Coste total: %.2f €".format(totalCoste)
        tvLitrosTotales.text = "Litros totales: %.2f L".format(totalLitros)
        tvKmTotales.text = "Km recorridos: $totalKm km"

        btnVolverInicio.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    private fun obtenerIdUsuarioActual(): Int {
        val prefs = getSharedPreferences("MyCarCheckPrefs", MODE_PRIVATE)
        return prefs.getInt("id_usuario", -1)
    }
}