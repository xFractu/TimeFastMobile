package uv.tc.timefastmobile.adaptadores

import android.content.Intent
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uv.tc.timefastmobile.DetallesEnvioActivity
import uv.tc.timefastmobile.InicioActivity
import uv.tc.timefastmobile.databinding.ItemRecyclerEnvioBinding
import uv.tc.timefastmobile.poko.Envio

class EnviosAdapter(
    private val context: Context,
    private val enviosList: List<Envio>

) : RecyclerView.Adapter<EnviosAdapter.EnvioViewHolder>() {

    inner class EnvioViewHolder(private val binding: ItemRecyclerEnvioBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(envio: Envio) {
            binding.tvNumeroGuia.text = "${envio.numeroGuia}"
            binding.tvDireccion.text = "${envio.direccion}"
            binding.tvEstatus.text = "${envio.estatus}"
            binding.btnDetalles.setOnClickListener {
                irPantallaDetallesEnvio()



            }
        }
    }

    private fun irPantallaDetallesEnvio() {
        val intent = Intent(context, DetallesEnvioActivity::class.java)
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