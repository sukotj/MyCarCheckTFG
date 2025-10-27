package com.example.mycarcheckkotlin

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDeDatos(context: Context) : SQLiteOpenHelper(context, "MyCarCheck.db", null, 1) {

    //obligatorio para ejecutar las claves foraneas de sqlite
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }


    // =======================
    // CREACIÓN DE TABLAS
    // =======================
    override fun onCreate(db: SQLiteDatabase) {
        //tabla usuario
        db.execSQL(
            """
            CREATE TABLE usuarios (
                id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL UNIQUE,
                contrasena TEXT NOT NULL
            )
        """.trimIndent()
        )
        //tabla vehiculos
        db.execSQL(
            """
    CREATE TABLE vehiculos (
        id_vehiculo INTEGER PRIMARY KEY AUTOINCREMENT,
        id_usuario INTEGER NOT NULL,
        matricula TEXT NOT NULL UNIQUE,
        marca TEXT NOT NULL,
        modelo TEXT NOT NULL,
        motor INTEGER,
        tipo_combustible TEXT,
        ano_matriculacion INTEGER,
        fecha_compra DATE,
        km_actuales INTEGER,
        FOREIGN KEY(id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE

    )
""".trimIndent()
        )
        //tabla repostajes
        db.execSQL(
            """
    CREATE TABLE repostajes (
        id_repostaje INTEGER PRIMARY KEY AUTOINCREMENT,
        id_vehiculo INTEGER NOT NULL,
        fecha TEXT NOT NULL,
        km_anterior INTEGER NOT NULL,
        km_actuales INTEGER NOT NULL,
        litros REAL NOT NULL,
        precio_litro REAL NOT NULL,
        FOREIGN KEY(id_vehiculo) REFERENCES vehiculos(id_vehiculo) ON DELETE CASCADE
    )
""".trimIndent()
        )

    }

    // =======================
    // BORRADO DE TABLAS
    // =======================

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //eliminamos y recreamos en caso de que exista
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS vehiculos")
        db.execSQL("DROP TABLE IF EXISTS repostajes")
        onCreate(db)
    }

    //funciones para eliminar vehiculos, usuarios, repostajes

    // =======================
    // CREACIÓN DE REGISTROS
    // =======================

    //funciones para insertar usuarios, vehiculos, repostajes
    fun insertarUsuario(usuario: Usuario): Long {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", usuario.nombre)
            put("contrasena", usuario.contrasena)
        }
        return db.insert("usuarios", null, valores)
    }

    fun insertarVehiculo(vehiculo: Vehiculo): Long {
        val db = writableDatabase

        //comprobamos si la matricula está repetida
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM vehiculos WHERE matricula = ?",
            arrayOf(vehiculo.matricula)
        )
        cursor.moveToFirst()
        val existe = cursor.getInt(0) > 0
        cursor.close()

        if (existe) return -1L

        val valores = ContentValues().apply {
            put("id_usuario", vehiculo.idUsuario)
            put("matricula", vehiculo.matricula)
            put("marca", vehiculo.marca)
            put("modelo", vehiculo.modelo)
            put("motor", vehiculo.motor)
            put("tipo_combustible", vehiculo.tipoCombustible)
            put("ano_matriculacion", vehiculo.anoMatriculacion)
            put("fecha_compra", vehiculo.fechaCompra)
            put("km_actuales", vehiculo.kmActuales)
        }
        return db.insert("vehiculos", null, valores)
    }

    fun insertarRepostaje(repostaje: Repostaje): Long {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("id_vehiculo", repostaje.idVehiculo)
            put("fecha", repostaje.fecha)
            put("km_anterior", repostaje.kmAnterior)
            put("km_actuales", repostaje.kmActuales)
            put("litros", repostaje.litros)
            put("precio_litro", repostaje.precioLitro)
        }
        return db.insert("repostajes", null, valores)
    }

    // =======================
    // ELIMINACIÓN DE REGISTROS
    // =======================

    //funciones para eliminar vehiculos, usuarios, respostajes
    fun eliminarVehiculo(idVehiculo: Int): Int {
        val db = writableDatabase
        return db.delete("vehiculos", "id_vehiculo = ?", arrayOf(idVehiculo.toString()))
    }

    fun eliminarUsuario(idUsuario: Int): Int {
        val db = writableDatabase
        return db.delete("usuarios", "id_usuario = ?", arrayOf(idUsuario.toString()))
    }

    fun eliminarRepostaje(idRepostaje: Int): Int {
        val db = writableDatabase
        return db.delete("repostajes", "id_repostaje = ?", arrayOf(idRepostaje.toString()))
    }

    // =======================
    // EDICIÓN DE REGISTROS
    // =======================

    //funciones para editar vehiculos, repostajes
    fun editarVehiculo(vehiculo: Vehiculo): Int {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("matricula", vehiculo.matricula)
            put("marca", vehiculo.marca)
            put("modelo", vehiculo.modelo)
            put("motor", vehiculo.motor)
            put("tipo_combustible", vehiculo.tipoCombustible)
            put("ano_matriculacion", vehiculo.anoMatriculacion)
            put("fecha_compra", vehiculo.fechaCompra)
            put("km_actuales", vehiculo.kmActuales)
        }
        return db.update(
            "vehiculos",
            valores,
            "id_vehiculo = ?",
            arrayOf(vehiculo.idVehiculo.toString())
        )
    }

    fun editarRepostaje(repostaje: Repostaje): Int {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("fecha", repostaje.fecha)
            put("km_anterior", repostaje.kmAnterior)
            put("km_actuales", repostaje.kmActuales)
            put("litros", repostaje.litros)
            put("precio_litro", repostaje.precioLitro)
        }
        return db.update(
            "repostajes",
            valores,
            "id_repostaje = ?",
            arrayOf(repostaje.idRepostaje.toString())
        )
    }

    // =======================
    // CONSULTAS Y BÚSQUEDAS
    // =======================
    //funcion para obtener el usuario
    fun buscarUsuario(nombre: String, contrasena: String? = null): Usuario? {
        val db = readableDatabase
        val query: String
        val args: Array<String>

        if (contrasena != null) {
            //busqueda con nombre y contraseña para la pantalla login
            query = "SELECT * FROM usuarios WHERE nombre = ? AND contrasena = ?"
            args = arrayOf(nombre, contrasena)
        } else {
            //busqueda por nombre para la pantalla registro
            query = "SELECT * FROM usuarios WHERE nombre = ?"
            args = arrayOf(nombre)
        }

        val cursor = db.rawQuery(query, args)
        return if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario"))
            val nombreUsuario = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            val contrasenaUsuario = cursor.getString(cursor.getColumnIndexOrThrow("contrasena"))
            cursor.close()
            Usuario(id, nombreUsuario, contrasenaUsuario)
        } else {
            cursor.close()
            null
        }

    }

    fun getVehiculosPorUsuario(idUsuario: Int): List<Vehiculo> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM vehiculos WHERE id_usuario = ?",
            arrayOf(idUsuario.toString())
        )

        val lista = mutableListOf<Vehiculo>()
        while (cursor.moveToNext()) {
            val idVehiculo = cursor.getInt(cursor.getColumnIndexOrThrow("id_vehiculo"))
            val matricula = cursor.getString(cursor.getColumnIndexOrThrow("matricula"))
            val marca = cursor.getString(cursor.getColumnIndexOrThrow("marca"))
            val modelo = cursor.getString(cursor.getColumnIndexOrThrow("modelo"))
            val motor = cursor.getInt(cursor.getColumnIndexOrThrow("motor"))
            val tipoCombustible = cursor.getString(cursor.getColumnIndexOrThrow("tipo_combustible"))
            val anoMatriculacion = cursor.getInt(cursor.getColumnIndexOrThrow("ano_matriculacion"))
            val fechaCompra = cursor.getString(cursor.getColumnIndexOrThrow("fecha_compra"))
            val kmActuales = cursor.getInt(cursor.getColumnIndexOrThrow("km_actuales"))

            lista.add(
                Vehiculo(
                    idVehiculo,
                    matricula,
                    marca,
                    modelo,
                    motor,
                    tipoCombustible,
                    anoMatriculacion,
                    fechaCompra,
                    kmActuales,
                    idUsuario
                )
            )
        }
        cursor.close()
        return lista
    }

    fun getRepostajesPorVehiculo(idVehiculo: Int): List<Repostaje> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM repostajes WHERE id_vehiculo = ? ORDER BY fecha DESC",
            arrayOf(idVehiculo.toString())
        )

        val lista = mutableListOf<Repostaje>()
        while (cursor.moveToNext()) {
            val idRepostaje = cursor.getInt(cursor.getColumnIndexOrThrow("id_repostaje"))
            val fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"))
            val kmAnterior = cursor.getInt(cursor.getColumnIndexOrThrow("km_anterior"))
            val kmActuales = cursor.getInt(cursor.getColumnIndexOrThrow("km_actuales"))
            val litros = cursor.getDouble(cursor.getColumnIndexOrThrow("litros"))
            val precioLitro = cursor.getDouble(cursor.getColumnIndexOrThrow("precio_litro"))

            lista.add(
                Repostaje(
                    idRepostaje,
                    idVehiculo,
                    fecha,
                    kmAnterior,
                    kmActuales,
                    litros,
                    precioLitro
                )
            )
        }
        cursor.close()
        return lista
    }

    fun getUltimoRepostaje(idVehiculo: Int): Repostaje? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM repostajes WHERE id_vehiculo = ? ORDER BY id_repostaje DESC LIMIT 1",
            arrayOf(idVehiculo.toString())
        )

        return if (cursor.moveToFirst()) {
            val fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"))
            val kmAnterior = cursor.getInt(cursor.getColumnIndexOrThrow("km_anterior"))
            val kmActuales = cursor.getInt(cursor.getColumnIndexOrThrow("km_actuales"))
            val litros = cursor.getDouble(cursor.getColumnIndexOrThrow("litros"))
            val precioLitro = cursor.getDouble(cursor.getColumnIndexOrThrow("precio_litro"))
            val idRepostaje = cursor.getInt(cursor.getColumnIndexOrThrow("id_repostaje"))

            cursor.close()
            Repostaje(idRepostaje, idVehiculo, fecha, kmAnterior, kmActuales, litros, precioLitro)
        } else {
            cursor.close()
            null
        }
    }

    fun obtenerKmAnterior(idVehiculo: Int): Int {
        return getUltimoRepostaje(idVehiculo)?.kmActuales ?: 0
    }

    fun actualizarKmVehiculo(idVehiculo: Int, nuevosKm: Int): Int {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("km_actuales", nuevosKm)
        }
        return db.update("vehiculos", valores, "id_vehiculo = ?", arrayOf(idVehiculo.toString()))
    }

    fun calcularConsumoMedio(idVehiculo: Int): Double {
        val lista = getRepostajesPorVehiculo(idVehiculo)
        if (lista.size < 2) return 0.0

        val totalKm = lista.sumOf { it.kmActuales - it.kmAnterior }
        val totalLitros = lista.sumOf { it.litros }

        return if (totalLitros > 0) totalKm / totalLitros else 0.0
    }

}