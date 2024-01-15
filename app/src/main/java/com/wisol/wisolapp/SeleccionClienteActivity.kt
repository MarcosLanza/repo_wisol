package com.wisol.wisolapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.opencsv.CSVReader
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException

class SeleccionClienteActivity : AppCompatActivity() {


    var selected:String? = null
    var selectedCo:String? = null
    var filtro: String? = null
    var filtroCodigo: String? = null
    var idProducto: String? = null
    var arrayList: MutableList<ClientesProductosModel> = ArrayList()
    var user = ""

    private lateinit var recyclerViewAdapter: RecyclerViewAdapterClientes


    private lateinit var editText: TextInputEditText
    private lateinit var editCodigo: TextInputEditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccion_cliente)
        userUsu()

        recyclerViewAdapter = RecyclerViewAdapterClientes()
        getClients()
        recyclerViewAdapter.RecyclerViewAdapter(arrayList.distinctBy { it.id_cliente }.toMutableList(), this)
        println("hola primera "+arrayList)

        val recycler: RecyclerView = findViewById(R.id.recyclerViewC)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = recyclerViewAdapter

        val btnCLear = findViewById<Button>(R.id.btnClear)
        btnCLear.setOnClickListener { clear() }

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

                updateFiltro()



            }

            override fun afterTextChanged(s: Editable?) {
                // No se utiliza en este caso
            }
        })
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

                    filtroCodigo = text
                    println(filtroCodigo)

                }

                updateFiltroB()



            }

            override fun afterTextChanged(s: Editable?) {
                // No se utiliza en este caso
            }
        })






    }
    private fun userUsu(){
        val sharedPreferences = getSharedPreferences("MiUsuario", Context.MODE_PRIVATE)
        val valorRecuperado = sharedPreferences.getString("miValor", "")


        user = valorRecuperado.toString()


        println("valori $user")

    }

    fun updateFiltro(){
        recyclerViewAdapter.updateFiltro(filtro.toString(),1)

    }
    fun updateFiltroB(){
        recyclerViewAdapter.updateFiltro(filtroCodigo.toString(),2)

    }
    private fun clear(){
        editCodigo.setText("")
        editText.setText("")
        idProducto = null
    }

    private fun navigateToBackPedido(){
        val intent = Intent(this, PedidosActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToProductos() {
        if (idProducto != null){
            val intent = Intent(this, SeleccionProductoActivity::class.java)
            intent.putExtra("ID_Producto", idProducto)
            startActivity(intent)

        }


    }


    fun click(ola:String?, codigo: String?){
        println("welcome $codigo")


        filtro = ola
        //editText.setText(ola)
        //updateFiltro()
        idProducto = codigo



    }


    private fun getClients() {
        val filePath = applicationContext.filesDir.absolutePath + "/productos_$user.csv" // Ruta al archivo CSV


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


                addCliente(arrayList, product)



            }

            csvReader.close()

            // Inicializar RecyclerView y su adaptador fuera del bucle while

        } catch (e: FileNotFoundException) {
            // Manejo de excepción si el archivo no se encuentra
            e.printStackTrace()
            // Aquí puedes agregar tu lógica para manejar la falta del archivo
        } catch (e: IOException) {
            // Manejo de excepción en caso de error de lectura
            e.printStackTrace()
            // Aquí puedes agregar tu lógica para manejar errores de lectura del archivo
        }
    }




    private fun addCliente(arrayList: MutableList<ClientesProductosModel>, product: ProductsModel) {
        arrayList.add(ClientesProductosModel(
            id_cliente = product.id_cliente,
            desc_cliente = product.desc_cliente,
            vendedor = product.vendedor,
            id_producto = product.id_producto
        ))
    }
}