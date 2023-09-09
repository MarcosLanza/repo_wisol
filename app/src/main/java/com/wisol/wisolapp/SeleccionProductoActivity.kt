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
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class SeleccionProductoActivity : AppCompatActivity() {

    var sheetInjasonUrl = "https://script.google.com/macros/s/AKfycbzxwZic2UbnCH5aSm2sId2QHinWrX4vASOmntQbWUzW9GC-JXFXQdUMWeHVxzCX93bH/exec?spreadsheetId=1QUEQ5jhqDUHOXstgC7i8uLuYXWD93swMdhzGGpyMmZM&sheet=productos_clientes"
    var LOG_TAG = "kiko"
    var contador = 0
    var idProducto: String? = null
    var selectedItemsA: MutableList<ProductosModel> = ArrayList() // Lista para rastrear elementos seleccionados



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccion_producto)
        getProducts()
        idProducto = intent.extras?.getString("ID_Producto").orEmpty()
        val btnSave = findViewById<Button>(R.id.btnSavePro)
        btnSave.setOnClickListener { navigateToSaveProduct() }



    }
    private fun navigateToSaveProduct(){
        if (contador == 1){

            val gson = GsonBuilder().setPrettyPrinting().create()
            val json = gson.toJson(selectedItemsA)

            val directorioInterno = applicationContext.filesDir
            val rutaArchivo = File(directorioInterno, "pedidos.txt")


            // Crea el archivo y escribe el JSON en él
            rutaArchivo.createNewFile()

            val escritor = FileWriter(rutaArchivo)
            escritor.use { escritor ->
                escritor.write(json)
            }

            println("Lista mutable guardada en ${rutaArchivo.absolutePath}")
            leerArchivo()
        }

        val intent = Intent(this, PedidosActivity::class.java)
        startActivity(intent)

    }

    fun save(selectedItems: MutableList<ProductosModel>) {
        println("Ya llegue "+selectedItems)
        contador = 1

        selectedItemsA = selectedItems



    }

    fun onEditTextChanged(editTextTag: String, newText: String) {
        // Manejar el cambio de texto aquí
        // Puedes utilizar el editTextTag para identificar el EditText específico
        // Puedes realizar acciones específicas según el tag y el nuevo texto
        Log.d("EditTextChange", "EditText con tag $editTextTag cambió a: $newText")
    }

    private fun leerArchivo(){
        val rutaArchivo = File(applicationContext.filesDir, "pedidos.txt")

        try {
            // Verificar si el archivo existe
            if (rutaArchivo.exists()) {
                val lector = BufferedReader(FileReader(rutaArchivo))
                val contenido = StringBuilder()
                var linea: String?

                // Leer el archivo línea por línea
                while (lector.readLine().also { linea = it } != null) {
                    contenido.append(linea).append("\n")
                }

                // Cerrar el lector
                lector.close()

                // Imprimir el contenido del archivo
                println("Contenido del archivo:")
                println(contenido.toString())
            } else {
                println("El archivo no existe.")
            }
        } catch (e: Exception) {
            println("Error al leer el archivo: ${e.message}")
        }
    }

    fun getProducts(){
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, sheetInjasonUrl, null,
            Response.Listener { response ->
                Log.i(LOG_TAG, "Response is: $response")

                val gson = Gson()
                val jsonObject: JsonObject = gson.fromJson<JsonObject>(response.toString(), JsonObject::class.java)
                val jsonArray: JSONArray = response.getJSONArray("clientes")
                val arrayList: MutableList<ProductosModel> = ArrayList()
                /*

                for (element: JsonElement in jsonObject){
                    Log.i(LOG_TAG, element.asJsonObject.toString())
                }*/
                for (i in 0 until jsonArray.length()) {
                    val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                    Log.i(LOG_TAG, jsonObject.toString())


                }
                for (i in 0 until jsonArray.length()) {
                    val producto: JSONObject = jsonArray.getJSONObject(i)
                    val idCliente = producto.getString("ID_CLIENTE")
                    val idProduc = producto.getString("ID_PRODUCTO")
                    val nameP = producto.getString("DESC_PRODUCTO")
                    val cnt = producto.getString("PRECIO")
                    val bono = producto.getString("BONO")
                    if (idCliente == idProducto){
                        arrayList.add(ProductosModel(idCliente = idCliente, idP = idProduc, nameP = nameP, cnt = cnt, bono = bono))

                        val recycler : RecyclerView = findViewById(R.id.recyclerViewP)
                        val adapter : RecyclerViewAdapterProductos = RecyclerViewAdapterProductos()


                        adapter.RecyclerViewAdapter(arrayList, this)

                        recycler.hasFixedSize()
                        recycler.adapter = adapter

                        recycler.layoutManager = LinearLayoutManager(this)

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