package uv.tc.timefastmobile.poko

data class LoginColaborador(
    val error: Boolean,
    val mensaje: String,
    var colaborador: Colaborador ?
)
