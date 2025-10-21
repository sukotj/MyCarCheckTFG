package com.example.mycarcheckkotlin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistorialRepostajesActivity : AppCompatActivity() {

    private lateinit var rvRepostajes: RecyclerView
    private lateinit var db: BaseDeDatos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_repostajes)

        rvRepostajes = findViewById(R.id.rvRepostajes)
        db = BaseDeDatos(this)

        val idVehiculo = intent.getIntExtra("id_vehiculo", -1)
        if (idVehiculo == -1) {
            Toast.makeText(this, "Vehículo no identificado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val lista = db.getRepostajesPorVehiculo(idVehiculo)
        rvRepostajes.layoutManager = LinearLayoutManager(this)
        rvRepostajes.adapter = RepostajeAdapter(lista)
    }
}
