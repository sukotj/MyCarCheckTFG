package com.example.mycarcheckkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class AgregarRepostajeActivity : AppCompatActivity() {

    //elementos visuales del xml
    private lateinit var etLitros: TextInputEditText
    private lateinit var etPrecioLitro: TextInputEditText
    private lateinit var etKmActuales: TextInputEditText
    private lateinit var etFecha: TextInputEditText
    private lateinit var tvKmAnterior: TextView
    private lateinit var btnGuardar: MaterialButton
    private lateinit var btnVolver: MaterialButton

    //referencia a nuestra bbdd
    private lateinit var baseDeDatos: BaseDeDatos

    //usamos -1 para saber que hemos recibido un id correcto en caso contrario al recuperar -1 en nuestra id detectaremos el problem
    private var idVehiculo: Int = -1
    private var kmAnterior: Int = 0
    private var idRepostaje: Int = -1
    private var esEdicion: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_repostaje)

        //inicializamos los datos del xml
        etLitros = findViewById(R.id.etLitros)
        etPrecioLitro = findViewById(R.id.etPrecioLitro)
        etKmActuales = findViewById(R.id.etKmActuales)
        etFecha = findViewById(R.id.etFecha)
        tvKmAnterior = findViewById(R.id.tvKmAnterior)
        btnGuardar = findViewById(R.id.btnGuardarRepostaje)
        btnVolver = findViewById(R.id.btnVolver)

        //inicializamos la base de datos
        baseDeDatos = BaseDeDatos(this)

        //recuperamos datos del intent
        idVehiculo = intent.getIntExtra("idVehiculo", -1)
        idRepostaje = intent.getIntExtra("idRepostaje", -1)
        esEdicion = idRepostaje != -1

        if (idVehiculo == -1) {
            Toast.makeText(this, "Error: vehículo no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        //mostrar los km anteriores en pantalla
        kmAnterior = baseDeDatos.obtenerKmAnterior(idVehiculo)
        tvKmAnterior.text = "Km anteriores: $kmAnterior"

        //si estamos editando, cargamos datos del repostaje
        if (esEdicion) {
            //si no es null la id se ejecuta el bloque con la variable repostaje para evitar la excepcion
            baseDeDatos.getRepostajePorId(idRepostaje)?.let { repostaje ->
                etLitros.setText(repostaje.litros.toString())
                etPrecioLitro.setText(repostaje.precioLitro.toString())
                etKmActuales.setText(repostaje.kmActuales.toString())
                etFecha.setText(repostaje.fecha)
                tvKmAnterior.text = "Km anteriores: ${repostaje.kmAnterior}"
                kmAnterior = repostaje.kmAnterior
            }
        }

        //boton para guardar el repostaje
        btnGuardar.setOnClickListener {
            guardarRepostaje()
        }
        //boton para volver al inicio
        btnVolver.setOnClickListener {
            finish()
        }
    }

    //ontenemos los datos y guardamos el repostaje en la base de datos
    private fun guardarRepostaje() {
        //hacemos la conversion de los datos. usamos todouble or null para convertir el string a double sin lanzar excepcion si falla
        val litros = etLitros.text.toString().toDoubleOrNull()
        val precioLitro = etPrecioLitro.text.toString().toDoubleOrNull()
        val kmActuales = etKmActuales.text.toString().toIntOrNull()
        val fecha = etFecha.text.toString()

        //comprobamos que todos los campos estan rellenos sino mostramos un toast
        if (litros == null || litros <= 0.0 ||
            precioLitro == null || precioLitro <= 0.0 ||
            kmActuales == null || kmActuales <= 0 ||
            fecha.isBlank()
        ) {
            mostrarError("Por favor, completa todos los campos correctamente")
            return
        }

        //expresion regular para validar la fecha correctamente
        val regexFecha = Regex("""\d{2}/\d{2}/\d{4}""")
        if (!regexFecha.matches(fecha)) {
            mostrarError("Formato de fecha inválido (dd/mm/yyyy)")
            return
        }

        //si no estamos en modo edicion y los km nuevos son menores que los anteriores salta el mesaje
        if (!esEdicion && kmActuales <= kmAnterior) {
            mostrarError("Los kilómetros actuales deben ser mayores que los anteriores ($kmAnterior)")
            return
        }

        //creamos el objeto repostaje con los datos que se han introducido en el formulario
        val repostaje = Repostaje(
            idRepostaje = if (esEdicion) idRepostaje else 0,
            idVehiculo = idVehiculo,
            fecha = fecha,
            kmAnterior = kmAnterior,
            kmActuales = kmActuales,
            litros = litros,
            precioLitro = precioLitro
        )

        //si esedicion es true editamos el repostaje si es false, llamamos a insertar resposteje
        val resultado = if (esEdicion) baseDeDatos.editarRepostaje(repostaje)
        else baseDeDatos.insertarRepostaje(repostaje)

        if ((esEdicion && resultado != 0) || (!esEdicion && resultado != -1L)) {
            baseDeDatos.actualizarKmVehiculo(idVehiculo, kmActuales)
            Toast.makeText(this, "Repostaje guardado correctamente", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, HistorialRepostajesActivity::class.java).apply {
                putExtra("idVehiculo", idVehiculo)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
        } else {
            mostrarError("Error al guardar el repostaje")
        }
    }

    //funcion creada para los posibles errores y no repetir codigo
    private fun mostrarError(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    //ocultar teclado
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) ocultarTeclado()
        return super.dispatchTouchEvent(ev)
    }

    private fun ocultarTeclado() {
        currentFocus?.let { view ->
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
    }
}
