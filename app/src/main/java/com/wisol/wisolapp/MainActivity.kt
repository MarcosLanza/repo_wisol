package com.wisol.wisolapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject


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


            getUsers()


        }


    }


    private fun navigateToIncio() {
        val intent = Intent(this, InicioActivity::class.java)
        getUsers()
        startActivity(intent)


    }
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

    }



}

