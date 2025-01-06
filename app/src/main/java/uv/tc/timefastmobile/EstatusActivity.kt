package uv.tc.timefastmobile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.koushikdutta.async.future.FutureCallback
import com.koushikdutta.ion.Ion
import uv.tc.timefastmobile.databinding.ActivityEstatusBinding
import uv.tc.timefastmobile.poko.Colaborador
import uv.tc.timefastmobile.util.Constantes

class EstatusActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEstatusBinding
    private var idEnvio: Int = 0
    private var numGuia: String = ""
    private var estatusActual: String = ""
    private lateinit var colaborador: Colaborador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEstatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val colaboradorJSON = intent.getStringExtra("colaborador")
        if (colaboradorJSON != null) {
            val gson = Gson()
            colaborador = gson.fromJson(colaboradorJSON, Colaborador::class.java)
            //mostrarDatosColaborador(colaborador)
        } else {
            //Toast.makeText(this, "No se recibieron datos del colaborador", Toast.LENGTH_LONG).show()
        }

        // Obtener los datos del Intent
        idEnvio = intent.getIntExtra("idEnvio", 0)
        numGuia = intent.getStringExtra("numGuia") ?: ""
        estatusActual = intent.getStringExtra("estatus") ?: ""

        // Mostrar número de guía
        binding.tvNumGuiaEnvio.text = numGuia

        // Inicializar estado visual
        actualizarEstadoVisual(estatusActual)

        // Listeners para cambios de estatus
        binding.llEntregado.setOnClickListener { cambiarEstatus("Entregado") }
        binding.llEnTransito.setOnClickListener { cambiarEstatus("En transito") }
        binding.llDetenido.setOnClickListener { cambiarEstatus("Detenido") }
        binding.llCancelado.setOnClickListener { cambiarEstatus("Cancelado") }

        // Listener del botón de actualizar estatus
        binding.btnActualizarEstatus.setOnClickListener { actualizarEstatus() }


        binding.btnInicio.setOnClickListener {
            irPantallaInicio(colaborador)
        }

        binding.btnPerfil.setOnClickListener {
            irPantallaPerfil(colaborador)
        }

    }

    private fun cambiarEstatus(nuevoEstatus: String) {
        estatusActual = nuevoEstatus
        actualizarEstadoVisual(nuevoEstatus)
    }

    private fun actualizarEstadoVisual(estatus: String) {
        // Mapa de estados con sus respectivos layouts, iconos y textos
        val estados = mapOf(
            "Entregado" to Triple(binding.llEntregado, binding.ivEntregado, binding.tvEntregado),
            "En transito" to Triple(binding.llEnTransito, binding.ivEnTransito, binding.tvEnTransito),
            "Detenido" to Triple(binding.llDetenido, binding.ivDetenido, binding.tvDetenido),
            "Cancelado" to Triple(binding.llCancelado, binding.ivCancelado, binding.tvCancelado)
        )

        // Resetear todos los estados visuales
        estados.values.forEach { (layout, icon, text) ->
            layout.backgroundTintList = getColorStateList(R.color.l_secondary)
            icon.setColorFilter(getColor(R.color.l_accent))
            text.setTextColor(getColor(R.color.l_primary_dark))
        }

        // Cambiar colores del estado seleccionado
        estados[estatus]?.let { (layout, icon, text) ->
            layout.backgroundTintList = getColorStateList(R.color.l_primary)
            icon.setColorFilter(getColor(R.color.l_secondary))
            text.setTextColor(getColor(R.color.l_secondary))
        }
    }

    private fun actualizarEstatus() {
        // Validar si "Detenido" o "Cancelado" requieren comentario
        val comentario = binding.etComentario.text.toString()
        if ((estatusActual == "Detenido" || estatusActual == "Cancelado") && comentario.isBlank()) {
            Toast.makeText(this, "Debe agregar un comentario para este estatus", Toast.LENGTH_SHORT).show()
            return
        }

        // Preparar JSON para la llamada
        val json = mapOf(
            "idEnvio" to idEnvio,
            "descripcion" to comentario.ifBlank { "El paquete está $estatusActual" },
            "estado" to estatusActual
        )

        // Realizar la llamada POST
        Ion.with(this)
            .load("${Constantes().URL_WS}/envios/nuevo-estado")
            .setJsonPojoBody(json)
            .asJsonObject()
            .setCallback(FutureCallback { e, result ->
                if (e != null) {
                    Toast.makeText(this, "Error al actualizar estatus: ${e.message}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Estatus actualizado correctamente", Toast.LENGTH_SHORT).show()
                    irPantallaInicioF(colaborador)

                }
            })
    }

    private fun mostrarDatosColaborador(colaborador: Colaborador) {
        val mensaje = """
            Estatus Activity:
            No. Personal: ${colaborador.noPersonal}
            Nombre: ${colaborador.persona.nombre} ${colaborador.persona.apellidoPaterno} ${colaborador.persona.apellidoMaterno}
            Rol: ${colaborador.rol.rol}
        """.trimIndent()
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    private fun irPantallaInicioF(colaborador: Colaborador) {
        val gson = Gson()
        val colaboradorJson = gson.toJson(colaborador)
        val intent = Intent(this@EstatusActivity, InicioActivity::class.java)
        intent.putExtra("colaborador", colaboradorJson)
        intent.putExtra("inicio", "InicioLogin")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun irPantallaPerfil(colaborador: Colaborador) {
        val gson = Gson()
        val colaboradorJson = gson.toJson(colaborador)
        val intent = Intent(this@EstatusActivity, InicioActivity::class.java)
        intent.putExtra("colaborador", colaboradorJson)
        intent.putExtra("inicio", "Perfil")
        startActivity(intent)
    }

    private fun irPantallaInicio(colaborador: Colaborador) {
        val gson = Gson()
        val colaboradorJson = gson.toJson(colaborador)
        val intent = Intent(this@EstatusActivity, InicioActivity::class.java)
        intent.putExtra("colaborador", colaboradorJson)
        intent.putExtra("inicio", "Inicio")
        startActivity(intent)
    }
}
