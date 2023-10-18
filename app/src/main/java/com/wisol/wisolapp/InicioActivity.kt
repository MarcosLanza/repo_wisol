package com.wisol.wisolapp

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.opencsv.CSVReader
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
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

class InicioActivity : AppCompatActivity() {

    var contador = 0

    val arrayListE: MutableList<PedidosModel> = ArrayList()
    private lateinit var btnCirculo: FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        val btnPedidos = findViewById<Button>(R.id.btnPedidos)
        btnPedidos.setOnClickListener { navigateToIncio() }
        val btnNewClient = findViewById<Button>(R.id.btnNewClient)
        val btnSincronizar = findViewById<Button>(R.id.btnSincronizar)
        val btnout = findViewById<Button>(R.id.btnOut)
        val btnSubirPediddo = findViewById<Button>(R.id.btnSubirPedido)
        btnCirculo = findViewById<FloatingActionButton>(R.id.flsincronizar)

        btnSubirPediddo.setOnClickListener { subirDrivePedido() }
        btnout.setOnClickListener { out() }
        btnSincronizar.setOnClickListener { sincronizar() }

        btnNewClient.setOnClickListener { navigateToNewClient() }
        geto()
        changeCOlorBtn()

    }
    private fun changeCOlorBtn(){
        val colorRojo = ColorStateList.valueOf(Color.RED)
        val colorVerde = ColorStateList.valueOf(Color.GREEN)



        for (lista in arrayListE){
            if (lista.estado == "abierto"){
                btnCirculo.backgroundTintList = colorRojo

            }else if(lista.estado == "cerrado"){
                btnCirculo.backgroundTintList = colorVerde
            }
        }
    }
    private fun navigateToIncio() {
        val intent = Intent(this, PedidosActivity::class.java)
        startActivity(intent)


    }

    private fun out() {
        val intent = Intent(this, SolicitudCreditoActivity::class.java)
        startActivity(intent)


    }

    private fun navigateToNewClient(){
        val intent = Intent(this, NewClientActivity::class.java)
        startActivity(intent)
    }

    private fun sincronizar(){
        sincronizarUsers()
        sincronizarProducts()
        //subirDrivePedido()
    }


    private fun sincronizarUsers(){
        val usuarios: MutableList<UsersModel> = mutableListOf()

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
                    println("XD   " + responseBody)

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
                            }

                            for (fila in datosCSVFormattedList) {
                                // Accede a cada valor por su índice
                                val usuarioId = fila[0]
                                val usuario = fila[1]
                                val password = fila[2]
                                val correo = fila[3]
                                val funcionId = fila[4]
                                val aplicacion = fila[5]
                                val funcion = fila[6]
                                val roleId = fila[7]
                                val permiso = fila[8]

                                val userModel = UsersModel(
                                    usuarioId,
                                    usuario,
                                    password,
                                    correo,
                                    funcionId,
                                    aplicacion,
                                    funcion,
                                    roleId,
                                    permiso
                                )

                                usuarios.add(userModel)
                            }

                            // De aquí en adelante, después de procesar todos los datos, crea el archivo CSV
                            createCsvUsers(usuarios)
                            println("Se pudo")

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

    private fun createCsvUsers(usuarios: MutableList<UsersModel>) {
        val fileName = "usuarios.csv" // Cambia esto al nombre de archivo deseado
        val directory = applicationContext.filesDir // Directorio interno de la aplicación
        val file = File(directory, fileName)
        try {
            val csvWriter = FileWriter(file)

            // Escribir encabezados
          //  csvWriter.append("usuarioId,usuario,password,correo,funcionId,aplicacion,funcion,roleId,permiso\n")

            // Escribir datos de usuarios
            for (user in usuarios) {
                csvWriter.append("${user.id},${user.usuario},${user.password},${user.correo},${user.funcionId},${user.aplicacion},${user.funcion},${user.rolId},${user.permiso}\n")
            }

            csvWriter.flush()
            csvWriter.close()

            println("Archivo CSV creado en: $file")
            leercsvUser()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun leercsvUser(){
        val filePath = applicationContext.filesDir.absolutePath + "/usuarios.csv" // Ruta al archivo CSV

        val usersList = mutableListOf<UsersModel>()

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
                    record[8]
                )
                usersList.add(user)

            }
            csvReader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun sincronizarProducts(){
        val productos: MutableList<ProductsModel> = mutableListOf()

        val url =
            "https://script.google.com/macros/s/AKfycbx614cu1R5lWtnrGcom7pDvY4guzPVWvWcYiHsKgM8R0tGZzMRg-B7SJ4J1lIzo1pdmCQ/exec?function=doGet&nombreArchivo=matrices_productos_precios_20230905_181449.csv"
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
                    println("XD   " + responseBody)

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
                            }

                            for (fila in datosCSVFormattedList) {
                                // Accede a cada valor por su índice
                                val vendedor = fila[0]
                                val idCliente = fila[1]
                                val descCliente = fila[2]
                                val idProducto = fila[3]
                                val descProducto = fila[4]
                                val marca = fila[5]
                                val tipo = fila[6]
                                val precio = fila[7]
                                val descuento = fila[8]
                                val tipoImpuesto = fila[9]
                                val tipoTarifa = fila[10]
                                val impuesto = fila[11]
                                val ventaTrim = fila[12]
                                val ventaAnt = fila[13]
                                val ventaAct = fila[14]
                                val bono = fila[15]
                                val minimo = fila[16]

                                val productModel = ProductsModel(
                                    vendedor,
                                    idCliente,
                                    descCliente,
                                    idProducto,
                                    descProducto,
                                    marca,
                                    tipo,
                                    precio,
                                    descuento,
                                    tipoImpuesto,
                                    tipoTarifa,
                                    impuesto,
                                    ventaTrim,
                                    ventaAnt,
                                    ventaAct,
                                    bono,
                                    minimo
                                )

                                productos.add(productModel)
                            }

                            // De aquí en adelante, después de procesar todos los datos, crea el archivo CSV
                            createProductCsv(productos)
                            println("Se pudo")

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
    private fun createProductCsv(productos: MutableList<ProductsModel>) {
        val fileName = "productos.csv"
        val directory = applicationContext.filesDir
        val file = File(directory, fileName)

        try {
            val csvWriter = FileWriter(file)


            // Escribir datos de productos
            for (product in productos) {
                val productFields = mutableListOf(
                    product.vendedor,
                    product.id_cliente,
                    product.desc_cliente,
                    product.id_producto,
                    product.desc_producto,
                    product.marca,
                    product.tipo,
                    product.precio,
                    product.descuento,
                    product.tipo_impuesto,
                    product.tipo_tarifa,
                    product.impuesto,
                    product.venta_trim,
                    product.venta_ant,
                    product.venta_act,
                    product.bono,
                    product.minimo
                )

                val productCsvRow = productFields.joinToString(",")
                csvWriter.append("$productCsvRow\n")
            }

            csvWriter.flush()
            csvWriter.close()

            println("Archivo CSV creado en: $file")
            leerproducts()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun leerproducts() {
        val filePath = applicationContext.filesDir.absolutePath + "/productos.csv" // Ruta al archivo CSV
        val productsList = mutableListOf<ProductsModel>()

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

                productsList.add(product)
            }
            println(productsList)

            csvReader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun subirDrivePedido(){
        val scriptUrl = "https://script.google.com/macros/s/AKfycbzKmqjG7A7ZjZIXsrCbGPmORumqjmmWQqbgObEd4f63VT5JwvoXMuGPGWZHRaSESNARvg/exec?function=doPost&nombreArchivo=1234_20230901.csv"

        for (lista in arrayListE){
            lista.vendedor
            println("este vendeor ${lista.estado}")
            if (lista.estado == "cerrado"){
                println("este vendeor cerrado ${lista.vendedor}")
                // Crear un objeto JSON con los datos que deseas enviar
                val jsonData = """
                {
                   "nombreArchivo": "1234_20230901.csv",
                   "nuevosDatos": ["${lista.numPedido};${lista.vendedor};${lista.id_cliente};${lista.desc_producto};${lista.marca};${lista.tipo};${lista.precio};${lista.cnt};${lista.estado};${lista.fecha};${lista.codigo_producto};${lista.comentario}"]
                }
                """.trimIndent()

                        // Convierte el objeto JSON a una cadena
                val jsonRequestBody = jsonData.toRequestBody("application/json".toMediaType())

                // Crea la solicitud POST
                val request = Request.Builder()
                    .url(scriptUrl)
                    .post(jsonRequestBody)
                    .build()

                // Crea un cliente OkHttp
                val client = OkHttpClient()

                // Utiliza una coroutine para realizar la solicitud en un hilo separado
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        // Ejecuta la solicitud de forma asíncrona
                        val response = client.newCall(request).execute()

                        // Verifica si la solicitud fue exitosa y maneja la respuesta
                        if (response.isSuccessful) {
                            val responseBody = response.body?.string()
                            // Maneja la respuesta del script de Google Apps aquí
                            println(responseBody)
                        } else {
                            // Maneja errores aquí
                            println("Error en la solicitud POST")
                        }
                    } catch (e: IOException) {
                        // Maneja excepciones de red aquí
                        println("Error de red: ${e.message}")
                    }
                }
            }
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

                    arrayListE.add(PedidosModel(
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
                        idPedido = idPedido))


                }
                println("eso papus "+ arrayListE)

            } else {
                println("El archivo no existe.")
            }
        } catch (e: Exception) {
            println("Error al leer el archivo: ${e.message}")
        }
    }



}