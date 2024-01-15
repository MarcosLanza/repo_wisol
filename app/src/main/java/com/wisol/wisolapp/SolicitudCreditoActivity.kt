package com.wisol.wisolapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.Camera
import android.os.Bundle
import android.text.Editable
import android.util.Base64
import android.util.Patterns
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
import java.util.Calendar

class SolicitudCreditoActivity : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 1  // Código de solicitud para la captura de imágenes
    private var contador = 0
    private var a = 0
    private var b = 0
    private var c = 0
    private var e = 0
    private var f = 0
    private var nameClient = ""
    private val imagenes = mutableListOf<ImageView>()










    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitud_credito)

        val img1: ImageView = findViewById(R.id.img1)
        val img2: ImageView = findViewById(R.id.img2)
        val img3: ImageView = findViewById(R.id.img3)

        imagenes.addAll(listOf(img1, img2, img3))

        for ((index, image) in imagenes.withIndex()) {
            image.setOnClickListener {
                // Llamas directamente a la función para tomar la foto en lugar de llamar a onActivityResult
                tomarFoto(index + 1)
            }
        }


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


        val email = correoElectronico.text
        val emailA = facturacionCorreoElectronico.text






        val buttonTomarFoto = findViewById<Button>(R.id.btnAdjuntarImagen)
        val atras = findViewById<Button>(R.id.btnAtrasImagen)

        atras.setOnClickListener { backtras() }



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
                println("este es el correo ${correoElectronico.text}")
                if (isValidEmail(email)) {
                    // El formato del correo electrónico es válido
                    correoElectronico.error = null
                    e = 1
                    if(isValidEmail(emailA)){
                        facturacionCorreoElectronico.error = null
                        f = 1

                    }else
                    {
                        // El formato del correo electrónico no es válido
                        facturacionCorreoElectronico.error = "Correo electrónico no válido"
                        f = 0
                        facturacionCorreoElectronico.text = null

                    }

                }else
                {
                    // El formato del correo electrónico no es válido
                    correoElectronico.error = "Correo electrónico no válido"
                    if(isValidEmail(emailA)){
                        facturacionCorreoElectronico.error = null
                        f = 1

                    }else
                    {
                        f = 0
                        // El formato del correo electrónico no es válido
                        facturacionCorreoElectronico.error = "Correo electrónico no válido"

                    }
                }
                mostrarMensajeSubidaCompleta()
                println("lugar="+lugarFecha.text)

            } else {
                println("entre $f")

                nameClient = nombreDueno.text.toString()
                if (a == 1 && b == 1 && c ==1) {
                    println("confirmo")
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
                                subirImage() // Llamada a la función para subir la imagen

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
    private fun tomarFoto(indice: Int) {
        val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)

        if (intent.resolveActivity(packageManager) != null) {
            // Especifica que se debe usar la cámara trasera
            intent.putExtra("android.intent.extras.CAMERA_FACING", Camera.CameraInfo.CAMERA_FACING_BACK)
            intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 0)
            intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", false)

            // Asegúrate de que la orientación sea la correcta
            intent.putExtra("android.intent.extra.ROTATION", windowManager.defaultDisplay.rotation)

            // Inicia la actividad de captura de imágenes
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            contador = indice
        } else {
            println("No hay aplicación de cámara disponible.")
        }
    }

    private fun backtras() {
        val intent = Intent(this, InicioActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap?

            if (contador in 1..3 && imageBitmap != null) {
                imagenes[contador - 1].setImageBitmap(imageBitmap)
                when (contador) {
                    1 -> a = 1
                    2 -> b = 1
                    3 -> c = 1
                }
            }
        }
    }


    private fun isValidEmail(email: Editable?): Boolean {
        println("correo es este $email")
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()

    }
    private fun subirImage() {

        if (a == 1 && b== 1 && c == 1) {
            val calendario = Calendar.getInstance()
            val ano = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH) + 1
            val dia = calendario.get(Calendar.DAY_OF_MONTH)
            val diaFormateado = String.format("%02d", dia)
            val mesFormateado = String.format("%02d", mes)


            val scriptUrl =
                "https://script.google.com/macros/s/AKfycbwdem32q8MDn5w8U_GGmGiAHyEtTTnklVmvn5bSm8cpbc5Sj90kPQ66fmYAZjRKVLnD/exec?function=doPost"

            val jsonBody = JSONObject().apply {
                put("imagenes", JSONArray().apply {
                    val currentMillis = System.currentTimeMillis()

                    for ((index, imageView) in imagenes.withIndex()) {
                        val fileName =
                            "$nameClient-$ano$mesFormateado$diaFormateado-$index.jpg"
                        println("Imagen $index - Nombre: $fileName")

                        val base64Image =
                            convertBitmapToBase64(imageView.drawable.toBitmap())
                        println("Imagen $index - Base64: $base64Image")

                        put(JSONObject().apply {
                            put("base64", base64Image)
                            put("fileName", fileName)
                        })
                    }
                })
            }

            val requestBody =
                jsonBody.toString().toRequestBody("application/json".toMediaType())

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
                        backWelcome()
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
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            "Faltan campos por llenar",
            5000
        ) // Duración de 5 segundos
        val view = snackbar.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.CENTER
        view.layoutParams = params
        snackbar.show()
    }

    private fun backWelcome(){
        val intent = Intent(this, InicioActivity::class.java)
        startActivity(intent)
    }

}