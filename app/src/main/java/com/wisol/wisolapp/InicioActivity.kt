package com.wisol.wisolapp

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
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
import java.util.Calendar

// Otras importaciones


class InicioActivity : AppCompatActivity() {

    var contador = 0
    var usu: String? = ""
    var rol: String? = ""
    var usuarioGit: String? = ""






    val arrayListE: MutableList<PedidosModel> = ArrayList()
    private lateinit var btnCirculo: FloatingActionButton
    private lateinit var btnPedidos: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        usu = intent.extras?.getString("ID_USUARIO").orEmpty()
        rol = intent.extras?.getString("ROLE").orEmpty()

        userUsu()

        btnPedidos = findViewById(R.id.btnPedidos)
        btnPedidos.setOnClickListener { navigateToIncio() }
        val btnNewClient = findViewById<Button>(R.id.btnNewClient)
        val btnSincronizar = findViewById<Button>(R.id.btnSincronizar)
        val btnout = findViewById<Button>(R.id.btnOut)
        val btnSubirPediddo = findViewById<Button>(R.id.btnSubirPedido)
        btnCirculo = findViewById(R.id.flsincronizar)

        btnSubirPediddo.setOnClickListener { subirDrivePedido() }
        btnout.setOnClickListener { cerrarSeccion() }
        btnSincronizar.setOnClickListener { sincronizar() }

        btnNewClient.setOnClickListener { navigateToNewClient() }
        geto()
        leercsvUser()
        secion()
        changeCOlorBtn()



    }
    private fun cerrarSeccion(){
        val sharedPreferences = getSharedPreferences("MiPref", Context.MODE_PRIVATE)
        val nuevoValor = 0
        val editor = sharedPreferences.edit()
        editor.putString("miValor", nuevoValor.toString())
        editor.apply()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun inicioSeccion(){
        val sharedPreferences = getSharedPreferences("MiPref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("miValor", "$contador")
        editor.apply()


    }
    private fun secion(){
        val sharedPreferences = getSharedPreferences("MiPref", Context.MODE_PRIVATE)
        val valorRecuperado = sharedPreferences.getString("miValor", "")
        println("valor $valorRecuperado")
        if (valorRecuperado == "1"){
            contador = 1
        }
    }

    private fun userUsu(){
        val sharedPreferences = getSharedPreferences("MiUsuario", Context.MODE_PRIVATE)
        val valorRecuperado = sharedPreferences.getString("miValor", "")
        val calendario = Calendar.getInstance()
        val hora = calendario.get(Calendar.HOUR_OF_DAY)
        val minutos = calendario.get(Calendar.MINUTE)
        val segundos = calendario.get(Calendar.SECOND)
        val ano = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH) + 1  // Los meses comienzan desde 0, por lo que sumamos 1
        val dia = calendario.get(Calendar.DAY_OF_MONTH)
        val diaFormateado = String.format("%02d", dia)


        usuarioGit = "$valorRecuperado"+"_$ano$mes$diaFormateado.csv"


        println("valori $usuarioGit")

    }


    private fun changeCOlorBtn(){
        val colorRojo = ColorStateList.valueOf(Color.RED)
        val colorVerde = ColorStateList.valueOf(Color.GREEN)
        if (contador == 0){
            btnPedidos.backgroundTintList = colorRojo
        }


        for (lista in arrayListE){
            if (lista.estado == "abierto"){
                btnCirculo.backgroundTintList = colorRojo

            }else if(lista.estado == "cerrado"){
                btnCirculo.backgroundTintList = colorVerde
            }
        }
    }
    private fun navigateToIncio() {
        if (contador == 1){
            val intent = Intent(this, PedidosActivity::class.java)
            startActivity(intent)
        }



    }



    private fun navigateToNewClient(){
        val intent = Intent(this, SolicitudCreditoActivity::class.java)
        startActivity(intent)
    }

    private fun sincronizar(){
        sincronizarUsers()
        sincronizarProducts()

        //subirDrivePedido()
    }


    private fun sincronizarUsers(){
        val usuarios: MutableList<UsersModel> = mutableListOf()

        val url = "https://script.google.com/macros/s/AKfycbykgrf2MAkYOrlcgyupiA0laVPYM845yx0Tdj-qfFXcHeo7qwFNs_annyEzDwgY9UhF/exec?function=doGet&nombreArchivo=matrices_seguridad_usuarios_20231021_203555.csv"
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
                                val nombre = fila[4]
                                val funcionId = fila[5]
                                val aplicacion = fila[5]
                                val funcion = fila[7]
                                val roleId = fila[8]
                                val role = fila[9]
                                val permiso = fila[10]

                                val userModel = UsersModel(
                                    usuarioId,
                                    usuario,
                                    password,
                                    nombre,
                                    correo,
                                    funcionId,
                                    aplicacion,
                                    funcion,
                                    roleId,
                                    role,
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
                    record[8],
                    record[9],
                    record[10]
                )
                usersList.add(user)

            }
            csvReader.close()
            for (use in usersList){
                println("wewona "+use.correo)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun sincronizarProducts(){
        val productos: MutableList<ProductsModel> = mutableListOf()

        val url =
            "https://script.google.com/macros/s/AKfycbykgrf2MAkYOrlcgyupiA0laVPYM845yx0Tdj-qfFXcHeo7qwFNs_annyEzDwgY9UhF/exec?function=doGet&nombreArchivo=matrices_productos_precios_20231021_203555.csv"
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
                                println("usu = $vendedor + $usu")
                                if (rol == "Vendedor"){
                                    println("usuario final $usu")
                                    if (vendedor == usu) {
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
                                }else if(rol == "Supervisor"){
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

                            }

                            // De aquí en adelante, después de procesar todos los datos, crea el archivo CSV
                            createProductCsv(productos)
                            println("Se pudo")
                            contador = 1
                            inicioSeccion()
                            mostrarMensajeSubidaCompleta()
                            startActivity(intent)

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
        println("usuario = $usuarioGit")
        val scriptUrl = "https://script.google.com/macros/s/AKfycbzKmqjG7A7ZjZIXsrCbGPmORumqjmmWQqbgObEd4f63VT5JwvoXMuGPGWZHRaSESNARvg/exec?function=doPost&nombreArchivo=$usuarioGit"

        val client = OkHttpClient()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                for (lista in arrayListE) {
                    val poducto = lista.codigo_producto
                    if (lista.estado == "cerrado") {
                        val jsonData = """
                        {
                           "nombreArchivo": "$usuarioGit",
                           "nuevosDatos": ["${lista.numPedido};${lista.fecha};${lista.vendedor};${lista.id_cliente};${lista.codigo_producto};${lista.tipo};${lista.precio};${lista.cnt};${lista.estado};${lista.comentario}"]
                        }
                    """.trimIndent()

                        val jsonRequestBody = jsonData.toRequestBody("application/json".toMediaType())

                        val request = Request.Builder()
                            .url(scriptUrl)
                            .post(jsonRequestBody)
                            .build()

                        val response = client.newCall(request).execute()

                        if (response.isSuccessful) {
                            val responseBody = response.body?.string()
                            println("Elemento subido con éxito: $responseBody")
                            //editarPedido(poducto)
                        } else {
                            println("Error en la solicitud POST para el elemento: ${lista.numPedido}")
                        }
                    }
                }
                startActivity(intent)


            } catch (e: IOException) {
                println("Error de red: ${e.message}")
            }
        }

    }
    private fun mostrarMensajeSubidaCompleta() {
        val snackbar = Snackbar.make(findViewById(android.R.id.content), "Subida completa", 5000) // Duración de 5 segundos
        val view = snackbar.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.CENTER
        view.layoutParams = params
        snackbar.show()
    }
    private fun editarPedido(poducto: String) {
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
            val idPedidoAEliminar = poducto // Reemplaza con el ID que necesitas eliminar
            println("pedido a eliminar $poducto")
            // Eliminar objetos con el ID deseado
            val objetosFiltrados = JSONArray()
            for (i in 0 until listaDeObjetos.length()) {
                val objeto = listaDeObjetos.getJSONObject(i)
                val idPedido = objeto.getString("codigo_producto")


                if (idPedido != idPedidoAEliminar) {
                    objetosFiltrados.put(objeto)
                    println("estos son los filtros"+objetosFiltrados)
                }
            }

            // Convertir la lista de objetos filtrados a JSON
            val nuevoContenido = objetosFiltrados.toString()

            // Escribir el nuevo contenido en el archivo TXT
            rutaArchivo.writeText(nuevoContenido)

            println("Objeto con ID $idPedidoAEliminar eliminado exitosamente.")


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

                    if (idPedido != "0" && cnt != "0") {
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
                                bonoT = bono
                            )
                        )
                    }
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