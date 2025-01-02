package uv.tc.timefastmobile.poko

import java.io.Serializable

data class Envio(
    val idEnvio: Int,
    val idOrigen: Int,
    val idDestino: Int,
    val origen: Direccion,
    val destino: Direccion,
    val cliente: Cliente,
    val conductor: Colaborador,
    val costo: Double,
    val fecha: String,
    val numGuia: String,
    var paquetes: List<Paquete>,
    var estatus: String
) : Serializable