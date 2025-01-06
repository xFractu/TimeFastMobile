package uv.tc.timefastmobile

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.koushikdutta.ion.Ion
import org.json.JSONArray
import org.json.JSONObject
import uv.tc.timefastmobile.databinding.ActivityInicioBinding
import uv.tc.timefastmobile.poko.*
import uv.tc.timefastmobile.util.Constantes
import java.nio.charset.Charset

class InicioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInicioBinding
    private lateinit var colaborador: Colaborador
    private var enviosList: MutableList<Envio> = mutableListOf()
    private lateinit var inicio: String

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityInicioBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

        val view = binding.root
        setContentView(view)

        val pantalla = intent.getStringExtra("inicio")
        inicio = pantalla +""
        val colaboradorJSON = intent.getStringExtra("colaborador")
        if (colaboradorJSON != null) {
            val gson = Gson()
            colaborador = gson.fromJson(colaboradorJSON, Colaborador::class.java)
            //mostrarDatosColaborador(colaborador)
            obtenerEnvios()
        } else {
            //Toast.makeText(this, "No se recibieron datos del colaborador", Toast.LENGTH_LONG).show()
        }



        // Manejo de navegación
        binding.btnInicio.setOnClickListener { openInicioFragment() }


        binding.btnPerfil.setOnClickListener { openPerfilFragment() }
    }

    private fun mostrarDatosColaborador(colaborador: Colaborador) {
        val mensaje = """
            Colaborador Autenticado:
            No. Personal: ${colaborador.noPersonal}
            Nombre: ${colaborador.persona.nombre} ${colaborador.persona.apellidoPaterno} ${colaborador.persona.apellidoMaterno}
            Rol: ${colaborador.rol.rol}
        """.trimIndent()
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    private fun openPerfilFragment(){
        setSecondaryColor()
        replaceFragment(PerfilFragment().apply {
            arguments = Bundle().apply {
                putString("colaborador", Gson().toJson(colaborador))
            }
        }, addToBackStack = true)
    }

    private fun openInicioFragment() {
        setPrimaryDarkColor()
        replaceFragment(InicioFragment().apply {
            arguments = Bundle().apply {
                putString("colaborador", Gson().toJson(colaborador))
                putString("envios", Gson().toJson(enviosList))
            }
        }, addToBackStack = true)
    }


    private fun openInicioFragmentBorrar() {
        setPrimaryDarkColor()
        replaceFragmentClearingBackStack(InicioFragment().apply {
            arguments = Bundle().apply {
                putString("colaborador", Gson().toJson(colaborador))
                putString("envios", Gson().toJson(enviosList))
            }
        })
    }

    private fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container_inicio, fragment)

        // Condicionalmente agregar a la pila de retroceso
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null)
        }

        fragmentTransaction.commit()
    }

    private fun replaceFragmentClearingBackStack(fragment: Fragment) {
        val fragmentManager = supportFragmentManager


        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)


        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container_inicio, fragment)
        fragmentTransaction.commit()
    }


    fun setPrimaryDarkColor() {
        binding.containerNav.setBackgroundColor(
            ContextCompat.getColor(this, R.color.l_primary_dark)
        )
    }


    fun setSecondaryColor() {
        binding.containerNav.setBackgroundColor(
            ContextCompat.getColor(this, R.color.l_secondary)
        )
    }

    private fun obtenerEnvios() {
        Ion.getDefault(this).conscryptMiddleware.enable(false)

        Ion.with(this)
            .load("GET", "${Constantes().URL_WS}/envios/detalles-envio/${colaborador.idColaborador}")
            .asString(Charset.forName("UTF-8"))
            .setCallback { e, result ->
                if (e == null) {
                    try {
                        enviosList = procesarEnvios(JSONArray(result)).toMutableList()
                        actualizarEstatusEnvios()
                    } catch (ex: Exception) {
                        Log.e("InicioActivity", "obtenerEnvios: Error al procesar envíos", ex)
                        Toast.makeText(this, "Error al procesar los envíos", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Log.e("InicioActivity", "obtenerEnvios: Error en la solicitud", e)
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun procesarEnvios(jsonArray: JSONArray): List<Envio> {
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
                    estatus = ""
                )
                enviosList.add(envio)
            } catch (ex: Exception) {
                Log.e("InicioActivity", "procesarEnvios: Error procesando envío en índice $i", ex)
            }
        }
        return enviosList
    }



    private fun actualizarEstatusEnvios() {
        var completados = 0
        val total = enviosList.size

        for (envio in enviosList) {
            Ion.with(this)
                .load("GET", "${Constantes().URL_WS}/envios/consultar-estado/${envio.idEnvio}")
                .asString(Charset.forName("UTF-8"))
                .setCallback { e, result ->
                    if (e == null) {
                        try {
                            val jsonObject = JSONObject(result)
                            val objetoValue = jsonObject.getJSONObject("objeto").getString("value")
                            val objetoInterno = JSONObject(objetoValue)
                            envio.estatus = objetoInterno.getString("estado")
                        } catch (ex: Exception) {
                            Log.e("InicioActivity", "actualizarEstatusEnvios: Error al procesar estatus del envío", ex)
                        }
                    } else {
                        Log.e("InicioActivity", "actualizarEstatusEnvios: Error en la solicitud", e)
                    }

                    completados++
                    if (completados == total) {
                        obtenerPaquetesParaEnvios(enviosList)
                    }
                }
        }
    }


    private fun obtenerPaquetesParaEnvios(enviosList: List<Envio>) {
        Log.d("InicioActivity", "obtenerPaquetesParaEnvios: Obteniendo paquetes para los envíos")

        var completados = 0
        val total = enviosList.size

        for (envio in enviosList) {
            Ion.with(this)
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
                                Log.d("InicioActivity", "obtenerPaquetesParaEnvios: Paquetes asignados a envío ${envio.idEnvio}")
                            }
                        } catch (ex: Exception) {
                            Log.e("InicioActivity", "obtenerPaquetesParaEnvios: Error procesando paquetes para el envío ${envio.idEnvio}", ex)
                        }
                    } else {
                        Log.e("InicioActivity", "obtenerPaquetesParaEnvios: Error en la solicitud para el envío ${envio.idEnvio}", e)
                    }

                    completados++
                    if (completados == total) {
                        Toast.makeText(this, "Datos recuperados correctamente", Toast.LENGTH_LONG).show()
                        when (inicio) {
                            "InicioLogin" -> openInicioFragmentBorrar()
                            "Inicio" -> openInicioFragment()
                            "Perfil" -> openPerfilFragment()
                            else -> {
                                // Opcional: Manejar el caso si no se recibe ningún valor o si el valor no es reconocido
                                Log.w("InicioActivity", "Pantalla desconocida: $inicio")
                            }
                        }
                    }
                }
        }
    }

}

