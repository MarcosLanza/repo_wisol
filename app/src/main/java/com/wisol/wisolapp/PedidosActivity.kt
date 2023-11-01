package com.wisol.wisolapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileReader


class PedidosActivity : AppCompatActivity() {


    var idPodido = ""
    var idCliente = ""
    var estadoP = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedidos)

        val btnBack = findViewById<Button>(R.id.btnBackPedido)
        btnBack.setOnClickListener { navigateToBackWelcome() }

        val btnDelete = findViewById<FloatingActionButton>(R.id.fabDelete)
        btnDelete.setOnClickListener { deletePedido() }

        val btnEdit = findViewById<FloatingActionButton>(R.id.fabEdit)
        btnEdit.setOnClickListener { editPedido() }

        val btnPedidosNew = findViewById<Button>(R.id.btnNewPedido)
        btnPedidosNew.setOnClickListener { navigateToSlectionClient() }

        val btnSubitDrive = findViewById<FloatingActionButton>(R.id.fabUpdate)
        btnSubitDrive.setOnClickListener { changestate() }

        geto()




    }


    private fun navigateToBackWelcome(){
        val intent = Intent(this, InicioActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToSlectionClient() {
        val intent = Intent(this, SeleccionClienteActivity::class.java)
        startActivity(intent)


    }
    private fun editPedido(){
        if(idPodido!="" && estadoP == "abierto"){
            val intent = Intent(this, SeleccionProductoActivity::class.java)
            intent.putExtra("ID_Pedido", idPodido)
            intent.putExtra("ID_Client", idCliente)

            startActivity(intent)
        }

    }
    private fun changestate(){
        val rutaArchivo = File(applicationContext.filesDir, "pedidos.txt")

        try {
            if (rutaArchivo.exists()) {
                // Lee el archivo JSON existente y conviértelo a una lista de objetos JSON
                val contenido = rutaArchivo.readText()
                val jsonArray = JSONArray(contenido)

                // Modifica el atributo "estado" a "cerrado" en todos los objetos
                for (i in 0 until jsonArray.length()) {
                    val pedidoJson = jsonArray.getJSONObject(i)
                    pedidoJson.put("estado", "cerrado")
                }

                // Escribe el JSONArray actualizado en el archivo
                rutaArchivo.writeText(jsonArray.toString())

                println("Estado de todos los pedidos actualizado a 'cerrado' correctamente.")
            } else {
                println("El archivo no existe.")
            }
        } catch (e: Exception) {
            println("Error al leer o escribir en el archivo: ${e.message}")
        }
        val intent = Intent(this, PedidosActivity::class.java)
        startActivity(intent)

    }



    private fun deletePedido(){
        if (idPodido != ""){
            println("Hello Ericka"+idPodido)
            deletePeidoTxt()
            geto()
        }
        else{
            println("BYE Ericka"+idPodido)

        }
    }
    fun obtenerIdPe(idPedido:String, idClient:String, estado:String){
        idPodido = idPedido
        idCliente = idClient
        estadoP = estado
    }

    private fun deletePeidoTxt(){
        val rutaArchivo = File(applicationContext.filesDir, "pedidos.txt")

        // Verificar si el archivo existe
        if (!rutaArchivo.exists()) {
            println("El archivo no existe.")
            return
        }

        // Leer el contenido del archivo TXT
        val contenido = rutaArchivo.readText()

        try {
            // Convertir el contenido JSON a una lista de objetos
            val listaDeObjetos = JSONArray(contenido)

            // ID del pedido que deseas eliminar
            val idPedidoAEliminar = idPodido // Reemplaza con el ID que necesitas eliminar

            // Eliminar objetos con el ID deseado
            val objetosFiltrados = JSONArray()
            for (i in 0 until listaDeObjetos.length()) {
                val objeto = listaDeObjetos.getJSONObject(i)
                val idPedido = objeto.getString("idPedido")

                if (idPedido != idPedidoAEliminar) {
                    objetosFiltrados.put(objeto)
                }
            }

            // Convertir la lista de objetos filtrados a JSON
            val nuevoContenido = objetosFiltrados.toString()

            // Escribir el nuevo contenido en el archivo TXT
            rutaArchivo.writeText(nuevoContenido)

            println("Objeto con ID $idPedidoAEliminar eliminado exitosamente.")

            if (objetosFiltrados.length() == 0) {
                // El archivo está vacío después de eliminar, inicia una nueva actividad
                finish()
                startActivity(intent)
            } else {
                // El archivo aún tiene elementos, notificar cambios al adaptador
                val recycler: RecyclerView = findViewById(R.id.recyclerView)
                val adapter: RecyclerViewAdapter = recycler.adapter as RecyclerViewAdapter
                adapter.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            println("Error al procesar el archivo: ${e.message}")
        }

    }




    private fun geto(){
        val rutaArchivo = File(applicationContext.filesDir, "pedidos.txt")

        try {
            if (rutaArchivo.exists()) {
                val lector = BufferedReader(FileReader(rutaArchivo))
                val contenido = lector.readText()
                lector.close()

                // Parsea el contenido como un JSONArray
                val jsonArray = JSONArray(contenido)

                // Crea una lista de pedidos
                val arrayList: MutableList<PedidosModel> = ArrayList()
                var arrayListN: MutableList<PedidosModel> = ArrayList()



                // Itera a través de los objetos JSON y crea manualmente los objetos Pedido
                for (i in 0 until jsonArray.length()) {
                    val pedido: JSONObject = jsonArray.getJSONObject(i)
                    val numPedido = pedido.getString( "numPedido")
                    val vendedor = pedido.getString("vendedor")
                    val id_cliente = pedido.getString("id_cliente")
                    val desc_cliente = pedido.getString("desc_cliente")
                    val desc_producto = pedido.getString("desc_producto")
                    val marca = pedido.getString("marca")
                    val tipo = pedido.getString("tipo")
                    val precio = pedido.getString("precio")
                    val cnt = pedido.getString("cnt")
                    val estado = pedido.getString("estado")
                    val fecha = pedido.getString("fecha")
                    val codigo_producto = pedido.getString("codigo_producto")
                    val comentario = pedido.getString("comentario")
                    val idPedido = pedido.getString("idPedido")
                    val precioT = pedido.getString("precioTotal")
                    val bono = pedido.getString("bonoT")
                    arrayList.add(PedidosModel(numPedido = numPedido,
                        vendedor = vendedor,
                        id_cliente = id_cliente,
                        desc_cliente = desc_cliente,
                        desc_producto = desc_producto,
                        marca = marca,
                        tipo = tipo,
                        precio = precio,
                        cnt = cnt,
                        estado = estado,
                        fecha = fecha,
                        codigo_producto = codigo_producto,
                        comentario = comentario,
                        idPedido = idPedido,
                        precioT = precioT,
                        bonoT = bono))
                    arrayListN = arrayList.distinctBy { it.idPedido }.toMutableList()
                    val recycler : RecyclerView = findViewById(R.id.recyclerView)
                    val adapter : RecyclerViewAdapter = RecyclerViewAdapter()

                    adapter.RecyclerViewAdapter(arrayListN, this)

                    recycler.hasFixedSize()
                    recycler.adapter = adapter

                    recycler.layoutManager = LinearLayoutManager(this)
                }
                println("eso papus "+ arrayList)

            } else {
                println("El archivo no existe.")
            }
        } catch (e: Exception) {
            println("Error al leer el archivo: ${e.message}")
        }
    }




}