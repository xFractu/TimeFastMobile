package uv.tc.timefastmobile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import uv.tc.timefastmobile.databinding.ActivityDetallesEnvioBinding
import uv.tc.timefastmobile.poko.Envio

class DetallesEnvioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetallesEnvioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetallesEnvioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val envioJson = intent.getStringExtra("envio")
        if (envioJson != null) {
            val envio = Gson().fromJson(envioJson, Envio::class.java)
            mostrarDetallesEnvio(envio)
        } else {
            binding.tvInfoEstatus.text = "Error al cargar el envío"
        }

        binding.tvCambiarEstatus.setOnClickListener {
            irPantallaEstatus()
        }
    }

    private fun mostrarDetallesEnvio(envio: Envio) {
        binding.tvInfoDireccionDeOrigen.text = with(envio.origen) {
            "$calle #$numero, $colonia, CP $codigoPostal, $ciudad, $estado"
        }
        binding.tvInfoDireccionDeDestino.text = with(envio.destino) {
            "$calle #$numero, $colonia, CP $codigoPostal, $ciudad, $estado"
        }

        binding.tvInfoPaquetes.text = envio.paquetes.joinToString("\n") {
            "- ${it.descripcion} (${it.peso} kg)"
        }

        binding.tvInfoEstatus.text = envio.estatus
        val estatusDrawable = when (envio.estatus) {
            "Pendiente" -> R.drawable.ic_transito
            "En Tránsito" -> R.drawable.ic_transito
            "Detenido" -> R.drawable.ic_detenido
            "Entregado" -> R.drawable.ic_entregado
            "Cancelado" -> R.drawable.ic_cancelado
            else -> R.drawable.ic_transito
        }
        binding.ivEstatus.setImageResource(estatusDrawable)

        binding.tvInfoContacto.text = with(envio.cliente) {
            "${persona.nombre}, ${persona.apellidoPaterno}, ${persona.apellidoMaterno}, Número: $telefono, Correo: ${persona.correo}, CURP: ${persona.CURP}"
        }
    }

    private fun irPantallaEstatus() {
        startActivity(Intent(this, EstatusActivity::class.java))
        finish()
    }
}
