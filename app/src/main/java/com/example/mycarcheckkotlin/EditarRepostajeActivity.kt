package com.example.mycarcheckkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class EditarRepostajeActivity : AppCompatActivity() {

    //elementos visuales del xml
    private lateinit var etFecha: EditText
    private lateinit var etKmActuales: EditText
    private lateinit var etLitros: EditText
    private lateinit var etPrecioLitro: EditText
    private lateinit var btnGuardarCambios: Button
    private lateinit var baseDeDatos: BaseDeDatos

    //identificador del repostaje al editar
    private var idRepostaje: Int = -1
    //reposaje original extraido de la bbdd
    private lateinit var repostajeOriginal: Repostaje

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_repostaje)

        //inicializamos los datos del xml
        etFecha = findViewById(R.id.etFecha)
        etKmActuales = findViewById(R.id.etKmActuales)
        etLitros = findViewById(R.id.etLitros)
        etPrecioLitro = findViewById(R.id.etPrecioLitro)
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios)
        baseDeDatos = BaseDeDatos(this)

        //recuperamos el repostaje mediante el intent
        idRepostaje = intent.getIntExtra("idRepostaje", -1)
        if (idRepostaje == -1) {
            Toast.makeText(this, "Repostaje no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        //obtenemos el repostaje desde la bbdd
        val repostaje = baseDeDatos.getRepostajePorId(idRepostaje)
        if (repostaje == null) {
            Toast.makeText(this, "No se encontró el repostaje", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        //lo guardamos para posibles validaciones
        repostajeOriginal = repostaje

        //precargamos los datos en los canmpos del formulario
        etFecha.setText(repostaje.fecha)
        etKmActuales.setText(repostaje.kmActuales.toString())
        etLitros.setText(repostaje.litros.toString())
        etPrecioLitro.setText(repostaje.precioLitro.toString())

        //guardamos los cambios
        btnGuardarCambios.setOnClickListener {
            val fecha = etFecha.text.toString()
            val kmActuales = etKmActuales.text.toString().toIntOrNull()
            val litros = etLitros.text.toString().toDoubleOrNull()
            val precioLitro = etPrecioLitro.text.toString().toDoubleOrNull()

            //validamos si los datos introducidos son correctos
            if (fecha.isBlank() || kmActuales == null || litros == null || precioLitro == null ||
                kmActuales <= repostajeOriginal.kmAnterior || litros <= 0.0 || precioLitro <= 0.0
            ) {
                Toast.makeText(this, "Datos inválidos o incompletos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //creamos un nuevo objeto con los datos editados
            val repostajeEditado = Repostaje(
                idRepostaje = idRepostaje,
                idVehiculo = repostajeOriginal.idVehiculo,
                fecha = fecha,
                kmAnterior = repostajeOriginal.kmAnterior,
                kmActuales = kmActuales,
                litros = litros,
                precioLitro = precioLitro
            )

            //actualiozamos en la base de datos
            val resultado = baseDeDatos.editarRepostaje(repostajeEditado)
            if (resultado > 0) {
                Toast.makeText(this, "Repostaje actualizado", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HistorialRepostajesActivity::class.java)
                intent.putExtra("idVehiculo", repostajeOriginal.idVehiculo)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}