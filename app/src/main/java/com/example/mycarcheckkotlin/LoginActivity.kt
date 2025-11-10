package com.example.mycarcheckkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager

class LoginActivity : AppCompatActivity() {

    //elementos visuales del xml
    private lateinit var etNombre: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnIrRegistro: Button
    private lateinit var db: BaseDeDatos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //inicializamos los datos del xml
        etNombre = findViewById(R.id.etNombre)
        etContrasena = findViewById(R.id.etContrasena)
        btnLogin = findViewById(R.id.btnLogin)
        btnIrRegistro = findViewById(R.id.btnIrRegistro)
        db = BaseDeDatos(this)

        //comprobamos si hay sesion iniciada con el usuario de shared preferences
        val prefs = getSharedPreferences("MyCarCheckPrefs", MODE_PRIVATE)
        val idUsuario = prefs.getInt("id_usuario", -1)
        val desdeInicio = intent.getBooleanExtra("desdeInicio", false)
        if (!desdeInicio && idUsuario != -1) {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        //accion del boton de login
        btnLogin.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            if (nombre.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val usuario = db.buscarUsuario(nombre, contrasena)

            if (usuario != null) {
                prefs.edit()
                    .putInt("id_usuario", usuario.idUsuario)
                    .putString("nombre_usuario", usuario.nombre)
                    .apply()

                val intent = Intent(this, MenuPrincipalActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

        //boton para ir a la pantalla de registro
        btnIrRegistro.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }

    //ocultar el teclado al tocar la pantalla
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
