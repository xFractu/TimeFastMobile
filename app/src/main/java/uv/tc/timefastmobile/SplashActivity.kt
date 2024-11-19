package uv.tc.timefastmobile

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import uv.tc.timefastmobile.databinding.ActivityInicioBinding
import uv.tc.timefastmobile.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)

        irPantallaInicio()

    }

    fun irPantallaInicio(){


        Handler().postDelayed({
            val intent = Intent(this@SplashActivity,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)



    }
}