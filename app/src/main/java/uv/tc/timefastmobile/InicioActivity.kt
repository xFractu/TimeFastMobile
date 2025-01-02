package uv.tc.timefastmobile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import uv.tc.timefastmobile.databinding.ActivityInicioBinding
import uv.tc.timefastmobile.poko.Colaborador

class InicioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInicioBinding
    private lateinit var colaborador: Colaborador

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityInicioBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

        val view = binding.root
        setContentView(view)

        val colaboradorJSON = intent.getStringExtra("colaborador")
        if (colaboradorJSON != null) {
            val gson = Gson()
            colaborador = gson.fromJson(colaboradorJSON, Colaborador::class.java)
            mostrarDatosColaborador(colaborador)
        } else {
            Toast.makeText(this, "No se recibieron datos del colaborador", Toast.LENGTH_LONG).show()
        }

        // Pasar el colaborador a los fragmentos
        replaceFragment(InicioFragment().apply {
            arguments = Bundle().apply {
                putString("colaborador", Gson().toJson(colaborador))
            }
        })

        binding.btnInicio.setOnClickListener {
            replaceFragment(InicioFragment().apply {
                arguments = Bundle().apply {
                    putString("colaborador", Gson().toJson(colaborador))
                }
            })
        }

        binding.btnPerfil.setOnClickListener {
            replaceFragment(PerfilFragment().apply {
                arguments = Bundle().apply {
                    putString("colaborador", Gson().toJson(colaborador))
                }
            })
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

