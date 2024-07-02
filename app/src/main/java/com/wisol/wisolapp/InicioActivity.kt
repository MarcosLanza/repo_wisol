package com.wisol.wisolapp

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.opencsv.CSVReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
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
    var usuarioGitT: String? = ""

    var user = ""






    val arrayListE: MutableList<PedidosModel> = ArrayList()
    private lateinit var btnCirculo: FloatingActionButton
    private lateinit var btnPedidos: Button
    private lateinit var btnNewClient: Button
    private lateinit var btnSincronizar: Button
    private lateinit var btnSubirPediddo: Button
    private lateinit var progressBar: ProgressBar




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        usu = intent.extras?.getString("ID_USUARIO").orEmpty()
        rol = intent.extras?.getString("ROLE").orEmpty()

        userUsu()
        val btnCerrar = findViewById<Button>(R.id.btncerrar)
        btnCerrar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) }

        btnPedidos = findViewById(R.id.btnPedidos)
        btnPedidos.setOnClickListener { navigateToIncio() }
        btnNewClient = findViewById(R.id.btnNewClient)
        btnSincronizar = findViewById(R.id.btnSincronizar)
        val btnout = findViewById<Button>(R.id.btnOut)
        btnSubirPediddo = findViewById(R.id.btnSubirPedido)
        btnCirculo = findViewById(R.id.flsincronizar)
        progressBar = findViewById(R.id.progressBar)

        btnSubirPediddo.setOnClickListener { subirDrivePedido() }
        btnout.setOnClickListener { cerrarSeccion() }
        btnSincronizar.setOnClickListener { loading()}

        btnNewClient.setOnClickListener { navigateToNewClient() }
        geto()
        secion()
        changeCOlorBtn()
        inicioRol()

        botonoBloqueados()




    }
    private fun loading (){
        progressBar.visibility = View.VISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            sincronizar()
            progressBar.visibility = View.GONE // Ocultar el ProgressBar cuando termine sincronizar
        }, 100) // 100 milisegundos de retraso

    }
    private fun botonoBloqueados(){
        val colorRojo = ColorStateList.valueOf(Color.RED)

        if (rol == "1"){
            btnPedidos.backgroundTintList = colorRojo
            btnPedidos.isEnabled = false

            btnSincronizar.backgroundTintList = colorRojo
            btnSincronizar.isEnabled = false

            btnNewClient.backgroundTintList = colorRojo
            btnNewClient.isEnabled = false

            btnSubirPediddo.backgroundTintList = colorRojo
            btnSubirPediddo.isEnabled = false

        }
        else if (rol == "2"){
            btnNewClient.backgroundTintList = colorRojo
            btnNewClient.isEnabled = false
        }
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
    private fun inicioRol(){
        val sharedPreferences = getSharedPreferences("RolUsuario", Context.MODE_PRIVATE)
        val valorRecuperado = sharedPreferences.getString("miValor", "")
        rol = valorRecuperado
        println("valorRol $valorRecuperado")


    }
    private fun secion(){
        val sharedPreferences = getSharedPreferences("MiPref", Context.MODE_PRIVATE)
        val valorRecuperado = sharedPreferences.getString("miValor", "")
        println("valor $valorRecuperado")
        if (valorRecuperado == "2"){
            contador = 2
        }
    }

    private fun userUsu(){
        val sharedPreferences = getSharedPreferences("MiUsuario", Context.MODE_PRIVATE)
        val valorRecuperado = sharedPreferences.getString("miValor", "")
        val calendario = Calendar.getInstance()

        val ano = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH) + 1  // Los meses comienzan desde 0, por lo que sumamos 1
        val dia = calendario.get(Calendar.DAY_OF_MONTH)
        val diaFormateado = String.format("%02d", dia)
        val mesFormateado = String.format("%02d", mes)



        usuarioGit = "$valorRecuperado"+"_$ano$mesFormateado$diaFormateado.csv"
        usuarioGitT = "_$ano$mesFormateado$diaFormateado.csv"

        user = valorRecuperado.toString()
        user = user.trim()


        println("valori $user")

    }

    private fun darkOn():Boolean{
        val isDarkModeEnabled = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }

        if (isDarkModeEnabled) {
            return true
            // El modo oscuro está activado
            // Puedes realizar acciones específicas para el modo oscuro aquí
        } else {
            return false
            // El modo oscuro no está activado
            // Puedes realizar acciones específicas para el modo claro aquí
        }
    }

    private fun changeCOlorBtn(){
        val dark = darkOn()
        if (dark == true){
            val colorTurquesa = ColorStateList.valueOf(Color.parseColor("#009688"))
            val colorRojo = ColorStateList.valueOf(Color.parseColor("#F0493D"))
            val colorVerde = ColorStateList.valueOf(Color.parseColor("#4CAF50"))


            btnCirculo.backgroundTintList = colorTurquesa

            if (contador == 0){
                btnPedidos.backgroundTintList = colorRojo
                println("rojo")
            }


            for (lista in arrayListE){
                if (lista.estado == "abierto"){
                    btnCirculo.backgroundTintList = colorRojo
                    println("rojo")


                }else if(lista.estado == "cerrado"){
                    btnCirculo.backgroundTintList = colorVerde
                    println("verde")

                }
            }

        }else{
            println("vamos a cambiar el color")
            val colorRojo = ColorStateList.valueOf(Color.RED)
            val colorVerde = ColorStateList.valueOf(Color.GREEN)
            val blue = ColorStateList.valueOf(Color.parseColor("#03A9F4"))
            btnCirculo.backgroundTintList = blue
            if (contador == 0){
            btnPedidos.backgroundTintList = colorRojo
            println("rojo")
            }


            for (lista in arrayListE){
                if (lista.estado == "abierto"){
                    btnCirculo.backgroundTintList = colorRojo
                    println("rojo")


                }else if(lista.estado == "cerrado"){
                    btnCirculo.backgroundTintList = colorVerde
                    println("verde")

                }
            }
        }
    }
    private fun navigateToIncio() {
        if (contador == 2){
            val intent = Intent(this, PedidosActivity::class.java)
            startActivity(intent)
        }



    }



    private fun navigateToNewClient(){
        val intent = Intent(this, SolicitudCreditoActivity::class.java)
        startActivity(intent)
    }

    private fun sincronizar(){
        sincronizarProducts()


        contador = 2


        //subirDrivePedido()
    }


    private fun sincronizarUsers(){
        val usuarios: MutableList<UsersModel> = mutableListOf()

        val url = "https://script.google.com/macros/s/AKfycbykgrf2MAkYOrlcgyupiA0laVPYM845yx0Tdj-qfFXcHeo7qwFNs_annyEzDwgY9UhF/exec?function=doGet&nombreArchivo=matrices_seguridad_usuarios$usuarioGitT"
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
                                val nombre = fila[3]
                                val correo = fila[4]
                                val funcionId = fila[5]
                                val aplicacion = fila[6]
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
                            inicioSeccion()
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

    private fun createCsvUsers(usuarios: MutableList<UsersModel>) {
        val fileName = "usuario_$user.csv" // Cambia esto al nombre de archivo deseado
        val directory = applicationContext.filesDir // Directorio interno de la aplicación
        val file = File(directory, fileName)
        try {
            val csvWriter = FileWriter(file)

            // Escribir encabezados
          //  csvWriter.append("usuarioId,usuario,password,correo,funcionId,aplicacion,funcion,roleId,permiso\n")

            // Escribir datos de usuarios
            for (user in usuarios) {
                csvWriter.append("${user.id},${user.usuario},${user.password},${user.nombre},${user.correo},${user.funcionId},${user.aplicacion},${user.funcion},${user.rolId},${user.role},${user.permiso}\n")
            }

            csvWriter.flush()
            csvWriter.close()

            println("Archivo CSV creado en: $file")
            leercsvUser()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun leercsvUser() {
        val filePath = applicationContext.filesDir.absolutePath + "/usuario_$user.csv" // Ruta al archivo CSV
        val file = File(filePath)

        if (!file.exists()) {
            println("El archivo no existe: $filePath")
            return
        }

        val usersList = mutableListOf<UsersModel>()

        try {
            val csvReader = CSVReader(FileReader(file))

            // Leer la primera línea (encabezados) y descartarla
            csvReader.readNext()

            var record: Array<String>?
            while (true) {
                record = csvReader.readNext()
                if (record == null) {
                    break
                }
                if (record.size >= 11) {
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
                } else {
                    println("Registro con formato incorrecto: ${record.joinToString(",")}")
                }
            }
            csvReader.close()
            for (use in usersList) {
                println("wewona ${use.correo}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error al leer el archivo CSV: ${e.message}")
        }
    }

    private fun sincronizarProducts() {
        println("hola entre a sincor")
        val url = "https://script.google.com/macros/s/AKfycbykgrf2MAkYOrlcgyupiA0laVPYM845yx0Tdj-qfFXcHeo7qwFNs_annyEzDwgY9UhF/exec?function=doGet&nombreArchivo=matrices_productos_precios$usuarioGitT"

        runBlocking {
            try {
                val productos = fetchData(url)
                createProductCsv(productos)

                println("Se pudo productos")
            } catch (e: Exception) {
                // Manejar errores
                e.printStackTrace()
                mostrarMensajeSubidaCompleta()
                println("no se pudo")
            }
        }
    }

    private suspend fun fetchData(url: String): List<ProductsModel> = withContext(Dispatchers.Default) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string()
            try {
                val responseObject = JSONObject(responseBody)

                if (responseObject.getString("resultado") == "Archivo encontrado") {
                    val datosCSVArray = responseObject.getJSONArray("datosCSV")

                    val datosCSVFormattedList = mutableListOf<List<String>>()
                    for (i in 0 until datosCSVArray.length()) {
                        val csvRowArray = datosCSVArray.getJSONArray(i)
                        val csvRowList = mutableListOf<String>()

                        for (j in 0 until csvRowArray.length()) {
                            val csvCellValue = csvRowArray.getString(j).trim()
                            csvRowList.add(csvCellValue)
                        }

                        if (csvRowList.isNotEmpty()) {
                            datosCSVFormattedList.add(csvRowList)
                        }
                    }

                    return@withContext filterProductsByRole(datosCSVFormattedList, rol.toString(), usu.toString())
                } else {
                    // Manejar el caso en que el resultado no sea "Archivo encontrado"
                    throw Exception("Archivo no encontrado")
                }
            } catch (e: JSONException) {
                // Manejar errores de análisis JSON
                throw e
            }
        } else {
            // Manejar errores de respuesta
            throw IOException("Error en la solicitud")
        }
    }

    // Función para filtrar productos según el rol
    private fun filterProductsByRole(
        datosCSVFormattedList: List<List<String>>,
        rol: String,
        usu: String
    ): List<ProductsModel> {
        println("hola entre a filter")

        val productos = mutableListOf<ProductsModel>()

        for (fila in datosCSVFormattedList) {
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
            val uc = fila[17]
            val ordernar = fila[18]

            if ((rol == "2" || rol == "3") && vendedor == usu || rol == "4") {
                productos.add(
                    ProductsModel(
                        vendedor, idCliente, descCliente, idProducto, descProducto,
                        marca, tipo, precio, descuento, tipoImpuesto, tipoTarifa,
                        impuesto, ventaTrim, ventaAnt, ventaAct, bono, minimo, uc, ordernar
                    )
                )
            }
        }
        println("ahead")

        return productos
    }
    private fun createProductCsv(productos: List<ProductsModel>) {
        val fileName = "productos_$user.csv"
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
                    product.minimo,
                    product.uc,
                    product.ordernar
                )

                val productCsvRow = productFields.joinToString(",")
                csvWriter.append("$productCsvRow\n")
            }

            csvWriter.flush()
            csvWriter.close()

            println("Archivo CSV creado en: $file")
            sincronizarUsers()

            leerproducts()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun leerproducts() {
        val filePath = applicationContext.filesDir.absolutePath + "/productos_$user.csv" // Ruta al archivo CSV
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
                    record[16],
                    record[17],
                    record[18]
                )

                productsList.add(product)
            }
            println(productsList.take(20))

            csvReader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun sincronizarProductsB() {
        println("hola entre a sincor")
        val url = "https://script.google.com/macros/s/AKfycbykgrf2MAkYOrlcgyupiA0laVPYM845yx0Tdj-qfFXcHeo7qwFNs_annyEzDwgY9UhF/exec?function=doGet&nombreArchivo=matrices_productos_precios$usuarioGitT"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.body?.use { responseBody ->
                    try {
                        val responseObject = JSONObject(responseBody.string())

                        if (responseObject.getString("resultado") == "Archivo encontrado") {
                            val datosCSVArray = responseObject.getJSONArray("datosCSV")

                            val datosCSVFormattedList = mutableListOf<List<String>>()
                            for (i in 0 until datosCSVArray.length()) {
                                val csvRowArray = datosCSVArray.getJSONArray(i)
                                val csvRowList = mutableListOf<String>()

                                for (j in 0 until csvRowArray.length()) {
                                    val csvCellValue = csvRowArray.getString(j).trim()
                                    csvRowList.add(csvCellValue)
                                }

                                if (csvRowList.isNotEmpty()) {
                                    datosCSVFormattedList.add(csvRowList)
                                }
                            }

                            val productos =
                                filterProductsByRole(datosCSVFormattedList, rol.toString(), usu.toString())

                            createProductCsv(productos)
                            println("Se pudo")
                            inicioSeccion()
                            startActivity(intent)
                            mostrarMensajeSubidaCompleta()

                        } else {
                            // Maneja el caso en el que el resultado no sea "Archivo encontrado"
                        }
                    } catch (e: JSONException) {
                        // Maneja errores de análisis JSON
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // Manejar errores de conexión
            }
        })
    }

    // Función para filtrar productos según el rol
    private fun filterProductsByRoleB(
        datosCSVFormattedList: List<List<String>>,
        rol: String,
        usu: String
    ): List<ProductsModel> {
        println("hola entre a filter")

        val productos = mutableListOf<ProductsModel>()

        for (fila in datosCSVFormattedList) {
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
            val uc = fila[17]
            val ordernar = fila[18]

            if ((rol == "2" || rol == "3") && vendedor == usu || rol == "4") {
                productos.add(
                    ProductsModel(
                        vendedor, idCliente, descCliente, idProducto, descProducto,
                        marca, tipo, precio, descuento, tipoImpuesto, tipoTarifa,
                        impuesto, ventaTrim, ventaAnt, ventaAct, bono, minimo, uc, ordernar
                    )
                )
            }
        }
        println("ahead")

        return productos
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
                           "nuevosDatos": ["${lista.idPedido};${lista.fecha};${lista.vendedor};${lista.id_cliente};${lista.codigo_producto};${lista.tipo};${lista.precio};${lista.cnt};${lista.estado};${lista.comentario}"]
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
                            editarPedido(poducto)
                        } else {
                            println("Error en la solicitud POST para el elemento: ${lista.idPedido}")
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
        val snackbar = Snackbar.make(findViewById(android.R.id.content), "ERROR AL SINCRONIZAR, VOLVER A INTENTAR", 4000) // Duración de 5 segundos
        val view = snackbar.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.CENTER
        view.layoutParams = params
        snackbar.show()
    }
    private fun editarPedido(poducto: String) {
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
                                bonoT = bono)
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
