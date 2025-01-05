package uv.tc.timefastmobile.poko

data class Colaborador(
    val idColaborador: Int,
    val idPersona: Int,
    val noPersonal: String,
    val persona: Persona,
    val contrasena: String,
    val rol: RolColaborador,
)
