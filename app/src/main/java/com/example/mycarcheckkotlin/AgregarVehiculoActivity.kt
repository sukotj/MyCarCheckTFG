package com.example.mycarcheckkotlin

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class AgregarVehiculoActivity : AppCompatActivity() {

    //elementos visuales del xml
    private lateinit var etMarca: TextInputEditText
    private lateinit var etModelo: TextInputEditText
    private lateinit var etMatricula: TextInputEditText
    private lateinit var etKmActuales: TextInputEditText
    private lateinit var etAnio: TextInputEditText
    private lateinit var etPotencia: TextInputEditText
    private lateinit var spinnerCombustible: Spinner
    private lateinit var btnGuardarVehiculo: MaterialButton
    private lateinit var btnVolverInicio: MaterialButton

    //referencia a nuestra bbdd
    private lateinit var db: BaseDeDatos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_vehiculo)

        //inicializamos los datos del xml
        etMarca = findViewById(R.id.etMarca)
        etModelo = findViewById(R.id.etModelo)
        etMatricula = findViewById(R.id.etMatricula)
        etKmActuales = findViewById(R.id.etKmActuales)
        etAnio = findViewById(R.id.etAnio)
        etPotencia = findViewById(R.id.etPotencia)
        spinnerCombustible = findViewById(R.id.spinnerCombustible)
        btnGuardarVehiculo = findViewById(R.id.btnGuardarVehiculo)
        btnVolverInicio = findViewById(R.id.btnVolverInicio)
        db = BaseDeDatos(this)

        //creamos un array con los tipos de combustible para el spinner
        val tiposCombustible = arrayOf("Gasolina", "Diésel", "Eléctrico", "Híbrido", "GLP")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tiposCombustible)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCombustible.adapter = adapter

        //si no tenemos un usuario logeado, mostramos el error y cerramos el activity
        val idUsuario = obtenerIdUsuario()
        if (idUsuario == -1) {
            mostrarError("Usuario no identificado")
            finish()
            return
        }

        //guardar vehículo
        btnGuardarVehiculo.setOnClickListener {
            guardarVehiculo(idUsuario)
        }

        //volver al inicio
        btnVolverInicio.setOnClickListener {
            startActivity(Intent(this, MenuPrincipalActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
        }
    }

    //guardamos los campos del formulario
    private fun guardarVehiculo(idUsuario: Int) {
        val marca = etMarca.text.toString().trim()
        val modelo = etModelo.text.toString().trim()
        val matricula = etMatricula.text.toString().trim()
        val kmActualesStr = etKmActuales.text.toString().trim()
        val anioStr = etAnio.text.toString().trim()
        val potenciaStr = etPotencia.text.toString().trim()
        val tipoCombustible = spinnerCombustible.selectedItem.toString()

        //validamos que los campos no esten vacios
        if (marca.isEmpty() || modelo.isEmpty() || matricula.isEmpty() ||
            kmActualesStr.isEmpty() || anioStr.isEmpty() || potenciaStr.isEmpty()
        ) {
            mostrarError("Rellena todos los campos")
            return
        }

        //comprobamos que el año es correcto
        val anio = anioStr.toIntOrNull()
        val anioActual = Calendar.getInstance().get(Calendar.YEAR)
        if (anio == null || anio !in 1950..anioActual) {
            mostrarError("Año inválido (debe estar entre 1950 y $anioActual)")
            return
        }

        //comprobamos que la potencia es correcta
        val potencia = potenciaStr.toIntOrNull()
        if (potencia == null || potencia <= 0) {
            mostrarError("Potencia inválida")
            return
        }

        //comprobamos que los km son distintos a los actuales
        val kmActuales = kmActualesStr.toIntOrNull()
        if (kmActuales == null || kmActuales < 0) {
            mostrarError("Kilómetros inválidos")
            return
        }

        val vehiculo = Vehiculo(
            idVehiculo = 0,
            matricula = matricula,
            marca = marca,
            modelo = modelo,
            motor = potencia,
            tipoCombustible = tipoCombustible,
            anoMatriculacion = anio,
            kmActuales = kmActuales,
            idUsuario = idUsuario
        )

        //guardamos el vehiculo en la bbdd
        val id = db.insertarVehiculo(vehiculo)
        if (id != -1L) {
            Toast.makeText(this, "Vehículo guardado", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            mostrarError("Error al guardar")
        }
    }

    //funcion para obtener la id del usuario desde shared preferences
    private fun obtenerIdUsuario(): Int {
        val prefs = getSharedPreferences("MyCarCheckPrefs", MODE_PRIVATE)
        return prefs.getInt("id_usuario", -1)
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
