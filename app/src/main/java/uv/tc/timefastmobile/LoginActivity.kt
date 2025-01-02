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
import org.json.JSONObject
import com.koushikdutta.ion.Ion
import uv.tc.timefastmobile.databinding.ActivityLoginBinding
import uv.tc.timefastmobile.poko.Colaborador
import uv.tc.timefastmobile.util.Constantes
import java.nio.charset.Charset
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
        Ion.getDefault(this@LoginActivity).conscryptMiddleware.enable(false)

        Ion.with(this@LoginActivity)
            .load("POST", "${Constantes().URL_WS}/login/login-colaborador-conductor")
            .setHeader("Content-Type", "application/x-www-form-urlencoded")
            .setBodyParameter("noPersonal", noPersonal)
            .setBodyParameter("password", password)
            .asString(Charset.forName("UTF-8"))
            .setCallback { e, result ->
                if (e == null) {
                    procesarRespuesta(result)
                } else {
                    Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun procesarRespuesta(json: String) {
        try {
            val gson = Gson()
            val jsonObject = JSONObject(json)

            // Extraer el mensaje y verificar si hubo error
            val mensaje = jsonObject.getString("mensaje")
            val error = jsonObject.getBoolean("error")
            Toast.makeText(this@LoginActivity, mensaje, Toast.LENGTH_LONG).show()

            if (!error) {
                // Extraer y decodificar el colaborador
                val objeto = jsonObject.getJSONObject("objeto")
                val colaboradorJson = objeto.getString("value")
                val colaborador = gson.fromJson(colaboradorJson, Colaborador::class.java)

                irPantallaInicio(gson.toJson(colaborador))
            }
        } catch (ex: Exception) {
            Toast.makeText(this@LoginActivity, "Error al procesar la respuesta", Toast.LENGTH_LONG).show()
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

