package com.example.mycarcheckkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnIrRegistro: Button
    private lateinit var db: BaseDeDatos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializamos los elementos visuales
        etNombre = findViewById(R.id.etNombre)
        etContrasena = findViewById(R.id.etContrasena)
        btnLogin = findViewById(R.id.btnLogin)
        btnIrRegistro = findViewById(R.id.btnIrRegistro)
        db = BaseDeDatos(this)

        // Botón para iniciar sesión
        btnLogin.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            // Validación básica
            if (nombre.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Consultamos si el usuario existe
            val usuario = db.getUsuario(nombre, contrasena)

            if (usuario != null) {
                // Guardamos el ID en SharedPreferences
                val prefs = getSharedPreferences("MyCarCheckPrefs", MODE_PRIVATE)
                prefs.edit()
                    .putInt("id_usuario", usuario.idUsuario)
                    .putString("nombre_usuario", usuario.nombre)
                    .apply()


                // Accedemos a la pantalla principal
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón para ir al registro
        btnIrRegistro.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }
}

