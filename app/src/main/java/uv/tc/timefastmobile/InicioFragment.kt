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
import com.koushikdutta.ion.Ion
import org.json.JSONArray
import org.json.JSONObject
import uv.tc.timefastmobile.databinding.FragmentInicioBinding
import uv.tc.timefastmobile.poko.*
import uv.tc.timefastmobile.adaptadores.EnviosAdapter
import uv.tc.timefastmobile.util.Constantes
import java.nio.charset.Charset

class InicioFragment : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    private lateinit var colaborador: Colaborador

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("InicioFragment", "onViewCreated: Iniciando fragmento")

        // Obtener el colaborador desde los argumentos del fragmento
        val colaboradorJSON = arguments?.getString("colaborador")
        if (colaboradorJSON != null) {
            Log.d("InicioFragment", "onViewCreated: Colaborador recibido: $colaboradorJSON")
            val gson = Gson()
            colaborador = gson.fromJson(colaboradorJSON, Colaborador::class.java)
            mostrarDatosColaborador(colaborador)
        } else {
            Log.e("InicioFragment", "onViewCreated: No se recibió información del colaborador")
        }

        obtenerEnvios()
    }

    private fun mostrarDatosColaborador(colaborador: Colaborador) {
        Log.d("InicioFragment", "mostrarDatosColaborador: Mostrando datos del colaborador")
        val rol = colaborador.rol
        val mensaje = """
            Colaborador Autenticado:
            idColaborador: ${colaborador.idColaborador}
            Nombre: ${colaborador.persona.nombre} ${colaborador.persona.apellidoPaterno} ${colaborador.persona.apellidoMaterno}
            Rol: ${rol.rol}
            No. Personal: ${colaborador.noPersonal}
            ${if (rol.numLicencia != null) "Licencia: ${rol.numLicencia}" else ""}
        """.trimIndent()
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()
    }

    private fun obtenerEnvios() {
        Log.d("InicioFragment", "obtenerEnvios: Iniciando solicitud de envíos")
        Ion.getDefault(requireContext()).conscryptMiddleware.enable(false)

        Ion.with(requireContext())
            .load("GET", "${Constantes().URL_WS}/envios/detalles-envio/${colaborador.idColaborador}")
            .asString(Charset.forName("UTF-8"))
            .setCallback { e, result ->
                if (e == null) {
                    Log.d("InicioFragment", "obtenerEnvios: Resultado recibido: $result")
                    try {
                        val enviosList = procesarEnvios(JSONArray(result))
                        actualizarEstatusEnvios(enviosList)
                        obtenerPaquetesParaEnvios(enviosList)
                    } catch (ex: Exception) {
                        Log.e("InicioFragment", "obtenerEnvios: Error al procesar envíos", ex)
                        Toast.makeText(requireContext(), "Error al procesar los envíos", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Log.e("InicioFragment", "obtenerEnvios: Error en la solicitud", e)
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun procesarEnvios(jsonArray: JSONArray): List<Envio> {
        Log.d("InicioFragment", "procesarEnvios: Procesando JSON con ${jsonArray.length()} elementos")
        val enviosList = mutableListOf<Envio>()
        val gson = Gson()

        for (i in 0 until jsonArray.length()) {
            try {
                val jsonObject = jsonArray.getJSONObject(i)
                val envio = Envio(
                    idEnvio = jsonObject.getInt("idEnvio"),
                    idOrigen = jsonObject.getInt("idOrigen"),
                    idDestino = jsonObject.getInt("idDestino"),
                    origen = gson.fromJson(jsonObject.getJSONObject("origen").toString(), Direccion::class.java),
                    destino = gson.fromJson(jsonObject.getJSONObject("destino").toString(), Direccion::class.java),
                    cliente = gson.fromJson(jsonObject.getJSONObject("cliente").toString(), Cliente::class.java),
                    conductor = colaborador,
                    costo = jsonObject.getDouble("costo"),
                    fecha = jsonObject.getString("fecha"),
                    numGuia = jsonObject.getString("numGuia"),
                    paquetes = emptyList(),
                    estatus = "" // Inicialmente vacío, se actualizará después
                )
                enviosList.add(envio)
                Log.d("InicioFragment", "procesarEnvios: Envío procesado correctamente: $envio")
            } catch (ex: Exception) {
                Log.e("InicioFragment", "procesarEnvios: Error procesando envío en índice $i", ex)
            }
        }
        return enviosList
    }

    private fun obtenerPaquetesParaEnvios(enviosList: List<Envio>) {
        Log.d("InicioFragment", "obtenerPaquetesParaEnvios: Obteniendo paquetes para los envíos")
        var completados = 0
        val total = enviosList.size

        for (envio in enviosList) {
            Ion.with(requireContext())
                .load("GET", "${Constantes().URL_WS}/paquetes/obtener-paquetes-por-envio/${envio.idEnvio}")
                .asString(Charset.forName("UTF-8"))
                .setCallback { e, result ->
                    if (e == null) {
                        try {
                            val jsonObject = JSONObject(result)
                            if (!jsonObject.getBoolean("error")) {
                                val paquetesJson = JSONArray(jsonObject.getJSONObject("objeto").getString("value"))
                                val paquetes = mutableListOf<Paquete>()
                                for (i in 0 until paquetesJson.length()) {
                                    val paqueteJson = paquetesJson.getJSONObject(i)
                                    val paquete = Paquete(
                                        id = paqueteJson.getInt("id"),
                                        idEnvio = paqueteJson.getInt("idEnvio"),
                                        descripcion = paqueteJson.getString("descripcion"),
                                        dimensiones = paqueteJson.getString("dimensiones"),
                                        peso = paqueteJson.getDouble("peso")
                                    )
                                    paquetes.add(paquete)
                                }
                                envio.paquetes = paquetes
                                Log.d("InicioFragment", "obtenerPaquetesParaEnvios: Paquetes asignados a envío ${envio.idEnvio}")
                            }
                        } catch (ex: Exception) {
                            Log.e("InicioFragment", "obtenerPaquetesParaEnvios: Error procesando paquetes para el envío ${envio.idEnvio}", ex)
                        }
                    } else {
                        Log.e("InicioFragment", "obtenerPaquetesParaEnvios: Error en la solicitud para el envío ${envio.idEnvio}", e)
                    }

                    completados++
                    if (completados == total) {
                        configurarRecycler(enviosList)
                    }
                }
        }
    }

    private fun actualizarEstatusEnvios(enviosList: List<Envio>) {
        Log.d("InicioFragment", "actualizarEstatusEnvios: Obteniendo estatus para ${enviosList.size} envíos")
        var completados = 0
        val total = enviosList.size

        for (envio in enviosList) {
            Ion.with(requireContext())
                .load("GET", "${Constantes().URL_WS}/envios/consultar-estado/${envio.idEnvio}")
                .asString(Charset.forName("UTF-8"))
                .setCallback { e, result ->
                    if (e == null) {
                        try {
                            val jsonObject = JSONObject(result)
                            envio.estatus = jsonObject.getString("objeto").substringAfter("descripcion=").substringBefore("}")
                            Log.d("InicioFragment", "actualizarEstatusEnvios: Estatus obtenido para envío ${envio.idEnvio}: ${envio.estatus}")
                        } catch (ex: Exception) {
                            Log.e("InicioFragment", "actualizarEstatusEnvios: Error al procesar estatus del envío ${envio.idEnvio}", ex)
                        }
                    } else {
                        Log.e("InicioFragment", "actualizarEstatusEnvios: Error en la solicitud para el envío ${envio.idEnvio}", e)
                    }

                    completados++
                    if (completados == total) {
                        configurarRecycler(enviosList) // Configurar RecyclerView al completar todas las solicitudes
                    }
                }
        }
    }

    private fun configurarRecycler(enviosList: List<Envio>) {
        binding.recyclerEnvios.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerEnvios.adapter = EnviosAdapter(requireContext(), enviosList)
        Log.d("InicioFragment", "configurarRecycler: RecyclerView configurado con ${enviosList.size} envíos")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

