package uv.tc.timefastmobile

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import uv.tc.timefastmobile.databinding.ActivityInicioBinding
import uv.tc.timefastmobile.poko.Colaborador

class InicioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInicioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityInicioBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

        val view = binding.root
        setContentView(view)

        // Recuperar y mostrar datos del colaborador
        val colaboradorJSON = intent.getStringExtra("colaborador")
        if (colaboradorJSON != null) {
            val gson = Gson()
            val colaborador = gson.fromJson(colaboradorJSON, Colaborador::class.java)
            mostrarDatosColaborador(colaborador)
        } else {
            Toast.makeText(this, "No se recibieron datos del colaborador", Toast.LENGTH_LONG).show()
        }

        replaceFragment(InicioFragment())

        binding.btnInicio.setOnClickListener {
            replaceFragment(InicioFragment())
            binding.containerNav.setBackgroundColor(
                ContextCompat.getColor(this, R.color.l_primary_dark)
            )
        }

        binding.btnPerfil.setOnClickListener {
            replaceFragment(PerfilFragment())
            binding.containerNav.setBackgroundColor(
                ContextCompat.getColor(this, R.color.l_secondary)
            )
        }

        binding.btnCerrarSesion.setOnClickListener {
            replaceFragment(CerrarSesionFragment())
            binding.containerNav.setBackgroundColor(
                ContextCompat.getColor(this, R.color.l_primary_dark)
            )
        }
    }

    private fun mostrarDatosColaborador(colaborador: Colaborador) {
        val mensaje = """
            Colaborador Autenticado:
            No. Personal: ${colaborador.noPersonal}
            Nombre: ${colaborador.persona.nombre} ${colaborador.persona.apellidoPaterno} ${colaborador.persona.apellidoMaterno}
            Rol: ${colaborador.rol.rol}
        """.trimIndent()
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container_inicio, fragment)
        fragmentTransaction.commit()
    }
}
