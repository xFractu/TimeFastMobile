package uv.tc.timefastmobile.poko

data class EstadoEnvio(
    val idEstadoEnvio: Int,
    val idEnvio: Int,
    val envio: Envio,
    val fecha: String,
    val descripcion: String
)
