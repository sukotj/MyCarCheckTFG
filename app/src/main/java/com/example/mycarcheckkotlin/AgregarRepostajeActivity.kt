import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mycarcheckkotlin.BaseDeDatos
import com.example.mycarcheckkotlin.R
import com.example.mycarcheckkotlin.Repostaje

class AgregarRepostajeActivity : AppCompatActivity() {

    private lateinit var etLitros: EditText
    private lateinit var etPrecioLitro: EditText
    private lateinit var etKmActuales: EditText
    private lateinit var etFecha: EditText
    private lateinit var btnGuardar: Button
    private lateinit var db: BaseDeDatos

    private var idVehiculo: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_repostaje)

        etLitros = findViewById(R.id.etLitros)
        etPrecioLitro = findViewById(R.id.etPrecioLitro) // ← asegúrate de que el XML lo tenga así
        etKmActuales = findViewById(R.id.etKmActuales)
        etFecha = findViewById(R.id.etFecha)
        btnGuardar = findViewById(R.id.btnGuardarRepostaje)
        db = BaseDeDatos(this)

        idVehiculo = intent.getIntExtra("id_vehiculo", -1)
        if (idVehiculo == -1) {
            Toast.makeText(this, "Vehículo no identificado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        btnGuardar.setOnClickListener {
            guardarRepostaje()
        }
    }

    private fun guardarRepostaje() {
        val litrosStr = etLitros.text.toString().trim()
        val precioStr = etPrecioLitro.text.toString().trim()
        val kmActStr = etKmActuales.text.toString().trim()
        val fecha = etFecha.text.toString().trim()

        if (litrosStr.isEmpty() || precioStr.isEmpty() || kmActStr.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val litros = litrosStr.toDoubleOrNull()
        val precioLitro = precioStr.toDoubleOrNull()
        val kmActuales = kmActStr.toIntOrNull()

        if (litros == null || precioLitro == null || kmActuales == null) {
            Toast.makeText(this, "Datos numéricos inválidos", Toast.LENGTH_SHORT).show()
            return
        }

        // Recuperamos el kmAnterior automáticamente
        val ultimoRepostaje = db.getUltimoRepostaje(idVehiculo)
        val kmAnterior = ultimoRepostaje?.kmActuales ?: 0

        val repostaje = Repostaje(
            idRepostaje = 0,
            idVehiculo = idVehiculo,
            fecha = fecha,
            kmAnterior = kmAnterior,
            kmActuales = kmActuales,
            litros = litros,
            precioLitro = precioLitro
        )

        val id = db.insertarRepostaje(repostaje)
        if (id != -1L) {
            Toast.makeText(this, "Repostaje guardado", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
        }
    }
}
