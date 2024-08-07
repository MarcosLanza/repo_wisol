package com.wisol.wisolapp

//import com.android.volley.Request

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.opencsv.CSVReader
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.FileReader
import java.io.IOException
import java.security.MessageDigest
import java.util.Calendar


class MainActivity : AppCompatActivity() {

    var nombreBuscado = ""
    private var passwordBuscada = ""
    var passwordCodificado = ""
    var idUsuario : String? = ""
    var rol : String? = null



    val usersList = mutableListOf<UsersModel>()
    private lateinit var swDarkMode: SwitchMaterial





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnStart = findViewById<Button>(R.id.btnStart)
        val inputName = findViewById<TextInputEditText>(R.id.inputUser)
        val inputPass = findViewById<TextInputEditText>(R.id.inputPass)
        swDarkMode = findViewById(R.id.swDarkMode)
        darkmode()
        swDarkMode.setOnCheckedChangeListener { _, isSelected ->
            if (isSelected){
                enableDarkMode()
            }else{
                disableDarkMode()
            }
        }


        loco()


        btnStart.setOnClickListener {
            nombreBuscado = inputName.text.toString()
            passwordBuscada = inputPass.text.toString()
            passwordCodificado = sha256(passwordBuscada)
            conexionInternet()

        }


    }


    private fun darkmode(){
        // Verificar si el modo oscuro está activado
        val isDarkModeEnabled = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }

        if (isDarkModeEnabled) {
            println("modo oscuro defalut")
            swDarkMode.isChecked = true
        } else {
            swDarkMode.isChecked = false
            println("no modo oscuro defalut")

            // El modo oscuro no está activado
            // Puedes realizar acciones específicas para el modo claro aquí
        }

    }
    private fun enableDarkMode(){
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        delegate.applyDayNight()
    }
    private fun disableDarkMode(){
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)

    }

    private fun conexionInternet(){
        if (isNetworkAvailable(this)) {
            println("hay internet")
            realizarSolicitudHTTP()

        } else {
            // No hay conexión a Internet
            println("no hay internet")
            leercsvUser()
            login()

        }
    }
    private fun login(){
        for (use in usersList){
            if (use.usuario == nombreBuscado && use.password == passwordCodificado){
                println("password es "+use.password)
                idUsuario = use.usuario
                if (use.funcionId == "APP-01"){
                    rol = "1"
                    println("esta es la FUNCION ID ${use.funcionId}")
                    inicioSeccion()
                    navigateToIncio()
                }

            }
            //println("Bye Muchachos")
            //println("password es "+use.password+" "+use.usuario)
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
    private fun sha256(text: String): String {
        val bytes = text.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)

        // Convierte el resultado en una representación hexadecimal
        val hexChars = CharArray(digest.size * 2)
        for (i in digest.indices) {
            val v = digest[i].toInt() and 0xFF
            hexChars[i * 2] = "0123456789ABCDEF"[v ushr 4]
            hexChars[i * 2 + 1] = "0123456789ABCDEF"[v and 0x0F]
        }

        return String(hexChars)
    }

    private fun leercsvUser(){
        val filePath = applicationContext.filesDir.absolutePath + "/usuarios.csv" // Ruta al archivo CSV


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
               // println("Hola "+usersList)

            }
            csvReader.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }


    private fun navigateToIncio() {
        val intent = Intent(this, InicioActivity::class.java)
        intent.putExtra("ID_USUARIO", idUsuario)
        intent.putExtra("ROLE", rol)


        println("estoy mandando esto $idUsuario")
        startActivity(intent)

    }

    private fun realizarSolicitudHTTP() {
        var roto = 0
        val calendario = Calendar.getInstance()

        val ano = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH) + 1  // Los meses comienzan desde 0, por lo que sumamos 1
        val dia = calendario.get(Calendar.DAY_OF_MONTH)
        val diaFormateado = String.format("%02d", dia)
        val mesFormateado = String.format("%02d", mes)

        val fechaj = "_$ano$mesFormateado$diaFormateado.csv"
        println("Fecha $fechaj")
        val url = "https://script.google.com/macros/s/AKfycbykgrf2MAkYOrlcgyupiA0laVPYM845yx0Tdj-qfFXcHeo7qwFNs_annyEzDwgY9UhF/exec?function=doGet&nombreArchivo=matrices_seguridad_usuarios$fechaj"

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

                            // Itera sobre los datos formateados
                            for (fila in datosCSVFormattedList) {
                                val usuario = fila[1]
                                val password = fila[2]
                                val nombre = fila[3]

                                println("Esta es la contraseña de la fila $password $passwordCodificado NOMBRE $nombreBuscado")

                                if (usuario.trim() == nombreBuscado.trim() && password.trim() == passwordCodificado.trim()) {
                                    roto = 2

                                    // Obtén el rol basado en la aplicación (funcionId)
                                    val funcionId = fila[5]
                                    println("Usuario: $usuario, FuncionId: $funcionId") // Agrega esta línea para depurar

                                    // Obtén el valor numérico del rol actual
                                    val rolActual = rol?.toIntOrNull()

                                    // Obtén el valor numérico del nuevo rol
                                    val nuevoRol = when (funcionId) {
                                        "APP-04" -> 4
                                        "APP-03" -> 3
                                        "APP-02" -> 2
                                        "APP-01" -> 1
                                        else -> {
                                            println("Rol no reconocido para FuncionId: $funcionId") // Agrega esta línea para depurar
                                            null
                                        }
                                    }

                                    // Actualiza el rol solo si el nuevo rol es mayor al rol actual
                                    if (nuevoRol != null && (rolActual == null || nuevoRol > rolActual)) {
                                        rol = nuevoRol.toString()
                                    }

                                    val usersModel = UsersModel(
                                        fila[0], usuario, password, nombre, fila[4], funcionId, fila[6], fila[7], fila[8], fila[9], fila[10]
                                    )
                                    usersList.add(usersModel)
                                    println("La lista de usuarios: $usersList")

                                    idUsuario = usuario

                                    rolUsuario()
                                    inicioSeccion()
                                    navigateToIncio()
                                }
                            }

                            println("Bye Muchachos")
                            println("Este es el rol $rol")
                        } else {
                            // Maneja el caso en el que el resultado no sea "Archivo encontrado"
                        }
                    } catch (e: JSONException) {
                        // Maneja errores de análisis JSON
                        e.printStackTrace()
                    }

                    if (roto == 0) {
                        mostrarMensajeInvalido()
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


    fun mostrarMensajeInvalido() {
        val rootView = findViewById<View>(android.R.id.content)
        Snackbar.make(rootView, "Contraseña o usuario inválido", Snackbar.LENGTH_LONG).show()
    }

    private fun inicioSeccion(){
        val sharedPreferences = getSharedPreferences("MiUsuario", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("miValor", "$idUsuario")
        println("hola inicio $idUsuario")
        editor.apply()


    }
    private fun loco(){
        val sharedPreferences = getSharedPreferences("MiPref", Context.MODE_PRIVATE)
        val valorRecuperado = sharedPreferences.getString("miValor", "")
        println("valor $valorRecuperado")
    }
    private fun rolUsuario(){
        val sharedPreferences = getSharedPreferences("RolUsuario", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("miValor", "$rol")
        println("hola inicio $rol")
        editor.apply()
    }



}

