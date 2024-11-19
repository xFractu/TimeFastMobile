package uv.tc.timefastmobile

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import uv.tc.timefastmobile.databinding.ActivityInicioBinding

class InicioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInicioBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityInicioBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

        val view = binding.root
        setContentView(view)

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

    private fun replaceFragment(fragment: Fragment){

        val fragmentManger = supportFragmentManager
        val fragmentTransaction = fragmentManger.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container_inicio,fragment)
        fragmentTransaction.commit()


    }



}