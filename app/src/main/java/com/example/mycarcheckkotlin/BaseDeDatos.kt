package com.example.mycarcheckkotlin

import RegistroVehiculo
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

    override fun onCreate(db: SQLiteDatabase) {
        //tabla usuario
        db.execSQL("""
            CREATE TABLE usuarios (
                id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL UNIQUE,
                contrasena TEXT NOT NULL
            )
        """.trimIndent())
        //tabla vehiculos
        db.execSQL("""
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
""".trimIndent())
        //tabla repostajes
        db.execSQL("""
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
""".trimIndent())
        //tabla registroVehiculo
        db.execSQL("""
    CREATE TABLE registro_vehiculo (
        id_registro INTEGER PRIMARY KEY AUTOINCREMENT,
        id_vehiculo INTEGER NOT NULL,
        tipo TEXT NOT NULL,
        fecha TEXT NOT NULL,
        km_realizados INTEGER,
        coste REAL NOT NULL,
        proxima_revision TEXT,
        FOREIGN KEY(id_vehiculo) REFERENCES vehiculos(id_vehiculo) ON DELETE CASCADE
    )
""".trimIndent())

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Por ahora, simplemente eliminamos y recreamos
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS vehiculos")
        db.execSQL("DROP TABLE IF EXISTS repostajes")
        db.execSQL("DROP TABLE IF EXISTS registro_vehiculo")
        onCreate(db)
    }

    fun eliminarVehiculo(idVehiculo: Int): Int {
        val db = writableDatabase
        return db.delete("vehiculos", "id_vehiculo = ?", arrayOf(idVehiculo.toString()))
    }

    fun eliminarUsuario(idUsuario: Int): Int {
        val db = writableDatabase
        return db.delete("usuarios", "id_usuario = ?", arrayOf(idUsuario.toString()))
    }


    fun insertarUsuario(usuario: Usuario): Long{
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", usuario.nombre)
            put("contrasena",usuario.contrasena)
        }
        return db.insert("usuarios",null,valores)
    }
    fun getUsuario(nombre: String, contrasena: String): Usuario? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM usuarios WHERE nombre = ? AND contrasena = ?",
            arrayOf(nombre, contrasena)
        )

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
    fun getUsuarioPorNombre(nombre: String): Usuario? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM usuarios WHERE nombre = ?",
            arrayOf(nombre)
        )

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
    fun insertarVehiculo(vehiculo: Vehiculo): Long {
        val db = writableDatabase
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

    fun eliminarRepostaje(idRepostaje: Int): Int {
        val db = writableDatabase
        return db.delete("repostajes", "id_repostaje = ?", arrayOf(idRepostaje.toString()))
    }

    fun insertarRegistroVehiculo(registro: RegistroVehiculo): Long {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("id_vehiculo", registro.idVehiculo)
            put("tipo", registro.tipo.name)
            put("fecha", registro.fecha)
            put("km_realizados", registro.kmRealizados)
            put("coste", registro.coste)
            put("proxima_revision", registro.proximaRevision)
        }
        return db.insert("registro_vehiculo", null, valores)
    }
    fun getRegistrosPorVehiculo(idVehiculo: Int): List<RegistroVehiculo> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM registro_vehiculo WHERE id_vehiculo = ? ORDER BY fecha DESC",
            arrayOf(idVehiculo.toString())
        )

        val lista = mutableListOf<RegistroVehiculo>()
        while (cursor.moveToNext()) {
            val idRegistro = cursor.getInt(cursor.getColumnIndexOrThrow("id_registro"))
            val tipoStr = cursor.getString(cursor.getColumnIndexOrThrow("tipo"))
            val fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"))
            val kmRealizados = cursor.getInt(cursor.getColumnIndexOrThrow("km_realizados"))
            val coste = cursor.getDouble(cursor.getColumnIndexOrThrow("coste"))
            val proximaRevision = cursor.getString(cursor.getColumnIndexOrThrow("proxima_revision"))

            val tipo = TipoRegistro.valueOf(tipoStr)

            lista.add(
                RegistroVehiculo(
                    idRegistro,
                    idVehiculo,
                    tipo,
                    fecha,
                    kmRealizados,
                    coste,
                    proximaRevision
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






}

