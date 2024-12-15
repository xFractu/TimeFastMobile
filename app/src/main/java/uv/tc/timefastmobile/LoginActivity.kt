package uv.tc.timefastmobile

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.koushikdutta.ion.Ion
import uv.tc.timefastmobile.databinding.ActivityLoginBinding
import uv.tc.timefastmobile.poko.LoginColaborador
import uv.tc.timefastmobile.util.Constantes
import kotlin.reflect.KMutableProperty0

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var passwordActual = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupPasswordVisibilityToggle(binding.etPassword, ::passwordActual)

        binding.btnLoginIngresar.setOnClickListener {
            val noPersonal = binding.etNoPersonal.text.toString()
            val password = binding.etPassword.text.toString()
            if (sonCamposValidos(noPersonal, password)) {
                verificarCredenciales(noPersonal, password)
            }
        }
    }

    private fun sonCamposValidos(noPersonal: String, password: String): Boolean {
        var camposValidos = true

        if (noPersonal.isEmpty()) {
            camposValidos = false
            binding.etNoPersonal.error = "Número de personal obligatorio"
        }
        if (password.isEmpty()) {
            camposValidos = false
            binding.etPassword.error = "Contraseña obligatoria"
        }
        return camposValidos
    }

    private fun verificarCredenciales(noPersonal: String, password: String) {
        // Configuración de Ion (solo la primera vez)
        Ion.getDefault(this@LoginActivity).conscryptMiddleware.enable(false)

        // Llamada al Web Service
        Ion.with(this@LoginActivity)
            .load("POST", "${Constantes().URL_WS}/conductores/iniciarSesion")
            .setHeader("Content-Type", "application/x-www-form-urlencoded")
            .setBodyParameter("noPersonal", noPersonal)
            .setBodyParameter("password", password)
            .asString()
            .setCallback { e, result ->
                if (e == null) {
                    procesarRespuesta(result)
                } else {
                    Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun procesarRespuesta(json: String) {
        val gson = Gson()
        val respuesta = gson.fromJson(json, LoginColaborador::class.java)
        Toast.makeText(this@LoginActivity, respuesta.mensaje, Toast.LENGTH_LONG).show()

        if (!respuesta.error) {
            val colaboradorJSON = gson.toJson(respuesta.colaborador)
            irPantallaInicio(colaboradorJSON)
        }
    }

    private fun irPantallaInicio(colaborador: String) {
        val intent = Intent(this@LoginActivity, InicioActivity::class.java)
        intent.putExtra("colaborador", colaborador)
        startActivity(intent)
        finish()
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
