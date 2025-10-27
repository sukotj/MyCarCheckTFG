package com.example.mycarcheckkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.mycarcheckkotlin.MenuPrincipalActivity
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
        var consumoMedioGlobal = 0.0

        for (vehiculo in vehiculos) {
            val repostajes = baseDeDatos.getRepostajesPorVehiculo(vehiculo.idVehiculo)
            for (r in repostajes) {
                totalLitros += r.litros
                totalCoste += r.litros * r.precioLitro
                totalKm += r.kmActuales - r.kmAnterior
            }
            consumoMedioGlobal += baseDeDatos.calcularConsumoMedio(vehiculo.idVehiculo)
        }

        val consumoMedioFinal =
            if (vehiculos.isNotEmpty()) consumoMedioGlobal / vehiculos.size else 0.0

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
        // Simulación temporal
        return 1
    }
}
