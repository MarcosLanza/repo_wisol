package com.wisol.wisolapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject

class SeleccionClienteActivity : AppCompatActivity() {
    var sheetInjasonUrl = "https://script.google.com/macros/s/AKfycbzxwZic2UbnCH5aSm2sId2QHinWrX4vASOmntQbWUzW9GC-JXFXQdUMWeHVxzCX93bH/exec?spreadsheetId=1QUEQ5jhqDUHOXstgC7i8uLuYXWD93swMdhzGGpyMmZM&sheet=productos_clientes"
    var LOG_TAG = "kiko"

    var selected:String? = null
    var filtro: String? = null
    var idProducto: String? = null


    private lateinit var editText: TextInputEditText
    private lateinit var editCodigo: TextInputEditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccion_cliente)

        val btnBackPedidos = findViewById<Button>(R.id.btnBack)
        btnBackPedidos.setOnClickListener { navigateToBackPedido() }

        val btnNext = findViewById<Button>(R.id.btnNext)
        btnNext.setOnClickListener { navigateToProductos() }

        val inputLayout = findViewById<TextInputLayout>(R.id.etFilterName)
        editText = findViewById<TextInputEditText>(R.id.inputFilterName)
        editCodigo = findViewById<TextInputEditText>(R.id.inputFilterCodigo)






        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No se utiliza en este caso
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Obtener el texto ingresado y dividirlo en palabras
                val text = s?.toString() ?: ""
                val words = text.split("\\s+".toRegex()) // Dividir por espacios en blanco
                // Aquí puedes hacer lo que necesites con las palabras capturadas
                // Por ejemplo, imprimir cada palabra
                words.forEachIndexed { index, word ->

                    filtro = text
                    println(filtro)

                }

                getClients()


            }

            override fun afterTextChanged(s: Editable?) {
                // No se utiliza en este caso
            }
        })


        init()



    }
    private fun navigateToBackPedido(){
        val intent = Intent(this, PedidosActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToProductos() {
        val intent = Intent(this, SeleccionProductoActivity::class.java)
        intent.putExtra("ID_Producto", idProducto)
        startActivity(intent)


    }

    private fun init(){
        getClients()
    }
    fun click(ola:String?, codigo:String?){

        println("Hola Paloma"+ola)
        selected = ola
        idProducto = codigo
        editText.setText(selected)
        editCodigo.setText(codigo)
        filtro = selected
        getClients()

    }

    fun getClients(){
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, sheetInjasonUrl, null,
            Response.Listener { response ->
                Log.i(LOG_TAG, "Response is: $response")

                val gson = Gson()
                val jsonObject: JsonObject = gson.fromJson<JsonObject>(response.toString(), JsonObject::class.java)
                val jsonArray: JSONArray = response.getJSONArray("clientes")
                var arrayList: MutableList<ClientesProductosModel> = ArrayList()
                val arrayListA: MutableList<ClientesProductosModel> = ArrayList()
                /*

                for (element: JsonElement in jsonObject){
                    Log.i(LOG_TAG, element.asJsonObject.toString())
                }
                for (i in 0 until jsonArray.length()) {
                    val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                    Log.i(LOG_TAG, jsonObject.toString())


                }*/
                if (selected == null && filtro == null){
                    for (i in 0 until jsonArray.length()) {
                        val clienteP: JSONObject = jsonArray.getJSONObject(i)
                        val cliente = clienteP.getString("DESC_CLIENTE")
                        val id = clienteP.getString("ID_CLIENTE")
                        val codigo = clienteP.getString("VENDEDOR")


                        arrayList.add(ClientesProductosModel(id = id, cliente = cliente, codigo = codigo))



                        val recycler : RecyclerView = findViewById(R.id.recyclerViewC)
                        val adapter : RecyclerViewAdapterClientes = RecyclerViewAdapterClientes()
                        adapter.SeleccionClienteActivity = this


                        adapter.RecyclerViewAdapterP(arrayList, this)

                        recycler.hasFixedSize()
                        recycler.adapter = adapter

                        recycler.layoutManager = LinearLayoutManager(this)
                    }



                }
                else if (filtro != null){
                    var id: String?
                    var cliente: String?
                    var codigo: String?

                    for (i in 0 until jsonArray.length()) {
                        val clienteP: JSONObject = jsonArray.getJSONObject(i)
                        cliente = clienteP.getString("DESC_CLIENTE")
                        id = clienteP.getString("ID_CLIENTE")
                        codigo = clienteP.getString("VENDEDOR")
                        arrayList.add(ClientesProductosModel(id = id, cliente = cliente, codigo = codigo))

                        }
                    val listaFiltrada = arrayList.filter { pruducto -> pruducto.cliente.contains(filtro.toString()) }
                    val recycler : RecyclerView = findViewById(R.id.recyclerViewC)
                    val adapter : RecyclerViewAdapterClientes = RecyclerViewAdapterClientes()
                    adapter.SeleccionClienteActivity = this

                    arrayList = listaFiltrada.toMutableList()
                    println("lista "+arrayList)


                    adapter.updatelista(arrayList, this)

                    recycler.hasFixedSize()
                    recycler.adapter = adapter

                    recycler.layoutManager = LinearLayoutManager(this)



                    }

                else if (selected != null){
                    println("entre ")
                    for (i in 0 until jsonArray.length()) {
                        val clienteP: JSONObject = jsonArray.getJSONObject(i)
                        val cliente = clienteP.getString("DESC_CLIENTE")
                        val id = clienteP.getString("ID_CLIENTE")
                        val codigo = clienteP.getString("VENDEDOR")
                        val clie = cliente


                        if (selected == cliente){
                            arrayList.add(ClientesProductosModel(id = id, cliente = clie , codigo = codigo))
                            println(arrayList)

                            val recycler : RecyclerView = findViewById(R.id.recyclerViewC)
                            val adapter : RecyclerViewAdapterClientes = RecyclerViewAdapterClientes()
                            adapter.SeleccionClienteActivity = this


                            adapter.RecyclerViewAdapterP(arrayList, this)

                            recycler.hasFixedSize()
                            recycler.adapter = adapter

                            recycler.layoutManager = LinearLayoutManager(this)

                        }


                    }



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