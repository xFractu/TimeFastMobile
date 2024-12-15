package uv.tc.timefastmobile.poko

data class RolColaborador(
    val id: Int,
    val rol: String,
    val numLicencia: String? = null,
    val idColaborador: Int? = null
)
