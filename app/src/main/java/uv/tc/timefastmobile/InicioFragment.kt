package uv.tc.timefastmobile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import uv.tc.timefastmobile.databinding.FragmentInicioBinding
import uv.tc.timefastmobile.poko.Envio
import uv.tc.timefastmobile.poko.Colaborador
import uv.tc.timefastmobile.adaptadores.EnviosAdapter

class InicioFragment : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!
    private lateinit var colaborador: Colaborador
    private var enviosList: List<Envio> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener el colaborador desde los argumentos del fragmento
        val colaboradorJSON = arguments?.getString("colaborador")
        if (colaboradorJSON != null) {
            val gson = Gson()
            colaborador = gson.fromJson(colaboradorJSON, Colaborador::class.java)
            mostrarDatosColaborador(colaborador)
        }

        val gson = Gson()

        arguments?.getString("envios")?.let {
            enviosList = gson.fromJson(it, object : TypeToken<List<Envio>>() {}.type)
        }

        

        configurarRecycler()
    }

    private fun mostrarDatosColaborador(colaborador: Colaborador) {
        val mensaje = """
            Inicio Fragment:
            No. Personal: ${colaborador.noPersonal}
            Nombre: ${colaborador.persona.nombre} ${colaborador.persona.apellidoPaterno} ${colaborador.persona.apellidoMaterno}
            Rol: ${colaborador.rol.rol}
        """.trimIndent()
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()
    }

    private fun configurarRecycler() {
        binding.recyclerEnvios.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerEnvios.adapter = EnviosAdapter(requireContext(), enviosList, colaborador)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
