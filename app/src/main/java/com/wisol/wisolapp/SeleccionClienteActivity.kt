package com.wisol.wisolapp

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



    private lateinit var editText: TextInputEditText
    private lateinit var editCodigo: TextInputEditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccion_cliente)

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

                getClients()


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

                getClients()


            }

            override fun afterTextChanged(s: Editable?) {
                // No se utiliza en este caso
            }
        })



        init()



    }
    private fun clear(){
        editCodigo.setText("")
        editText.setText("")
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

    private fun init(){
        getClients()
    }
    fun click(ola:String?, codigo:String?, codigoCo:String?){

        selected = ola
        idProducto = codigo
        editText.setText(selected)
        if (codigo != null){
            editCodigo.setText(codigo)

        }
        filtro = selected
        filtroCodigo = selected
        getClients()

    }


    private fun getClients() {
        val filePath = applicationContext.filesDir.absolutePath + "/productos.csv" // Ruta al archivo CSV

        val arrayList: MutableList<ClientesProductosModel> = ArrayList()

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

                if (filterProduct(product)) {
                    addCliente(arrayList, product)
                }
            }

            csvReader.close()

            // Inicializar RecyclerView y su adaptador fuera del bucle while
            val recycler: RecyclerView = findViewById(R.id.recyclerViewC)
            val adapter: RecyclerViewAdapterClientes = RecyclerViewAdapterClientes()
            adapter.RecyclerViewAdapterP(arrayList.distinctBy { it.id_cliente }.toMutableList(), this)

            recycler.hasFixedSize()
            recycler.adapter = adapter
            recycler.layoutManager = LinearLayoutManager(this)
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


    private fun filterProduct(product: ProductsModel): Boolean {
        return when {
            selected == null && filtro == null && filtroCodigo == null -> {
                filtro = null
                filtroCodigo = null
                true // No hay filtros
            }
            filtro != null && product.desc_cliente.contains(filtro.toString(), ignoreCase = true) -> {
                filtroCodigo = null
                true
            }
            selected == product.desc_cliente -> {
                true
            }
            filtroCodigo != null && product.id_cliente.contains(filtroCodigo.toString(), ignoreCase = true) -> {
                filtro = null
                selected = null

                true
            }
            else -> {
                false // No hay coincidencia
            }
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