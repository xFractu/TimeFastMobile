package uv.tc.timefastmobile.adaptadores

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import uv.tc.timefastmobile.DetallesEnvioActivity
import uv.tc.timefastmobile.databinding.ItemRecyclerEnvioBinding
import uv.tc.timefastmobile.poko.Envio

class EnviosAdapter(
    private val context: Context,
    private val enviosList: List<Envio>
) : RecyclerView.Adapter<EnviosAdapter.EnvioViewHolder>() {

    inner class EnvioViewHolder(private val binding: ItemRecyclerEnvioBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(envio: Envio) {
            // Formatear la dirección del destino
            val direccionDestino = with(envio.destino) {
                "$calle #$numero, $colonia, CP $codigoPostal, $ciudad, $estado"
            }

            // Asignar valores a los TextViews
            binding.tvNumeroGuia.text = envio.numGuia
            binding.tvDireccion.text = direccionDestino
            binding.tvEstatus.text = envio.estatus

            // Configurar el botón de detalles
            binding.btnDetalles.setOnClickListener {
                irPantallaDetallesEnvio(envio)
            }
        }
    }

    private fun irPantallaDetallesEnvio(envio: Envio) {
        val gson = Gson()
        val envioJson = gson.toJson(envio)
        val intent = Intent(context, DetallesEnvioActivity::class.java).apply {
            putExtra("envio", envioJson)
        }
        context.startActivity(intent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnvioViewHolder {
        val binding = ItemRecyclerEnvioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EnvioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EnvioViewHolder, position: Int) {
        holder.bind(enviosList[position])
    }

    override fun getItemCount(): Int = enviosList.size
}
