package com.example.mycarcheckkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager

class EditarVehiculoActivity : AppCompatActivity() {

    //elementos visuales del xml
    private lateinit var etMatricula: EditText
    private lateinit var etMarca: EditText
    private lateinit var etModelo: EditText
    private lateinit var etMotor: EditText
    private lateinit var etTipoCombustible: EditText
    private lateinit var etAnoMatriculacion: EditText
    private lateinit var etFechaCompra: EditText
    private lateinit var etKm: EditText
    private lateinit var btnGuardarCambios: Button
    private lateinit var baseDeDatos: BaseDeDatos

    //identificador del coche a editar
    private var idVehiculo: Int = -1

    //identificador del usuario propietario del coceh
    private var idUsuario: Int = -1

    //variable para obtener el ultimo repostaje
    private var kmUltimoRepostaje: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_vehiculo)

        //inicializamos los datos del xml
        etMatricula = findViewById(R.id.etMatricula)
        etMarca = findViewById(R.id.etMarca)
        etModelo = findViewById(R.id.etModelo)
        etMotor = findViewById(R.id.etMotor)
        etTipoCombustible = findViewById(R.id.etTipoCombustible)
        etAnoMatriculacion = findViewById(R.id.etAnoMatriculacion)
        etFechaCompra = findViewById(R.id.etFechaCompra)
        etKm = findViewById(R.id.etKm)
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios)
        baseDeDatos = BaseDeDatos(this)

        //validamos que el id sea valido
        idVehiculo = intent.getIntExtra("idVehiculo", -1)
        if (idVehiculo == -1) {
            Toast.makeText(this, "Vehículo no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        //obtenemos el coche desde la bbdd con su id
        val vehiculo = baseDeDatos.getVehiculoPorId(idVehiculo)
        //si existe precargamos los datos en el formulario
        if (vehiculo != null) {
            etMatricula.setText(vehiculo.matricula)
            etMarca.setText(vehiculo.marca)
            etModelo.setText(vehiculo.modelo)
            etMotor.setText(vehiculo.motor.toString())
            etTipoCombustible.setText(vehiculo.tipoCombustible)
            etAnoMatriculacion.setText(vehiculo.anoMatriculacion.toString())
            etFechaCompra.setText(vehiculo.fechaCompra)
            etKm.setText(vehiculo.kmActuales.toString())
            idUsuario = vehiculo.idUsuario

            val ultimoRepostaje = baseDeDatos.getUltimoRepostaje(idVehiculo)
            kmUltimoRepostaje = 0
            if (ultimoRepostaje != null) {
                kmUltimoRepostaje = ultimoRepostaje.kmActuales
            }
        }

        btnGuardarCambios.setOnClickListener {
            //recuperamos y guardamos los datos metidos por el usuario
            val matricula = etMatricula.text.toString()
            val marca = etMarca.text.toString()
            val modelo = etModelo.text.toString()
            val motor = etMotor.text.toString().toIntOrNull()
            val tipoCombustible = etTipoCombustible.text.toString()
            val anoMatriculacion = etAnoMatriculacion.text.toString().toIntOrNull()
            val fechaCompra = etFechaCompra.text.toString()
            val kmActualesStr = etKm.text.toString().trim()
            val kmActuales = kmActualesStr.toIntOrNull()

            if (kmActuales == null || kmActuales < 0) {
                Toast.makeText(this, "Kilómetros inválidos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //comprobamos que los km actuales son mayores que los nuevos
            if (kmActuales < kmUltimoRepostaje) {
                Toast.makeText(
                    this,
                    "No puedes introducir un valor menor al último repostaje: $kmUltimoRepostaje km",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            //comprobamos que los datos son correctos
            if (matricula.isBlank() || marca.isBlank() || modelo.isBlank() ||
                motor == null || motor <= 0 ||
                tipoCombustible.isBlank() ||
                anoMatriculacion == null || anoMatriculacion <= 1900 ||
                fechaCompra.isBlank()
            ) {
                Toast.makeText(this, "Completa todos los campos correctamente", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }


            //creamos el vehiculo con los datos editados
            val vehiculoEditado = Vehiculo(
                idVehiculo,
                matricula,
                marca,
                modelo,
                motor,
                tipoCombustible,
                anoMatriculacion,
                fechaCompra,
                kmActuales = kmActuales,
                idUsuario
            )

            //y actualizamos el coche en la base de datos
            val resultado = baseDeDatos.editarVehiculo(vehiculoEditado)

            if (resultado > 0) {
                Toast.makeText(this, "Vehículo actualizado", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ListaVehiculosActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            ocultarTeclado()
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun ocultarTeclado() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
    }

}
