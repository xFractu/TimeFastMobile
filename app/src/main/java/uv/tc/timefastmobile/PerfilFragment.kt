package uv.tc.timefastmobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import uv.tc.timefastmobile.databinding.FragmentPerfilBinding
import com.google.gson.Gson
import uv.tc.timefastmobile.poko.Colaborador
import kotlin.reflect.KMutableProperty0
import android.view.MotionEvent
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod

class PerfilFragment : Fragment(R.layout.fragment_perfil) {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    private lateinit var colaborador: Colaborador

    // Propiedades para manejar la visibilidad de las contraseñas
    private var passwordActualVisible: Boolean = false
    private var passwordNuevaVisible: Boolean = false
    private var passwordConfirmarVisible: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener el colaborador desde los argumentos del fragmento
        val colaboradorJSON = arguments?.getString("colaborador")
        if (colaboradorJSON != null) {
            val gson = Gson()
            colaborador = gson.fromJson(colaboradorJSON, Colaborador::class.java)
            mostrarDatosColaborador(colaborador)
        }

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
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
