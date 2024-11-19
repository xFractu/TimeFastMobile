package uv.tc.timefastmobile

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import uv.tc.timefastmobile.databinding.ActivityEstatusBinding

class EstatusActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEstatusBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEstatusBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
    }
}