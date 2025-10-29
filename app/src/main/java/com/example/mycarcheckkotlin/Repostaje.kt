package com.example.mycarcheckkotlin

data class Repostaje(
    val idRepostaje: Int,
    val idVehiculo: Int,
    val fecha: String,
    val kmAnterior: Int,
    val kmActuales: Int,
    val litros: Double,
    val precioLitro: Double
) {
    val precioTotal: Double
        get() = litros * precioLitro
    val consumoMedio: Double
        get() = if (kmActuales > kmAnterior) {
            (litros / (kmActuales - kmAnterior)) * 100
        } else {
            0.0
        }
}
