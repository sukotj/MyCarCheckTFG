package com.example.mycarcheckkotlin

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AgregarRepostajeActivity : AppCompatActivity() {

    private lateinit var etLitros: EditText
    private lateinit var etPrecioLitro: EditText
    private lateinit var etKmActuales: EditText
    private lateinit var etFecha: EditText
    private lateinit var btnGuardar: Button
    private lateinit var baseDeDatos: BaseDeDatos
    private var idVehiculo: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_repostaje)

        etLitros = findViewById(R.id.etLitros)
        etPrecioLitro = findViewById(R.id.etPrecioLitro)
        etKmActuales = findViewById(R.id.etKmActuales)
        etFecha = findViewById(R.id.etFecha)
        btnGuardar = findViewById(R.id.btnGuardarRepostaje)

        baseDeDatos = BaseDeDatos(this)
        idVehiculo = intent.getIntExtra("idVehiculo", -1)

        btnGuardar.setOnClickListener {
            guardarRepostaje()
        }
    }

    private fun guardarRepostaje() {
        val litros = etLitros.text.toString().toDoubleOrNull()
        val precioLitro = etPrecioLitro.text.toString().toDoubleOrNull()
        val kmActuales = etKmActuales.text.toString().toIntOrNull()
        val fecha = etFecha.text.toString()

        if (litros == null || precioLitro == null || kmActuales == null || fecha.isBlank()) {
            Toast.makeText(this, "Por favor, completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
            return
        }

        val kmAnterior = baseDeDatos.obtenerKmAnterior(idVehiculo)

        val repostaje = Repostaje(
            idRepostaje = 0,
            idVehiculo = idVehiculo,
            fecha = fecha,
            kmAnterior = kmAnterior,
            kmActuales = kmActuales,
            litros = litros,
            precioLitro = precioLitro
        )

        val resultado = baseDeDatos.insertarRepostaje(repostaje)

        if (resultado > 0) {
            baseDeDatos.actualizarKmVehiculo(idVehiculo, kmActuales)
            Toast.makeText(this, "Repostaje guardado correctamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al guardar el repostaje", Toast.LENGTH_SHORT).show()
        }
    }
}
