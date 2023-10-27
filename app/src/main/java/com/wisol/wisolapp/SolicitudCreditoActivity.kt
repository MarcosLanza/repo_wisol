package com.wisol.wisolapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

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