package uv.tc.timefastmobile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import uv.tc.timefastmobile.databinding.ActivityDetallesEnvioBinding
import uv.tc.timefastmobile.poko.Colaborador
import uv.tc.timefastmobile.poko.Envio

class DetallesEnvioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetallesEnvioBinding
    private lateinit var colaborador: Colaborador
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetallesEnvioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val colaboradorJSON = intent.getStringExtra("colaborador")
        if (colaboradorJSON != null) {
            val gson = Gson()
            colaborador = gson.fromJson(colaboradorJSON, Colaborador::class.java)
            //mostrarDatosColaborador(colaborador)
        } else {
            //Toast.makeText(this, "No se recibieron datos del colaborador", Toast.LENGTH_LONG).show()
        }

        val envioJson = intent.getStringExtra("envio")
        if (envioJson != null) {
            val envio = Gson().fromJson(envioJson, Envio::class.java)
            mostrarDetallesEnvio(envio)
            binding.tvNumGuiaEnvio.text = envio.numGuia
            binding.tvCambiarEstatus.setOnClickListener {
                irPantallaEstatus(envio,colaborador)
            }
        } else {
            binding.tvInfoEstatus.text = "Error al cargar el envío"
        }



        binding.btnInicio.setOnClickListener {
            irPantallaInicio(colaborador)
        }

        binding.btnPerfil.setOnClickListener {
            irPantallaPerfil(colaborador)
        }


    }

    private fun irPantallaInicio(colaborador: Colaborador) {
        val gson = Gson()
        val colaboradorJson = gson.toJson(colaborador)
        val intent = Intent(this@DetallesEnvioActivity, InicioActivity::class.java)
        intent.putExtra("colaborador", colaboradorJson)
        intent.putExtra("inicio", "Inicio")
        startActivity(intent)
    }

    private fun irPantallaPerfil(colaborador: Colaborador) {
        val gson = Gson()
        val colaboradorJson = gson.toJson(colaborador)
        val intent = Intent(this@DetallesEnvioActivity, InicioActivity::class.java)
        intent.putExtra("colaborador", colaboradorJson)
        intent.putExtra("inicio", "Perfil")
        startActivity(intent)
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
            "Nombre: ${persona.nombre} ${persona.apellidoPaterno} ${persona.apellidoMaterno}\nNúmero: $telefono\nCorreo: ${persona.correo}"
        }
    }

    private fun mostrarDatosColaborador(colaborador: Colaborador) {
        val mensaje = """
            Detalles Envio:
            No. Personal: ${colaborador.noPersonal}
            Nombre: ${colaborador.persona.nombre} ${colaborador.persona.apellidoPaterno} ${colaborador.persona.apellidoMaterno}
            Rol: ${colaborador.rol.rol}
        """.trimIndent()
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    private fun irPantallaEstatus(envio: Envio, colaborador: Colaborador) {
        val gson = Gson()
        val colaboradorJson = gson.toJson(colaborador)
        val intent = Intent(this, EstatusActivity::class.java).apply {
            putExtra("idEnvio", envio.idEnvio) // Debe ser Int
            putExtra("estatus", envio.estatus) // Debe ser String
            putExtra("numGuia", envio.numGuia) // Debe ser String
            putExtra("colaborador", colaboradorJson) // Enviado como JSON
        }
        startActivity(intent)
    }
}
