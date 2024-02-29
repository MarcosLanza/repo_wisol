package com.wisol.wisolapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.opencsv.CSVReader
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.lang.reflect.Type
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

class SeleccionProductoActivity : AppCompatActivity() {

    var contador = 0
    var contadorSave = 0
    var idPedido = ""
    var idPedidoFinal: String? = ""
    var idProducto: String? = null
    var contadora: String? = ""
    var change: String? = ""
    var changeA: String? = ""
    var comentarioB: String? = ""
    var user = ""
    var bako = 0
    var idClient = ""
    var selectedItemsA: MutableList<ProductosModel> = ArrayList() // Lista para rastrear elementos seleccionados
    var selectedItemsB: MutableList<ProductosModel> = ArrayList() // Lista para rastrear elementos seleccionados
    //esta lista es para ver los reportes
    var selectedItemsC: MutableList<ProductosModel> = ArrayList() // Lista para rastrear elementos seleccionados
    val arrayList: MutableList<ProductosModel> = ArrayList()

    private lateinit var promedioTrim:TextView
    private lateinit var mesAnterior:TextView
    private lateinit var mesActual:TextView
    private lateinit var precioP:TextView
    private lateinit var min:TextView
    private lateinit var cantidadBono:TextView
    private lateinit var recyclerViewAdapter: RecyclerViewAdapterProductos
    private lateinit var slogan:TextView





    var cntGlobal = 0
    var minimo = 0
    var preze:Double = 0.0

    val arrayListE: MutableList<PedidosModel> = ArrayList()

    private lateinit var editCodigo: TextInputEditText
    private lateinit var comentario: TextInputEditText
    var filtro: String? = null

    var textol: String? = null

    var comentarioC: String? = ""




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccion_producto)
        userUsu()
        idProducto = intent.extras?.getString("ID_Producto").orEmpty()
        idClient = intent.extras?.getString("ID_Client").orEmpty()
        idPedidoFinal = intent.extras?.getString("ID_Pedido").orEmpty()
        change = intent.extras?.getString("Cambio").orEmpty()
        comentarioC = intent.extras?.getString("Comentario").orEmpty()

        contadora = intent.extras?.getString("CONT").orEmpty()


        recyclerViewAdapter = RecyclerViewAdapterProductos()
        recyclerViewAdapter.RecyclerViewAdapter(arrayList, this)

        val recycler: RecyclerView = findViewById(R.id.recyclerViewP)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = recyclerViewAdapter


        promedioTrim = findViewById(R.id.tvPromedioTrim)
        mesAnterior = findViewById(R.id.tvMesAnteriror)
        mesActual = findViewById(R.id.tvMesActual)
        precioP = findViewById(R.id.tvPrecio)
        min = findViewById(R.id.tvMin)
        cantidadBono = findViewById(R.id.tvCantidadBono)
        slogan = findViewById(R.id.slogan)



        editCodigo = findViewById<TextInputEditText>(R.id.inputFilterCodigoProducto)

        comentario = findViewById(R.id.inputComentarioProductoA)
        leerproducts()
        editComentario()

        leercomentario()

        editCodigo.addTextChangedListener(object : TextWatcher {
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
                    println("Wenas "+filtro)

                }
                updateFiltro()

            }

            override fun afterTextChanged(s: Editable?) {
                // No se utiliza en este caso
            }
        })

        comentario.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Este método se llama antes de que el texto cambie
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Este método se llama cuando el texto está cambiando
                textol = s.toString()
                // Hacer lo que necesites con el nuevo texto
            }

            override fun afterTextChanged(s: Editable?) {
                // Este método se llama después de que el texto cambie
            }
            })


        val btnSave = findViewById<Button>(R.id.btnSavePro)
        btnSave.setOnClickListener { navigateToSaveProduct() }
        val btnBack = findViewById<Button>(R.id.btnBackPro)
        btnBack.setOnClickListener { backo() }

    }

    private fun userUsu(){
        val sharedPreferences = getSharedPreferences("MiUsuario", Context.MODE_PRIVATE)
        val valorRecuperado = sharedPreferences.getString("miValor", "")


        user = valorRecuperado.toString()


        println("valori $user")

    }

    private fun editComentario(){
        if (comentarioC != "null"){
            comentario.setText(comentarioC)
            println("hola comentarioC $comentarioC")
        }
    }

    private fun leercomentario() {
        if (comentarioB != ""){
            comentario.setText(comentarioB)

        }
    }

    fun updateFiltro(){
        recyclerViewAdapter.updateFiltro(filtro.toString())

    }
    private fun backo(){
        if (bako == 1){
            val intent = Intent(this, PedidosActivity::class.java)
            startActivity(intent)
        }else {
            val intent = Intent(this, SeleccionClienteActivity::class.java)
            startActivity(intent)
        }
    }

    private fun changeViewPro(){

        if (change == "si"){
            val intent = Intent(this, PedidosActivity::class.java)

            startActivity(intent)


        }else{
            val intent = Intent(this, SeleccionProductoB2Activity::class.java)
            intent.putExtra("ID_ProductoB", idProducto)
            intent.putExtra("ID_ClientB", idClient)
            intent.putExtra("ID_PedidoB", idPedidoFinal)
            val toy = comentario.text
            intent.putExtra("Comentarios", "$toy")

            println("hola comentario THE "+ toy)
            intent.putExtra("CambioB", changeA)
            startActivity(intent)
        }

    }
    private fun navigateToSaveProduct(){
        if (contador == 1){
            println("contsor save == $contadorSave")
            if (contadorSave == 0){
                 guardarPedido()
            }else{
                println("hola estoy editando")
                editarPedido()
            }
        }
            // Crea el archivo y escribe el JSON en él

      changeViewPro()

    }

    private fun guardarPedido(){
        val gson = Gson()
        for (lista in selectedItemsA){
            lista.comentario = comentario.text.toString()


        }
        selectedItemsA = selectedItemsA
        val json = gson.toJson(selectedItemsA)

        val directorioInterno = applicationContext.filesDir
        val rutaArchivo = File(directorioInterno, "pedidos_$user.txt")

        try {
            if (rutaArchivo.exists()) {
                // Leer contenido actual del archivo
                val lector = BufferedReader(FileReader(rutaArchivo))
                val contenidoActual = lector.readText()
                lector.close()

                val type: Type = object : TypeToken<MutableList<ProductosModel>>() {}.type
                selectedItemsB.addAll(gson.fromJson(contenidoActual, type))

            }

            selectedItemsB.addAll(selectedItemsA)
            val jsonActualizado = gson.toJson(selectedItemsB)
            val escritor = FileWriter(rutaArchivo)
            escritor.use { escritor ->
                escritor.write(jsonActualizado)
            }
            changeA = "si"


        } catch (e: IOException) {
            println("Error al manipular el archivo: ${e.message}")
        }
    }
    private fun editarPedido(){
        val rutaArchivo = File(applicationContext.filesDir, "pedidos_$user.txt")

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
            val idPedidoAEliminar = idPedido // Reemplaza con el ID que necesitas eliminar
            println("pedido a eliminar $idPedido")

            // Eliminar objetos con el ID deseado y tipo "DEVOLUCION"
            val objetosFiltrados = JSONArray()
            for (i in 0 until listaDeObjetos.length()) {
                val objeto = listaDeObjetos.getJSONObject(i)
                val idPedido = objeto.getString("idPedido")
                val tipo = objeto.getString("tipo")

                if (idPedido != idPedidoAEliminar || tipo != "TERMINADO") {
                    objetosFiltrados.put(objeto)
                    println("estos son los filtros: $objetosFiltrados")
                }
            }

            // Convertir la lista de objetos filtrados a JSON
            val nuevoContenido = objetosFiltrados.toString()

            // Escribir el nuevo contenido en el archivo TXT
            rutaArchivo.writeText(nuevoContenido)

            println("Objetos con ID $idPedidoAEliminar y tipo 'DEVOLUCION' eliminados exitosamente.")
            guardarPedido()
            contadorSave = 0

        } catch (e: Exception) {
            println("Error al procesar el archivo: ${e.message}")
        }
    }

    fun reportes(selectedItems: MutableList<ProductosModel>) {
        selectedItemsC = selectedItems
        if (selectedItemsC.isEmpty()){
            promedioTrim.text = "0"
            mesActual.text = "0"
            mesAnterior.text = "0"
          //  bonoR.text = "0"
            preze = 0.0
            precioP.text = "0"
        }
        else{
            var pro = selectedItemsC.last()
            promedioTrim.text = pro.venta_trim
            mesActual.text = pro.venta_act
            mesAnterior.text = pro.venta_ant
            minimo = pro.minimo.toInt()

            preze = pro.precio.toDouble()
            min.text = minimo.toString()
            cantidadBono.text = pro.bono
            updatePrecio()


        }

    }

    private fun updatePrecio(){
        val sumaTotal = selectedItemsC.sumByDouble { it.precioTotal.toDouble() }
        println("la suma total es: $sumaTotal")
        val formato = NumberFormat.getNumberInstance(Locale.getDefault())

        precioP.text = formato.format(sumaTotal)

    }

    // esta funcion guarda el producto
    fun save(selectedItems: MutableList<ProductosModel>) {
        val calendario = Calendar.getInstance()
        val hora = calendario.get(Calendar.HOUR_OF_DAY)
        val minutos = calendario.get(Calendar.MINUTE)
        val segundos = calendario.get(Calendar.SECOND)
        val año = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH) + 1  // Los meses comienzan desde 0, por lo que sumamos 1
        val dia = calendario.get(Calendar.DAY_OF_MONTH)
       contador = 1
        for (lista in selectedItems){
            if (idPedidoFinal == ""){
                lista.idPedido = "$hora$minutos$segundos"
                idPedidoFinal = "$hora$minutos$segundos"
                println("es nulo $idPedidoFinal")

            }else{
                lista.idPedido = "$idPedidoFinal"
                println("no, es nulo $idPedidoFinal")
            }
            lista.fecha = "$dia/$mes/$año"
            println("Hola comentaRIO"+textol)

        }
        selectedItemsA = selectedItems
    }


    fun onEditTextChanged(editTextTag: Int, newText: String) {
        // Manejar el cambio de texto aquí
        // Puedes utilizar el editTextTag para identificar el EditText específico
        // Puedes realizar acciones específicas según el tag y el nuevo texto
        Log.d("EditTextChange", "EditText con tag $editTextTag cambió a: $newText")
        if (newText!=""){
            println("hola")
            cntGlobal = newText.toInt()
            updatePrecio()
        }else{
            println("bye")
            cntGlobal = 0
        }
        //
    }

    //lee el archivo pedidos
    private fun leerproducts() {
        val filePath = applicationContext.filesDir.absolutePath + "/productos_$user.csv" // Ruta al archivo CSV

        try {
            CSVReader(FileReader(filePath)).use { csvReader ->
                // Leer la primera línea (encabezados) y descartarla
                csvReader.readNext()

                var record: Array<String>?
                while (true) {
                    record = csvReader.readNext()
                    if (record == null) {
                        break
                    }

                    val product = ProductsModel(
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
                        record[10],
                        record[11],
                        record[12],
                        record[13],
                        record[14],
                        record[15],
                        record[16]
                    )
                    if(product.tipo == "TERMINADO"){
                        if (product.id_cliente == idProducto && filtro == null) {
                            addProduct(arrayList, product)
                            slogan.text = product.desc_cliente
                        } else if (product.id_cliente == idClient && filtro == null) {
                            bako = 1
                            idProducto = null
                            addProduct(arrayList, product)

                            geto()
                            updateProductCounts(arrayList)

                            //comentario.setText(comentarioB)
                            slogan.text = product.desc_cliente


                            contador =1
                            contadorSave = 1
                        } else if (filtro != null) {
                           slogan.text = product.desc_cliente

                            println("entre al filtro $filtro")
                            if (product.desc_producto.contains(filtro!!, ignoreCase = true)) {
                                slogan.text = product.desc_cliente
                                addProduct(arrayList, product)
                            }
                        }

                    }

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun addProduct(arrayList: MutableList<ProductosModel>, product: ProductsModel) {
        arrayList.add(
            ProductosModel(
                id_cliente = product.id_cliente,
                id_producto = product.id_producto,
                desc_producto = product.desc_producto,
                cnt = 0.toString(),
                bono = product.bono,
                desc_cliente = product.desc_cliente,
                idPedido = "0",
                venta_trim = product.venta_trim,
                venta_ant = product.venta_ant,
                venta_act = product.venta_act,
                precio = product.precio,
                estado = "abierto",
                minimo = product.minimo,
                marca = product.marca,
                tipo_tarifa = product.tipo_tarifa,
                tipo_impuesto = product.tipo_impuesto,
                impuesto = product.impuesto,
                descuento = product.descuento,
                vendedor = product.vendedor,
                tipo = product.tipo,
                fecha = "",
                numPedido = "1",
                codigo_producto = product.id_producto,
                comentario = "",
                precioTotal = "0",
                bonoT = "0",


            )
        )
    }

    private fun updateProductCounts(arrayList: MutableList<ProductosModel>) {
        for (producto in arrayList) {
            for (productoB in arrayListE) {
                if (producto.id_producto == productoB.codigo_producto && producto.id_cliente == productoB.id_cliente) {
                    producto.cnt = productoB.cnt
                    producto.precioTotal = productoB.precioT
                    producto.bonoT = productoB.bonoT
                    comentarioB = productoB.comentario
                    println("comentario $comentarioB")


                }
            }
        }
    }
    private fun geto(){
        val rutaArchivo = File(applicationContext.filesDir, "pedidos_$user.txt")
        try {
            if (rutaArchivo.exists()) {
                val lector = BufferedReader(FileReader(rutaArchivo))
                val contenido = lector.readText()
                lector.close()

                // Parsea el contenido como un JSONArray
                val jsonArray = JSONArray(contenido)

                // Crea una lista de pedidos

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
                    val precioT = pedido.getString("precioTotal")
                    val bono = pedido.getString("bonoT")
                    val cnt = pedido.getString("cnt")
                    val estado = "abierto"
                    val fecha = pedido.getString("fecha")
                    val codigo_producto = pedido.getString("codigo_producto")
                    val comentario = pedido.getString("comentario")
                    idPedido = pedido.getString("idPedido")
                    if (idPedidoFinal == idPedido) {
                        arrayListE.add(
                            PedidosModel(
                                numPedido = numPedido,
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
                                bonoT = bono,
                            )
                        )
                    }


                }

            } else {
                println("El archivo no existe.")
            }
        } catch (e: Exception) {
            println("Error al leer el archivo: ${e.message}")
        }
    }



}