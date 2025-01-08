package uv.tc.timefastmobile

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.koushikdutta.ion.Ion
import uv.tc.timefastmobile.databinding.ActivityEditarPerfilBinding
import uv.tc.timefastmobile.poko.Colaborador
import uv.tc.timefastmobile.poko.Persona
import uv.tc.timefastmobile.poko.RolColaborador
import uv.tc.timefastmobile.util.Constantes
import java.nio.charset.Charset
import kotlin.reflect.KMutableProperty0

class EditarPerfilActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditarPerfilBinding
    private lateinit var colaborador: Colaborador

    // Propiedades para manejar la visibilidad de las contraseñas
    private var passwordActualVisible: Boolean = false
    private var passwordNuevaVisible: Boolean = false
    private var passwordConfirmarVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)

        val colaboradorJSON = intent.getStringExtra("colaborador")
        if (colaboradorJSON != null) {
            val gson = Gson()
            colaborador = gson.fromJson(colaboradorJSON, Colaborador::class.java)
            // log para depuración
            Log.d("ColaboradorData", "Colaborador: $colaborador")
            Log.d("ColaboradorData", "No. Personal: ${colaborador.noPersonal}")
            Log.d("ColaboradorData", "Nombre: ${colaborador.persona?.nombre}")
            Log.d("ColaboradorData", "Apellido Paterno: ${colaborador.persona?.apellidoPaterno}")
            Log.d("ColaboradorData", "Apellido Materno: ${colaborador.persona?.apellidoMaterno}")
            Log.d("ColaboradorData", "Rol: ${colaborador.rol?.rol}")
            Log.d("ColaboradorData", "Rol ID: ${colaborador.rol?.idRolColaborador}")
            Log.d("ColaboradorData", "ID Persona: ${colaborador.persona?.idPersona}")
            //mostrarDatosColaborador(colaborador)
        } else {
            //Toast.makeText(this, "No se recibieron datos del colaborador", Toast.LENGTH_LONG).show()
        }

        binding.etNombre.setText("${colaborador.persona.nombre}")
        binding.etApellidoPaterno.setText("${colaborador.persona.apellidoPaterno}")
        binding.etApellidoMaterno.setText("${colaborador.persona.apellidoMaterno}")
        binding.etCurp.setText("${colaborador.persona.CURP}")
        binding.etCorreo.setText("${colaborador.persona.correo}")
        binding.etNoLicencia.setText("${colaborador.rol.numLicencia}")
        binding.etPasswordActual.setText("${colaborador.contrasena}")

        // Configuración de visibilidad de contraseñas
        setupPasswordVisibilityToggle(binding.etPasswordActual, ::passwordActualVisible)
        setupPasswordVisibilityToggle(binding.etPasswordNueva, ::passwordNuevaVisible)
        setupPasswordVisibilityToggle(binding.etPasswordConfirmar, ::passwordConfirmarVisible)

        binding.btnInicio.setOnClickListener {
            irPantallaInicio(colaborador)
        }

        binding.btnPerfil.setOnClickListener {
            irPantallaPerfil(colaborador)
        }

        binding.btnEditarPerfil.setOnClickListener {
            // Crear una nueva instancia de Persona
            val persona = Persona(
                idPersona = colaborador.persona.idPersona,
                nombre = binding.etNombre.text.toString(),
                apellidoPaterno = binding.etApellidoPaterno.text.toString(),
                apellidoMaterno = binding.etApellidoMaterno.text.toString(),
                correo = binding.etCorreo.text.toString(),
                CURP = binding.etCurp.text.toString()
            )

            // Crear una nueva instancia de RolColaborador
            val rol = RolColaborador(
                idRolColaborador = colaborador.rol.idRolColaborador,
                rol = colaborador.rol.rol,
                numLicencia = binding.etNoLicencia.text.toString(),
                idColaborador = colaborador.idColaborador
            )

            // Validar si hay una nueva contraseña y si es válida
            val nuevaContrasena = binding.etPasswordNueva.text.toString()
            val confirmarContrasena = binding.etPasswordConfirmar.text.toString()
            val contrasenaFinal = if (nuevaContrasena.isNotEmpty() && nuevaContrasena == confirmarContrasena) {
                nuevaContrasena
            } else {
                colaborador.contrasena // Mantener la contraseña actual si no hay cambios
            }

            // Validar campos vacíos
            if (validarCampos()) {
                // Crear el objeto Colaborador actualizado
                val colaboradorActualizado = Colaborador(
                    idColaborador = colaborador.idColaborador,
                    idPersona = colaborador.persona.idPersona,
                    noPersonal = colaborador.noPersonal,
                    persona = persona,
                    contrasena = contrasenaFinal,
                    rol = rol
                )

                // Llamar al metodo para enviar los datos al servidor
                enviarDatosEdicion(colaboradorActualizado)
                Toast.makeText(this@EditarPerfilActivity, "Cambios guardados", Toast.LENGTH_LONG).show()
            }
        }


    }

    private fun enviarDatosEdicion(colaborador: Colaborador) {
        val gson = Gson()
        val parametros = gson.toJson(colaborador)

        Ion.with(this@EditarPerfilActivity)
            .load("PUT", "${Constantes().URL_WS}/colaborador/editar")
            .setHeader("Content-Type", "application/json")
            .setStringBody(parametros)
            .asString(Charset.forName("UTF-8"))
            .setCallback { e, result ->
                if (e == null) {
                    // Procesar respuesta del servidor
                    Log.d("Respuesta del Servidor", result)  // Log para depurar la respuesta del servidor
                    Toast.makeText(this@EditarPerfilActivity, "Colaborador actualizado con éxito", Toast.LENGTH_LONG).show()
                    irPantallaPerfilF(colaborador)
                } else {
                    // Manejar errores
                    Log.e("Error de Servidor", e.message ?: "Error desconocido")
                    Toast.makeText(this@EditarPerfilActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
    }



    private fun validarCampos(): Boolean {
        val emailRegex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")

        return when {
            binding.etNombre.text.isNullOrEmpty() -> {
                binding.etNombre.error = "El nombre no puede estar vacío"
                false
            }
            binding.etApellidoPaterno.text.isNullOrEmpty() -> {
                binding.etApellidoPaterno.error = "El apellido paterno no puede estar vacío"
                false
            }
            binding.etApellidoMaterno.text.isNullOrEmpty() -> {
                binding.etApellidoMaterno.error = "El apellido materno no puede estar vacío"
                false
            }
            binding.etCurp.text.isNullOrEmpty() -> {
                binding.etCurp.error = "El CURP no puede estar vacío"
                false
            }
            binding.etCorreo.text.isNullOrEmpty() -> {
                binding.etCorreo.error = "El correo no puede estar vacío"
                false
            }
            !emailRegex.matches(binding.etCorreo.text.toString()) -> {
                binding.etCorreo.error = "El correo no es válido"
                false
            }
            binding.etNoLicencia.text.isNullOrEmpty() -> {
                binding.etNoLicencia.error = "El número de licencia no puede estar vacío"
                false
            }
            binding.etPasswordNueva.text.isNotEmpty() && binding.etPasswordConfirmar.text.toString() != binding.etPasswordNueva.text.toString() -> {
                binding.etPasswordConfirmar.error = "Las contraseñas no coinciden"
                false
            }
            else -> true
        }
    }


    private fun irPantallaPerfilF(colaborador: Colaborador) {
        val gson = Gson()
        val colaboradorJson = gson.toJson(colaborador)
        val intent = Intent(this@EditarPerfilActivity, InicioActivity::class.java)
        intent.putExtra("colaborador", colaboradorJson)
        intent.putExtra("inicio", "Perfil")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }


    private fun irPantallaPerfil(colaborador: Colaborador) {
        val gson = Gson()
        val colaboradorJson = gson.toJson(colaborador)
        val intent = Intent(this@EditarPerfilActivity, InicioActivity::class.java)
        intent.putExtra("colaborador", colaboradorJson)
        intent.putExtra("inicio", "Perfil")
        startActivity(intent)
    }

    private fun irPantallaInicio(colaborador: Colaborador) {
        val gson = Gson()
        val colaboradorJson = gson.toJson(colaborador)
        val intent = Intent(this@EditarPerfilActivity, InicioActivity::class.java)
        intent.putExtra("colaborador", colaboradorJson)
        intent.putExtra("inicio", "Inicio")
        startActivity(intent)
    }



    private fun mostrarDatosColaborador(colaborador: Colaborador) {
        val mensaje = """
            Colaborador Autenticado:
            No. Personal: ${colaborador.noPersonal}
            Nombre: ${colaborador.persona.nombre} ${colaborador.persona.apellidoPaterno} ${colaborador.persona.apellidoMaterno}
            Rol: ${colaborador.rol.rol}
        """.trimIndent()
        Toast.makeText(this@EditarPerfilActivity, mensaje, Toast.LENGTH_LONG).show()
    }

    private fun setupPasswordVisibilityToggle(editText: EditText, visibilityState: KMutableProperty0<Boolean>) {
        editText.setOnTouchListener { _, event ->
            val DRAWABLE_END = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (editText.right - editText.compoundDrawables[DRAWABLE_END].bounds.width())) {
                    visibilityState.set(!visibilityState.get())
                    togglePasswordVisibility(editText, visibilityState.get())
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun togglePasswordVisibility(editText: EditText, isVisible: Boolean) {
        if (isVisible) {
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_ojo, 0)
        } else {
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_ojo_cerrado, 0)
        }
        editText.setSelection(editText.text.length)
    }
}