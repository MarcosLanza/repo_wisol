package com.wisol.wisolapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException

class SolicitudCreditoActivity : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 1  // Código de solicitud para la selección de imágenes
    private val REQUEST_IMAGE_CAPTURE = 1  // Código de solicitud para la captura de imágenes
    private lateinit var img1: ImageView
    private lateinit var img2: ImageView
    private lateinit var img3: ImageView
    private var contador = 0
    private var a = 0
    private var b = 0
    private var c = 0
    private var d = 0








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
                mostrarMensajeSubidaCompleta()
                println("lugar="+lugarFecha.text)

            } else {
                d = 1
                if (a == 1 && b == 1 && c ==1 && d == 1) {
                    subirImage()
                    println("lugar="+lugarFecha)
                    val scriptUrl = "https://script.google.com/macros/s/AKfycbzy8NU8JJAihzndD8EaqjGO6BZc6SWUZ6FiUV4_BmJ2lNTNGo-0Wlyz2jOKwm_mg8B4/exec?function=doPost&nombreArchivo=CLIENTES.csv"

                    val client = OkHttpClient()

                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            val jsonData = """
                            {
                               "nombreArchivo": "CLIENTES.csv",
                               "nuevosDatos": ["${lugarFecha.text};${nombreEmpresa.text};${nombreDueno.text};${cedulaJuridica.text};${cedulaIdentidad.text};${direccionExacta.text};${nombreFantasia.text};${provincia.text};${canton.text};${distrito.text};${telefonoNegocio.text};${telefonoDueno.text};${correoElectronico.text};${facturacionCorreoElectronico.text}"]
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


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            if (contador == 1) {
                img1.setImageBitmap(imageBitmap)
                b = 1
            } else if (contador == 2) {
                img2.setImageBitmap(imageBitmap)
                a = 1
            } else if (contador == 3) {
                img3.setImageBitmap(imageBitmap)
                c = 1
            }

            subirImage() // Llamada a la función para subir la imagen
        }
    }
    private fun subirImage() {
        if (a == 1 && b == 1 && c ==1 && d == 1) {
            val scriptUrl = "https://script.google.com/macros/s/AKfycbzJmkF8nuqI__BGe1VRPoMZf8iPf65n7ZaFbAdwxnK456H-ax3AiDcK1iWRKBMYgyHJRQ/exec?function=doPost"

            val jsonBody = JSONObject().apply {
                put("imagenes", JSONArray().apply {
                    put(JSONObject().apply {
                        put("base64", convertBitmapToBase64(img1.drawable.toBitmap()))
                        put("fileName", "imagen_${System.currentTimeMillis()}_1.jpg")
                    })
                    put(JSONObject().apply {
                        put("base64", convertBitmapToBase64(img2.drawable.toBitmap()))
                        put("fileName", "imagen_${System.currentTimeMillis()}_2.jpg")
                    })
                    put(JSONObject().apply {
                        put("base64", convertBitmapToBase64(img3.drawable.toBitmap()))
                        put("fileName", "imagen_${System.currentTimeMillis()}_3.jpg")
                    })
                })
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())

            val client = OkHttpClient()
            val request = Request.Builder()
                .url(scriptUrl)
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        println("Respuesta del servidor: $responseBody")
                    } else {
                        println("Error en la solicitud HTTP: ${response.code} - ${response.message}")
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    println("Error en la solicitud HTTP: ${e.message}")
                }
            })
        }
    }

    private fun convertBitmapToBase64(bitmap: Bitmap): String? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun mostrarMensajeSubidaCompleta() {
        val snackbar = Snackbar.make(findViewById(android.R.id.content), "Faltan campos por llenar", 5000) // Duración de 5 segundos
        val view = snackbar.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.CENTER
        view.layoutParams = params
        snackbar.show()
    }

}