package uv.tc.timefastmobile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import uv.tc.timefastmobile.databinding.FragmentPerfilBinding
import com.google.gson.Gson
import uv.tc.timefastmobile.poko.Colaborador
import android.util.Base64
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlin.reflect.KMutableProperty0
import android.view.MotionEvent
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.koushikdutta.ion.Ion
import org.json.JSONObject
import uv.tc.timefastmobile.poko.Mensaje
import uv.tc.timefastmobile.util.Constantes
import java.io.ByteArrayOutputStream
import java.io.InputStream

class PerfilFragment : Fragment(R.layout.fragment_perfil) {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!
    private var fotoPerfilBytes: ByteArray ? = null
    private lateinit var colaborador: Colaborador

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.btnEditarPerfil.setOnClickListener {
            irPantallaEditarPerfil(colaborador)
        }

        binding.btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }

        // Obtener el colaborador desde los argumentos del fragmento
        val colaboradorJSON = arguments?.getString("colaborador")
        if (colaboradorJSON != null) {
            val gson = Gson()
            colaborador = gson.fromJson(colaboradorJSON, Colaborador::class.java)

            //mostrarDatosColaborador(colaborador)
        }
        obtenerFotoColaborador(colaborador.idColaborador)
        val nombre = "${colaborador.persona.nombre} ${colaborador.persona.apellidoPaterno} ${colaborador.persona.apellidoMaterno}"
        val correo = "${colaborador.persona.correo}"
        binding.tvNombreCompleto.text = nombre
        binding.tvCorreo.text = correo


        binding.btnCambiarFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            seleccionarFotoPerfil.launch(intent)
        }

    }

    fun quitarTintFotoPerfil() {
        binding.ivFotoPerfil.colorFilter = null // Elimina el tint aplicado al ImageView
    }

    fun obtenerFotoColaborador(idColaborador: Int) {
        Ion.with(requireContext())
            .load("GET", "${Constantes().URL_WS}/colaborador/obtener-foto/${idColaborador}")
            .asString()
            .setCallback { e, result ->
                if (e == null) {
                    try {
                        // Parsear la respuesta JSON
                        val jsonObject = JSONObject(result)
                        val error = jsonObject.getBoolean("error")
                        if (!error) {
                            // Extraer el objeto "objeto" y su "value"
                            val objeto = jsonObject.getJSONObject("objeto")
                            val fotoBase64 = objeto.getString("value")
                            cargarFotoColaborador(fotoBase64)
                        } else {
                            val mensaje = jsonObject.getString("mensaje")
                            Toast.makeText(requireContext(), "Error: $mensaje", Toast.LENGTH_LONG).show()
                        }
                    } catch (ex: Exception) {
                        Toast.makeText(requireContext(), "Error al procesar la respuesta: ${ex.message}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun cerrarSesion() {
        val intent = Intent(requireContext(), SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }


    fun cargarFotoColaborador(foto: String){
        if(foto.isNotEmpty()){
            val colaboradorFoto = foto
            if(colaboradorFoto != null){

                try {
                    val imgBytes = Base64.decode(colaboradorFoto,Base64.DEFAULT)
                    val imgBitMap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.size)
                    binding.ivFotoPerfil.setImageBitmap(imgBitMap)
                    quitarTintFotoPerfil()
                }catch (e: Exception){
                    Toast.makeText(requireContext(),
                        "Error img: "+ e.message,Toast.LENGTH_LONG).show()

                }

            }else{
                Toast.makeText(requireContext(), "No cuentas con foto de perfil", Toast.LENGTH_LONG).show()
            }
        }

    }


    //Implementación selección foto
    private val seleccionarFotoPerfil = this.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result : ActivityResult ->
        if(result.resultCode == Activity.RESULT_OK){
            val data = result.data
            val imgUri = data?.data
            if (imgUri != null){
                fotoPerfilBytes = uriToByteArray(imgUri)
                if(fotoPerfilBytes != null){
                    subirFotoPerfil(colaborador.idColaborador)
                }
            }
        }
    }

    private fun uriToByteArray(uri: Uri): ByteArray? {
        return try {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            byteArrayOutputStream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun subirFotoPerfil(idColaborador : Int){
        Ion.with(requireContext())
            .load("PUT","${Constantes().URL_WS}/colaborador/actualizar-foto/${idColaborador}")
            .setByteArrayBody(fotoPerfilBytes)
            .asString()
            .setCallback { e, result ->
                if(e == null){
                    val gson = Gson()
                    val msj = gson.fromJson(result, Mensaje::class.java)
                    Toast.makeText(requireContext(), msj.mensaje,Toast.LENGTH_LONG).show()
                    if(!msj.error){
                        obtenerFotoColaborador(colaborador.idColaborador)
                    }
                }else{
                    Toast.makeText(requireContext(), e.message,Toast.LENGTH_LONG).show()
                }
            }
    }




    private fun irPantallaEditarPerfil(colaborador: Colaborador) {
        val gson = Gson()
        val colaboradorJson = gson.toJson(colaborador)
        val intent = Intent(requireContext(), EditarPerfilActivity::class.java) // Usa requireContext() para obtener el contexto.
        intent.putExtra("colaborador", colaboradorJson)
        startActivity(intent)
    }

    private fun mostrarDatosColaborador(colaborador: Colaborador) {
        val mensaje = """
            Perfil Fragment:
            No. Personal: ${colaborador.noPersonal}
            Nombre: ${colaborador.persona.nombre} ${colaborador.persona.apellidoPaterno} ${colaborador.persona.apellidoMaterno}
            Rol: ${colaborador.rol.rol}
        """.trimIndent()
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
