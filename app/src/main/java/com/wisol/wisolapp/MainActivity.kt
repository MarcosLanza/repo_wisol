package com.wisol.wisolapp

//import com.android.volley.Request

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.opencsv.CSVReader
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.FileReader
import java.io.IOException
import java.security.MessageDigest
import java.util.Calendar


class MainActivity : AppCompatActivity() {

    var nombreBuscado = ""
    var passwordBuscada = ""
    var passwordCodificado = ""
    var idUsuario : String? = ""
    var rol : String? = ""
    val usersList = mutableListOf<UsersModel>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnStart = findViewById<Button>(R.id.btnStart)
        val inputName = findViewById<TextInputEditText>(R.id.inputUser)
        val inputPass = findViewById<TextInputEditText>(R.id.inputPass)
        loco()


        btnStart.setOnClickListener {
            nombreBuscado = inputName.text.toString()
            passwordBuscada = inputPass.text.toString()
            passwordCodificado = sha256(passwordBuscada)
            conexionInternet()

        }


    }
    private fun conexionInternet(){
        if (isNetworkAvailable(this)) {
            println("hay internet")
            realizarSolicitudHTTP()

        } else {
            // No hay conexión a Internet
            println("no hay internet")
            leercsvUser()
            login()

        }
    }
    private fun login(){
        for (use in usersList){
            if (use.usuario == nombreBuscado && use.password == passwordCodificado){
                println("password es "+use.password)
                idUsuario = use.usuario
                rol = use.role
                println("Hola muchachos $use.usuario")
                inicioSeccion()
                navigateToIncio()
            }
            println("Bye Muchachos")
            println("password es "+use.password+" "+use.usuario)
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
    private fun sha256(text: String): String {
        val bytes = text.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)

        // Convierte el resultado en una representación hexadecimal
        val hexChars = CharArray(digest.size * 2)
        for (i in digest.indices) {
            val v = digest[i].toInt() and 0xFF
            hexChars[i * 2] = "0123456789ABCDEF"[v ushr 4]
            hexChars[i * 2 + 1] = "0123456789ABCDEF"[v and 0x0F]
        }

        return String(hexChars)
    }

    private fun leercsvUser(){
        val filePath = applicationContext.filesDir.absolutePath + "/usuarios.csv" // Ruta al archivo CSV


        try {
            val csvReader = CSVReader(FileReader(filePath))

            // Leer la primera línea (encabezados) y descartarla
            csvReader.readNext()

            var record: Array<String>?
            while (true) {
                record = csvReader.readNext()
                if (record == null) {
                    break
                }
                val user = UsersModel(
                    record[0],
                    record[1],
                    record[2],
                    record[3],
                    record[4],
                    record[5],
                    record[6],
                    record[7],
                    record[8],
                    record[9],
                    record[10]
                )
                usersList.add(user)
                println("Hola "+usersList)

            }
            csvReader.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }


    private fun navigateToIncio() {
        val intent = Intent(this, InicioActivity::class.java)
        intent.putExtra("ID_USUARIO", idUsuario)
        intent.putExtra("ROLE", rol)


        println("estoy mandando esto $idUsuario")
        startActivity(intent)

    }

    private fun realizarSolicitudHTTP() {
        var roto = 0
        val calendario = Calendar.getInstance()

        val ano = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH) + 1  // Los meses comienzan desde 0, por lo que sumamos 1
        val dia = calendario.get(Calendar.DAY_OF_MONTH)
        val diaFormateado = String.format("%02d", dia)
        val mesFormateado = String.format("%02d", mes)

        val fechaj = "_$ano$mesFormateado$diaFormateado.csv"
        println("fecha $fechaj")
        val url = "https://script.google.com/macros/s/AKfycbykgrf2MAkYOrlcgyupiA0laVPYM845yx0Tdj-qfFXcHeo7qwFNs_annyEzDwgY9UhF/exec?function=doGet&nombreArchivo=matrices_seguridad_usuarios$fechaj"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    // Procesar la respuesta JSON aquí
                    // responseBody contiene la respuesta JSON de la función doGet
                    println("XD   "+responseBody)

                    try {
                        // Convierte la respuesta JSON en un objeto JSONObject
                        val responseObject = JSONObject(responseBody)

                        // Verifica si el resultado es "Archivo encontrado"
                        if (responseObject.getString("resultado") == "Archivo encontrado") {
                            // Obtén el array "datosCSV" del JSON
                            val datosCSVArray = responseObject.getJSONArray("datosCSV")


                            // Crea una lista mutable para almacenar los datos CSV formateados
                            val datosCSVFormattedList: MutableList<List<String>> = mutableListOf()

                            // Itera a través de los elementos del array "datosCSV"
                            for (i in 0 until datosCSVArray.length()) {
                                val csvRowArray = datosCSVArray.getJSONArray(i)
                                val csvRowList: MutableList<String> = mutableListOf()

                                // Itera a través de los elementos de cada fila CSV
                                for (j in 0 until csvRowArray.length()) {
                                    val csvCellValue = csvRowArray.getString(j).trim() // Elimina espacios en blanco
                                    csvRowList.add(csvCellValue)
                                }

                                // Agrega la fila a la lista solo si no está vacía
                                if (csvRowList.isNotEmpty()) {
                                    datosCSVFormattedList.add(csvRowList)
                                }
                                for (fila in datosCSVFormattedList) {
                                    // Accede a cada valor por su índice
                                    val usuarioId = fila[0]
                                    val usuario = fila[1]
                                    val password = fila[2]
                                    val nombre = fila[3]
                                    val correo = fila[4]
                                    val funcionId = fila[5]
                                    val aplicacion = fila[6]
                                    val funcion = fila[7]
                                    val roleId = fila[8]
                                    val role = fila[9]
                                    val permiso = fila[10]
                                    println("esta es la password fila " +password+" "+passwordCodificado+" NAME"+nombreBuscado)
                                    if (usuario == nombreBuscado && password == passwordCodificado){
                                        println("password es "+password)
                                        idUsuario = usuario
                                        rol = roleId
                                        println("Hola muchachos $usuario")
                                        inicioSeccion()
                                        roto = 1
                                        navigateToIncio()

                                    }

                                    println("Bye Muchachos")
                                    println("password es "+password+" "+nombre)
                                    //navigateToIncio()

                                }



                                }

                            // Ahora tienes los datos CSV formateados en datosCSVFormattedList
                        } else {
                            // Maneja el caso en el que el resultado no sea "Archivo encontrado"
                        }
                    } catch (e: JSONException) {
                        // Maneja errores de análisis JSON
                        e.printStackTrace()
                    }

                    if (roto == 0){
                        mostrarMensajeInvalido()

                    }
                } else {
                    // Manejar errores de respuesta
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // Manejar errores de conexión
            }
        })
    }

    fun mostrarMensajeInvalido() {
        val rootView = findViewById<View>(android.R.id.content)
        Snackbar.make(rootView, "Contraseña o usuario inválido", Snackbar.LENGTH_LONG).show()
    }

    private fun inicioSeccion(){
        val sharedPreferences = getSharedPreferences("MiUsuario", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("miValor", "$idUsuario")
        println("hola inicio $idUsuario")
        editor.apply()


    }
    private fun loco(){
        val sharedPreferences = getSharedPreferences("MiPref", Context.MODE_PRIVATE)
        val valorRecuperado = sharedPreferences.getString("miValor", "")
        println("valor $valorRecuperado")
    }



}

