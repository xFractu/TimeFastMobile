package uv.tc.timefastmobile.poko

data class Persona(
    val idPersona: Int,
    val nombre: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String,
    val correo: String? = null,
    val CURP: String? = null,
    val fotoBase64: String? = null
)
