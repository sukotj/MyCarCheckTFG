package com.example.mycarcheckkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class RegistroActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var btnVolverLogin: Button
    private lateinit var db: BaseDeDatos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        etNombre = findViewById(R.id.etNombre)
        etContrasena = findViewById(R.id.etContrasena)
        btnRegistrar = findViewById(R.id.btnRegistrar)
        btnVolverLogin = findViewById(R.id.btnVolverLogin)
        db = BaseDeDatos(this)

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            if (nombre.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val usuarioExistente = db.getUsuarioPorNombre(nombre)
            if (usuarioExistente != null) {
                Toast.makeText(this, "El nombre de usuario ya existe", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nuevoUsuario = Usuario(0, nombre, contrasena)
            val resultado = db.insertarUsuario(nuevoUsuario)

            if (resultado > 0) {
                Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show()
            }
        }

        btnVolverLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
