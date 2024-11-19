package uv.tc.timefastmobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import uv.tc.timefastmobile.databinding.ActivityDetallesEnvioBinding

class DetallesEnvioActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetallesEnvioBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetallesEnvioBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)

        binding.tvCambiarEstatus.setOnClickListener {

            irPantallaEstatus()

        }

    }


    fun irPantallaEstatus(){

        val intent = Intent(this@DetallesEnvioActivity,EstatusActivity::class.java)
        startActivity(intent)
        finish()


    }


}