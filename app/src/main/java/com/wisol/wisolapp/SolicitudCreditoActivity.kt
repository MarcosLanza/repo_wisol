package com.wisol.wisolapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class SolicitudCreditoActivity : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 1  // Código de solicitud para la selección de imágenes
    private val REQUEST_IMAGE_CAPTURE = 1  // Código de solicitud para la captura de imágenes
    private lateinit var img1: ImageView
    private lateinit var img2: ImageView
    private lateinit var img3: ImageView
    private var contador = 0





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitud_credito)

        img1 = findViewById(R.id.img1)
        img2 = findViewById(R.id.img2)
        img3 = findViewById(R.id.img3)

        val lugarFecha = findViewById<TextInputEditText>(R.id.inputLugarFecha)
        val nombreDueno = findViewById<TextInputEditText>(R.id.inputNombreDueño)
        val cedulaIdentidad = findViewById<TextInputEditText>(R.id.inputCedulaIdentidad)
        val nombreEmpresa = findViewById<TextInputEditText>(R.id.inputNombreEmpresa)
        val canton = findViewById<TextInputEditText>(R.id.inputCanton)
        val distrito = findViewById<TextInputEditText>(R.id.inputDistrito)
        val telefonoNegocio = findViewById<TextInputEditText>(R.id.inputTelefonoNegocio)
        val nombreFantasia = findViewById<TextInputEditText>(R.id.inputNombreFantasia)
        val cedulaJuridica = findViewById<TextInputEditText>(R.id.inputCedulaJuridica)
        val direccionExacta = findViewById<TextInputEditText>(R.id.inputDireccionExacta)
        val provincia = findViewById<TextInputEditText>(R.id.inputProvincia)
        val telefonoDueno = findViewById<TextInputEditText>(R.id.inputTelefonoDueño)
        val correoElectronico = findViewById<TextInputEditText>(R.id.inputCorreoElectronico)
        val facturacionCorreoElectronico = findViewById<TextInputEditText>(R.id.inputFacturacionCorreoElectronico)







        val buttonTomarFoto = findViewById<Button>(R.id.btnAdjuntarImagen)

        img1.setOnClickListener {   // Crea un Intent para abrir la cámara
            val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)

            // Comprueba si hay una actividad de cámara disponible
            if (intent.resolveActivity(packageManager) != null) {
                // Inicia la actividad de captura de imágenes
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                contador = 1
            }else{
                println("no hay camara")
            }
        }
        img2.setOnClickListener {   // Crea un Intent para abrir la cámara
            val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)

            // Comprueba si hay una actividad de cámara disponible
            if (intent.resolveActivity(packageManager) != null) {
                // Inicia la actividad de captura de imágenes
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                contador = 2
            }else{
                println("no hay camara")
            }
        }
        img3.setOnClickListener {   // Crea un Intent para abrir la cámara
            val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)

            // Comprueba si hay una actividad de cámara disponible
            if (intent.resolveActivity(packageManager) != null) {
                // Inicia la actividad de captura de imágenes
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                contador = 3
            }else{
                println("no hay camara")
            }
        }

        buttonTomarFoto.setOnClickListener {
            if (lugarFecha.text.isNullOrBlank() ||
                nombreDueno.text.isNullOrBlank() ||
                cedulaIdentidad.text.isNullOrBlank() ||
                nombreEmpresa.text.isNullOrBlank() ||
                canton.text.isNullOrBlank() ||
                distrito.text.isNullOrBlank() ||
                telefonoNegocio.text.isNullOrBlank() ||
                nombreFantasia.text.isNullOrBlank() ||
                cedulaJuridica.text.isNullOrBlank() ||
                direccionExacta.text.isNullOrBlank() ||
                provincia.text.isNullOrBlank() ||
                telefonoDueno.text.isNullOrBlank() ||
                correoElectronico.text.isNullOrBlank() ||
                facturacionCorreoElectronico.text.isNullOrBlank()) {
                // Al menos uno de los campos está vacío, realiza alguna acción aquí
                // Por ejemplo, muestra un mensaje de error o toma alguna otra acción.
                println("faltan cosas")
            } else {
                val scriptUrl = "https://script.google.com/macros/s/AKfycbzy8NU8JJAihzndD8EaqjGO6BZc6SWUZ6FiUV4_BmJ2lNTNGo-0Wlyz2jOKwm_mg8B4/exec?function=doPost&nombreArchivo=CLIENTES.csv"

                val client = OkHttpClient()

                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        val jsonData = """
                        {
                           "nombreArchivo": "CLIENTES.csv",
                           "nuevosDatos": ["${lugarFecha};${nombreEmpresa};
                           ${nombreDueno};
                           ${cedulaJuridica};
                           ${cedulaIdentidad};
                           ${direccionExacta};
                           ${nombreFantasia};
                           ${provincia};
                           ${canton};
                           ${distrito};
                           ${telefonoNegocio};
                           ${telefonoDueno};
                           ${correoElectronico};
                           ${facturacionCorreoElectronico}"]
                        }
                    """.trimIndent()

                        val jsonRequestBody = jsonData.toRequestBody("application/json".toMediaType())

                        val request = Request.Builder()
                            .url(scriptUrl)
                            .post(jsonRequestBody)
                            .build()

                        val response = client.newCall(request).execute()

                        if (response.isSuccessful) {
                            val responseBody = response.body?.string()
                            println("Elemento subido con éxito: $responseBody")
                        } else {
                            println("Error en la solicitud POST para el elemento: ")
                        }



                    } catch (e: IOException) {
                        println("Error de red: ${e.message}")
                    }
                }
            }
        }


    }
    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // La imagen seleccionada está en data.data
            val selectedImageUri = data.data
            // Puedes hacer algo con la URI de la imagen, como mostrarla en una ImageView o cargarla en tu aplicación.
        }
    }*/
    // Método que se llama cuando la captura de imágenes está completa
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            // La imagen capturada estará en data, puedes hacer algo con ella
            val imageBitmap = data?.extras?.get("data") as Bitmap

            if (contador == 1){
                img1.setImageBitmap(imageBitmap)
            }else if(contador == 2){
                img2.setImageBitmap(imageBitmap)

            }else if(contador == 3){
                img3.setImageBitmap(imageBitmap)

            }
            // Puedes mostrar la imagen en una ImageView o realizar otras acciones en tu aplicación.
        }
    }

}