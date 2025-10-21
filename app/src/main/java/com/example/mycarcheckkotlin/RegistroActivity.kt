package com.example.mycarcheckkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegistroActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var db: BaseDeDatos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Inicializamos los elementos visuales
        etNombre = findViewById(R.id.etNombre)
        etContrasena = findViewById(R.id.etContrasena)
        btnRegistrar = findViewById(R.id.btnRegistrar)
        db = BaseDeDatos(this)

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            // Validación básica
            if (nombre.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Comprobamos si el usuario ya existe
            val usuarioExistente = db.getUsuarioPorNombre(nombre)
            if (usuarioExistente != null) {
                Toast.makeText(this, "Ese nombre ya está registrado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Creamos el nuevo usuario
            val nuevoUsuario = Usuario(0, nombre, contrasena)
            val id = db.insertarUsuario(nuevoUsuario)

            // Si se insertó correctamente, guardamos el ID y pasamos a MainActivity
            if (id != -1L) {
                val prefs = getSharedPreferences("MyCarCheckPrefs", MODE_PRIVATE)
                prefs.edit().putInt("id_usuario", id.toInt()).apply()

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

