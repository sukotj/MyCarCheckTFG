package com.example.mycarcheckkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AgregarVehiculoActivity : AppCompatActivity() {

    //elementos visuales del xml
    private lateinit var etMarca: EditText
    private lateinit var etModelo: EditText
    private lateinit var etMatricula: EditText
    private lateinit var etAnio: EditText
    private lateinit var etPotencia: EditText
    private lateinit var spinnerCombustible: Spinner
    private lateinit var btnGuardarVehiculo: Button

    //referencia a nuestra bbdd
    private lateinit var db: BaseDeDatos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_vehiculo)

        //inicializamos los datos del xml
        etMarca = findViewById(R.id.etMarca)
        etModelo = findViewById(R.id.etModelo)
        etMatricula = findViewById(R.id.etMatricula)
        etAnio = findViewById(R.id.etAnio)
        etPotencia = findViewById(R.id.etPotencia)
        spinnerCombustible = findViewById(R.id.spinnerCombustible)
        btnGuardarVehiculo = findViewById(R.id.btnGuardarVehiculo)
        db = BaseDeDatos(this)

        //creamos un array con los tipos de combustible para el spinner
        val tiposCombustible = arrayOf("Gasolina", "Diésel", "Eléctrico", "Híbrido", "GLP")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tiposCombustible)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCombustible.adapter = adapter

        //si no tenemos un usuario logeado salta el error
        val idUsuario = obtenerIdUsuario()
        if (idUsuario == -1) {
            Toast.makeText(this, "Usuario no identificado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        //guardamos los campos del formulario
        btnGuardarVehiculo.setOnClickListener {
            val marca = etMarca.text.toString().trim()
            val modelo = etModelo.text.toString().trim()
            val matricula = etMatricula.text.toString().trim()
            val anioStr = etAnio.text.toString().trim()
            val potenciaStr = etPotencia.text.toString().trim()
            val tipoCombustible = spinnerCombustible.selectedItem.toString()

            //validamos que los campos no esten vacios
            if (marca.isEmpty() || modelo.isEmpty() || matricula.isEmpty() || anioStr.isEmpty() || potenciaStr.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener //si usamos return a secas se sale del oncreate
            }

            //comprobamos que el año es correcto
            val anio = anioStr.toIntOrNull()
            if (anio == null || anio < 1900 || anio > 2100) {
                Toast.makeText(this, "Año inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //comprobamos que la potencia es correcta
            val potencia = potenciaStr.toIntOrNull()
            if (potencia == null || potencia <= 0) {
                Toast.makeText(this, "Potencia inválida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //creamos un objeto vehiculo
            val vehiculo = Vehiculo(
                idVehiculo = 0,
                matricula = matricula,
                marca = marca,
                modelo = modelo,
                motor = potencia,
                tipoCombustible = tipoCombustible,
                anoMatriculacion = anio,
                fechaCompra = "",
                kmActuales = 0,
                idUsuario = idUsuario
            )

            //guardamos el vehiculo en la bbdd
            val id = db.insertarVehiculo(vehiculo)
            if (id != -1L) {
                Toast.makeText(this, "Vehículo guardado", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
            }
        }

        //boton para volver al inicio
        val btnVolverInicio = findViewById<Button>(R.id.btnVolverInicio)
        btnVolverInicio.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

    }

    //funcion para obtener la id del usuario desde shared preferences
    private fun obtenerIdUsuario(): Int {
        val prefs = getSharedPreferences("MyCarCheckPrefs", MODE_PRIVATE)
        return prefs.getInt("id_usuario", -1)
    }
}
