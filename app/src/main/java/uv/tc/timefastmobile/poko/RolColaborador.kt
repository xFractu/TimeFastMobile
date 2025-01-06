package uv.tc.timefastmobile.poko

data class RolColaborador(
    val idRolColaborador: Int,  // Cambié el nombre del campo
    val rol: String,
    val numLicencia: String? = null,
    val idColaborador: Int? = null
)
