package com.example.mycarcheckkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MenuPrincipalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)

        // ============================
        // NAVEGACIÓN ENTRE ACTIVIDADES
        // ============================

        findViewById<Button>(R.id.btnAgregarVehiculo).setOnClickListener {
            startActivity(Intent(this, AgregarVehiculoActivity::class.java))
        }

        findViewById<Button>(R.id.btnVerVehiculos).setOnClickListener {
            startActivity(Intent(this, ListaVehiculosActivity::class.java))
        }
/*
        findViewById<Button>(R.id.btnVerRepostajes).setOnClickListener {
            startActivity(Intent(this, ListaRepostajesActivity::class.java))
        }
*/
        findViewById<Button>(R.id.btnEstadisticas).setOnClickListener {
            startActivity(Intent(this, EstadisticasActivity::class.java))
        }

        // ============================
        // GESTIÓN DE SESIÓN
        // ============================

        //cerrar sesion y volver al login
        findViewById<Button>(R.id.btnCerrarSesion).setOnClickListener {
            val prefs = getSharedPreferences("MyCarCheckPrefs", MODE_PRIVATE)
            prefs.edit().clear().apply() //borra toda la sesión

            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("desdeInicio", true) //evita redireccion automatica
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // ============================
        // ELIMINACIÓN DE CUENTA
        // ============================
        //eliminar el usiario actual mediante la id
        findViewById<Button>(R.id.btnEliminarCuenta).setOnClickListener {
            val prefs = getSharedPreferences("MyCarCheckPrefs", MODE_PRIVATE)
            val idUsuario = prefs.getInt("id_usuario", -1)

            if (idUsuario != -1) {
                val resultado = BaseDeDatos(this).eliminarUsuario(idUsuario)

                if (resultado > 0) {
                    Toast.makeText(this, "Cuenta eliminada correctamente", Toast.LENGTH_SHORT)
                        .show()
                    prefs.edit().clear().apply() //limpiamos la sesion
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Usuario no válido", Toast.LENGTH_SHORT).show()
            }
        }
    }
}