package com.example.mycarcheckkotlin

data class Vehiculo(
    val idVehiculo: Int,
    val matricula: String,
    val marca: String,
    val modelo: String,
    val motor: Int,
    val tipoCombustible: String,
    val anoMatriculacion: Int,
    val fechaCompra: String,
    val kmActuales: Int,
    val idUsuario: Int
)