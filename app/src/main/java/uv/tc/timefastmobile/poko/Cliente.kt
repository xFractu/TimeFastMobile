package uv.tc.timefastmobile.poko

data class Cliente(
    val id: Int,
    val idPersona: Int,
    val idDireccion: Int,
    val telefono: String,
    val persona: Persona,
    val direccion: Direccion
)

