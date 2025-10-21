enum class TipoRegistro {
    REVISION,
    ITV,
    IMPUESTO_CIRCULACION
}

data class RegistroVehiculo(
    val idRegistro: Int,
    val idVehiculo: Int,
    val tipo: TipoRegistro,
    val fecha: String,
    val kmRealizados: Int,
    val coste: Double,
    val proximaRevision: String? = null
)
