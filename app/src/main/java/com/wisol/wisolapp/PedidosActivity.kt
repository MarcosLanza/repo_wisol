package com.wisol.wisolapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject


class PedidosActivity : AppCompatActivity() {

    var SPREAD_SHEET_ID = "19rMYY91d0E_QsgwoddgCIv89omoEgwos4XHatRP5baA"
    var sheetInjasonUrl = "https://script.google.com/macros/s/AKfycbw_BeU3Z9548zeRHtvguZsLIU5gNCk2m5B45IojMhBKR3M6pLulrPskKTUMttWaV8ek/exec?spreadsheetId=1QUEQ5jhqDUHOXstgC7i8uLuYXWD93swMdhzGGpyMmZM&sheet=pedidos"

    var LOG_TAG = "kikopalomares"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedidos)

        val btnBack = findViewById<Button>(R.id.btnBackPedido)
        btnBack.setOnClickListener { navigateToBackWelcome() }

        val btnPedidosNew = findViewById<Button>(R.id.btnNewPedido)
        btnPedidosNew.setOnClickListener { navigateToSlectionClient() }

        getUsers()



    }


    private fun navigateToBackWelcome(){
        val intent = Intent(this, InicioActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToSlectionClient() {
        val intent = Intent(this, SeleccionClienteActivity::class.java)
        startActivity(intent)


    }



    /**
     * Obtenemos los datos de la hoja de users
     */
    fun getUsers(){
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, sheetInjasonUrl, null,
            Response.Listener { response ->
                Log.i(LOG_TAG, "Response is: $response")

                val gson = Gson()
                val jsonObject: JsonObject = gson.fromJson<JsonObject>(response.toString(), JsonObject::class.java)
                val jsonArray: JSONArray = response.getJSONArray("pedidos")
                val arrayList: MutableList<PedidosModel> = ArrayList()
                /*

                for (element: JsonElement in jsonObject){
                    Log.i(LOG_TAG, element.asJsonObject.toString())
                }*/
                for (i in 0 until jsonArray.length()) {
                    val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                    Log.i(LOG_TAG, jsonObject.toString())


                }
                for (i in 0 until jsonArray.length()) {
                    val pedido: JSONObject = jsonArray.getJSONObject(i)
                    val usuario = pedido.getString("Usuario")
                    val estado = pedido.getString("estado")
                    val id = pedido.getString("id")

                    arrayList.add(PedidosModel(id = id,usuario = usuario, estado = estado))

                    val recycler : RecyclerView = findViewById(R.id.recyclerView)
                    val adapter : RecyclerViewAdapter = RecyclerViewAdapter()

                    adapter.RecyclerViewAdapter(arrayList, this)

                    recycler.hasFixedSize()
                    recycler.adapter = adapter

                    recycler.layoutManager = LinearLayoutManager(this)
                }




            },
            Response.ErrorListener { error ->
                error.printStackTrace()
                Log.e(LOG_TAG, "That didn't work!")
            }
        )
        queue.add(jsonObjectRequest)
    }




}