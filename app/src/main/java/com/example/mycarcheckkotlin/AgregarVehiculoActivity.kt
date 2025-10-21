import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mycarcheckkotlin.BaseDeDatos
import com.example.mycarcheckkotlin.R
import com.example.mycarcheckkotlin.Vehiculo

class AgregarVehiculoActivity : AppCompatActivity() {

    private lateinit var etMarca: EditText
    private lateinit var etModelo: EditText
    private lateinit var etMatricula: EditText
    private lateinit var etAnio: EditText
    private lateinit var btnGuardarVehiculo: Button
    private lateinit var db: BaseDeDatos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_vehiculo)

        etMarca = findViewById(R.id.etMarca)
        etModelo = findViewById(R.id.etModelo)
        etMatricula = findViewById(R.id.etMatricula)
        etAnio = findViewById(R.id.etAnio)
        btnGuardarVehiculo = findViewById(R.id.btnGuardarVehiculo)
        db = BaseDeDatos(this)

        val idUsuario = obtenerIdUsuario()
        if (idUsuario == -1) {
            Toast.makeText(this, "Usuario no identificado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        btnGuardarVehiculo.setOnClickListener {
            val marca = etMarca.text.toString().trim()
            val modelo = etModelo.text.toString().trim()
            val matricula = etMatricula.text.toString().trim()
            val anioStr = etAnio.text.toString().trim()

            if (marca.isEmpty() || modelo.isEmpty() || matricula.isEmpty() || anioStr.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val anio = anioStr.toIntOrNull()
            if (anio == null || anio < 1900 || anio > 2100) {
                Toast.makeText(this, "Año inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val vehiculo = Vehiculo(
                idVehiculo = 0,
                matricula = matricula,
                marca = marca,
                modelo = modelo,
                motor = 0,
                tipoCombustible = "",
                anoMatriculacion = anio,
                fechaCompra = "",
                kmActuales = 0,
                idUsuario = idUsuario
            )

            val id = db.insertarVehiculo(vehiculo)
            if (id != -1L) {
                Toast.makeText(this, "Vehículo guardado", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun obtenerIdUsuario(): Int {
        val prefs = getSharedPreferences("MyCarCheckPrefs", MODE_PRIVATE)
        return prefs.getInt("id_usuario", -1)
    }
}
