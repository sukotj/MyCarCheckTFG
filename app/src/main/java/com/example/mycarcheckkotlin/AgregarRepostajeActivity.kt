package com.example.mycarcheckkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AgregarRepostajeActivity : AppCompatActivity() {

    //elementos visuales del xml
    private lateinit var etLitros: EditText
    private lateinit var etPrecioLitro: EditText
    private lateinit var etKmActuales: EditText
    private lateinit var etFecha: EditText
    private lateinit var tvKmAnterior: TextView
    private lateinit var btnGuardar: Button
    private lateinit var btnVolverInicio: Button

    //referencia a nuestra bbdd
    private lateinit var baseDeDatos: BaseDeDatos

    //usamos -1 para saber que hemos recibido un id correcto en caso contrario al recuperar -1 en nuestra id detectaremos el problema
    private var idVehiculo: Int = -1
    private var kmAnterior: Int = 0

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
        btnVolverInicio = findViewById(R.id.btnVolverInicio)

        //inicializamos la base de datos
        baseDeDatos = BaseDeDatos(this)

        //obtenemos el id del coche desde el intent
        idVehiculo = intent.getIntExtra("idVehiculo", -1)
        if (idVehiculo == -1) {
            Toast.makeText(this, "Error: vehículo no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        //mostrar los km anteriores en pantalla
        kmAnterior = baseDeDatos.obtenerKmAnterior(idVehiculo)
        tvKmAnterior.text = "Km anteriores: $kmAnterior"

        //pasamos la funcion de guardar repostaje creada
        btnGuardar.setOnClickListener {
            guardarRepostaje()
        }

        //boton para volver al inicio
        btnVolverInicio.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
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
            Toast.makeText(
                this,
                "Por favor, completa todos los campos correctamente",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        //expresion regular para validar la fecha correctamente
        val regexFecha = Regex("""\d{2}/\d{2}/\d{4}""")
        if (!regexFecha.matches(fecha)) {
            Toast.makeText(this, "Formato de fecha inválido (dd/mm/yyyy)", Toast.LENGTH_SHORT)
                .show()
            return
        }

        //recuperamos los km del coche para calcular el consumo con la funcion creada en nuestra bbdd
        val kmAnterior = baseDeDatos.obtenerKmAnterior(idVehiculo)
        //si los km nuevos son menosres que los anteriores salta el mensaje
        if (kmActuales <= kmAnterior) {
            Toast.makeText(
                this,
                "Los kilómetros actuales deben ser mayores que los anteriores ($kmAnterior)",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        //creamos el objeto repostaje con los datos que se han introducido en el formulario
        val repostaje = Repostaje(
            idRepostaje = 0, //igualamos a 0 por haber puesto en la creacion de tablas que el id es autoincremental
            idVehiculo = idVehiculo,
            fecha = fecha,
            kmAnterior = kmAnterior,
            kmActuales = kmActuales,
            litros = litros,
            precioLitro = precioLitro
        )

        //insertamos el repostaje en la base de datos recuperando la funcion creada en bbdd
        val resultado = baseDeDatos.insertarRepostaje(repostaje)

        if (resultado > 0) {
            //actualizamos los km del coche en la tabla vehiulo
            baseDeDatos.actualizarKmVehiculo(idVehiculo, kmActuales)

            //si es correcto mandamos un toast y cerramos la actividad
            Toast.makeText(this, "Repostaje guardado correctamente", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, HistorialRepostajesActivity::class.java)
            intent.putExtra("idVehiculo", idVehiculo)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        } else {
            //si falla mostramos el mensaje de error
            Toast.makeText(this, "Error al guardar el repostaje", Toast.LENGTH_SHORT).show()
        }
    }
}