package uv.tc.timefastmobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import uv.tc.timefastmobile.databinding.FragmentPerfilBinding
import kotlin.reflect.KMutableProperty0
import android.view.MotionEvent
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod

class PerfilFragment : Fragment(R.layout.fragment_perfil) {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

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

        // Configuración de visibilidad de contraseñas
        setupPasswordVisibilityToggle(binding.etPasswordActual, ::passwordActualVisible)
        setupPasswordVisibilityToggle(binding.etPasswordNueva, ::passwordNuevaVisible)
        setupPasswordVisibilityToggle(binding.etPasswordConfirmar, ::passwordConfirmarVisible)

        // Aquí irían otras configuraciones del fragmento, como inicializar listas o cargar datos
    }

    private fun setupPasswordVisibilityToggle(editText: EditText, visibilityState: KMutableProperty0<Boolean>) {
        editText.setOnTouchListener { _, event ->
            val DRAWABLE_END = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (editText.right - editText.compoundDrawables[DRAWABLE_END].bounds.width())) {
                    visibilityState.set(!visibilityState.get()) // Cambiar el estado de visibilidad
                    togglePasswordVisibility(editText, visibilityState.get()) // Actualizar visibilidad
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
        editText.setSelection(editText.text.length) // Mantener el cursor al final
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}