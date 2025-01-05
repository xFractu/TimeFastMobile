package uv.tc.timefastmobile

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import uv.tc.timefastmobile.databinding.ActivityEditarPerfilBinding
import uv.tc.timefastmobile.poko.Colaborador
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
            mostrarDatosColaborador(colaborador)
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