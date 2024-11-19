package uv.tc.timefastmobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import uv.tc.timefastmobile.databinding.FragmentInicioBinding
import uv.tc.timefastmobile.poko.Envio
import uv.tc.timefastmobile.adaptadores.EnviosAdapter

class InicioFragment : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Datos de prueba
        val enviosList = listOf(
            Envio("1Z999AA10123456784", "Avenida Insurgentes Sur 1234, Colonia Del Valle, CP 03100, Ciudad de México.", "Pendiente"),
            Envio("1Z999AA10123456785", "Calle Reforma 345, Colonia Centro, CP 06500, Ciudad de México.", "En tránsito"),
            Envio("1Z999AA10123456786", "Boulevard Miguel Alemán 456, Colonia El Rosario, CP 50000, Toluca, Estado de México.", "Entregado"),
            Envio("1Z999AA10123456786", "Boulevard Miguel Alemán 456, Colonia El Rosario, CP 50000, Toluca, Estado de México.", "Entregado"),
            Envio("1Z999AA10123456784", "Avenida Insurgentes Sur 1234, Colonia Del Valle, CP 03100, Ciudad de México.", "Pendiente")


        )


        binding.recyclerEnvios.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerEnvios.adapter = EnviosAdapter(requireContext(), enviosList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}