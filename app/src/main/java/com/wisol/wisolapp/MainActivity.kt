package com.wisol.wisolapp

//import com.android.volley.Request

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class MainActivity : AppCompatActivity() {

    var SPREAD_SHEET_ID = "19rMYY91d0E_QsgwoddgCIv89omoEgwos4XHatRP5baA"
    var sheetInjasonUrl = "https://script.google.com/macros/s/AKfycbyTrI_UpxGxVEki9NPM-ZRkdG_kRr101alzMM78NkKfZjUvWgiGqLBrzpaBlxafsLLQ/exec?spreadsheetId=1QUEQ5jhqDUHOXstgC7i8uLuYXWD93swMdhzGGpyMmZM&sheet=matriz_seguridad_usuarios_20230818_172800"
    var TABLE_USERS = "personas"

    var LOG_TAG = "kikopalomares"

    var nombreBuscado = ""
    var passwordBuscada = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnStart = findViewById<Button>(R.id.btnStart)
        val inputName = findViewById<TextInputEditText>(R.id.inputUser)
        val inputPass = findViewById<TextInputEditText>(R.id.inputPass)

        btnStart.setOnClickListener {
            nombreBuscado = inputName.text.toString()
            println("Hola"+ nombreBuscado)
            passwordBuscada = inputPass.text.toString()
            realizarSolicitudHTTP()





        }


    }


    private fun navigateToIncio() {
        val intent = Intent(this, InicioActivity::class.java)
        startActivity(intent)

    }

    fun realizarSolicitudHTTP() {
        val url = "https://script.google.com/macros/s/AKfycbx614cu1R5lWtnrGcom7pDvY4guzPVWvWcYiHsKgM8R0tGZzMRg-B7SJ4J1lIzo1pdmCQ/exec?function=doGet&nombreArchivo=users.csv"
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
                                    if (usuario == nombreBuscado && password == passwordBuscada){
                                        println("Hola muchachos")
                                        navigateToIncio()
                                    }
                                    println("Bye Muchachos")
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


                } else {
                    // Manejar errores de respuesta
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // Manejar errores de conexión
            }
        })
    }
/*
    private fun getUsers() {
        val queue = Volley.newRequestQueue(this)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, sheetInjasonUrl, null,
            { response ->
                Log.i(LOG_TAG, "Response is: $response")

                val gson = Gson()
                val jsonObject: JsonObject= gson.fromJson(response.toString(), JsonObject::class.java)

                val personasArray: JSONArray = response.getJSONArray("personas")



                // Buscar en el array 'personas' por nombre y contraseña
                for (i in 0 until personasArray.length()) {
                    val persona: JSONObject = personasArray.getJSONObject(i)
                    val nombre = persona.getString("USUARIO")
                    val password = persona.getString("PASSWORD")

                    if (nombre == nombreBuscado && password == passwordBuscada) {
                        println("Nombre y contraseña válidos")
                        navigateToIncio()
                        nombreBuscado = ""
                        passwordBuscada = ""
                        // Realizar acciones adicionales si la validación es exitosa
                        break
                    }else{
                        // Si llegas aquí, no se encontró una coincidencia válida
                        println("Nombre o contraseña no válidos")

                        // Ahora tienes un objeto JSON (jsonObject) que contiene el arreglo JSON de "personas"
                    }
                }



            },
            { error ->
                error.printStackTrace()
                Log.e(LOG_TAG, "That didn't work!")
            }
        )

        queue.add(jsonObjectRequest)

    }*/



}

