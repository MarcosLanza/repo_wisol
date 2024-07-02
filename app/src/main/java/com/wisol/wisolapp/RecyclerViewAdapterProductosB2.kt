package com.wisol.wisolapp

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class RecyclerViewAdapterProductosB2 : RecyclerView.Adapter<RecyclerViewAdapterProductosB2.ViewHolder>() {
    var productos: MutableList<ProductosModel> = ArrayList()
    var filteredProductos: MutableList<ProductosModel> = ArrayList() // Lista filtrada

    lateinit var filtro: String

    lateinit var context: Context
    var idP = ""
    var conta = 0
    var contaB = 0
    var rito = 0
    var num = 0.0
    var total = 0.0


    var selectedItems: MutableList<ProductosModel> = ArrayList() // Lista para rastrear elementos seleccionados

    init {
        filteredProductos = productos // Inicializa la lista filtrada con todos los productos
    }

    fun RecyclerViewAdapter(productos: MutableList<ProductosModel>, context: Context) {
        this.productos = productos
        this.filteredProductos = productos // Inicializa la lista filtrada con todos los productos

        this.context = context
    }

    fun updateFiltro(filtro: String) {
        this.filtro = filtro
        applyFilter(filtro)
        println("FILTRO $filtro")
    }

    fun applyFilter(newFilter: String) {
        filtro = newFilter
        filteredProductos = productos.filter { it.desc_producto.contains(filtro, ignoreCase = true) }.toMutableList()
        notifyDataSetChanged()
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val producto: TextView
        val cnt: TextView
        val bono: TextView
        val id_producto: TextView
        val precio : TextView
        val descuento : TextView
        val precioT : TextView
        val impuesto : TextView
        val uc : TextView


        init {
            producto = view.findViewById(R.id.txtNameProducto)
            cnt = view.findViewById(R.id.txtCNTProdcuto)
            bono = view.findViewById(R.id.txtProductoBono)
            id_producto = view.findViewById(R.id.txtProductoCodigo)
            precio = view.findViewById(R.id.txtProductoPrecio)
            descuento = view.findViewById(R.id.txtProductoDescuento)
            precioT = view.findViewById(R.id.txtProductoPrecioP)
            impuesto = view.findViewById(R.id.txtProductoImpuestoto)
            uc = view.findViewById(R.id.txtUC)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_producto, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredProductos.size // Usar la lista filtrada en lugar de la lista original
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val isEvenRow = position % 2 == 0

        if (isEvenRow) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white1))
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.grey)) // Cambiar al color deseado
        }
        val formato = NumberFormat.getNumberInstance(Locale.getDefault())


        val producto = filteredProductos[position]
        holder.producto.text = producto.desc_producto
        holder.cnt.text = producto.cnt
        holder.id_producto.text = producto.id_producto
        holder.precio.text = producto.precio
        holder.descuento.text = producto.descuento
        holder.impuesto.text = producto.impuesto
        holder.uc.text = producto.uc
        val she = producto.bonoT
        println("se esrta $she")

        holder.bono.text = producto.bonoT
        holder.precioT.text = formato.format(producto.precioTotal.toDouble())

        holder.producto.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        holder.cnt.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        holder.bono.setTextColor(ContextCompat.getColor(context, android.R.color.black))


        holder.cnt.tag = position // Almacena la posición en lugar de una etiqueta de texto

        holder.cnt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Este método se llama antes de que el texto cambie
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Este método se llama cuando el texto está cambiando
                val newText = s.toString()
                // Obtener la posición del elemento en lugar de la etiqueta
                val itemPosition = holder.cnt.tag as Int
                try {
                    if (newText != "" && newText != "0") {

                        num = (newText.toDouble() / producto.minimo.toDouble())
                        println("este es el nium $num")
                        val esEntero = num == num.toInt().toDouble()
                        if (esEntero) {
                            if (num != 0.0){
                                val hola = producto.bono.toDouble()
                                num = num*hola
                                println("hi $num")
                                holder.bono.text = String.format("%.1f", num)
                                holder.bono.setBackgroundColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.green
                                    )
                                ) // Cambiar al color deseado


                            }

                        }
                        else{
                            val hola = producto.bono.toDouble()
                            num = num*hola
                            println("hi ${producto.minimo}")
                            if (producto.minimo == "0"){
                                holder.bono.text = "0"

                                holder.bono.setBackgroundColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.red
                                    ))

                            }else{
                                holder.bono.text = String.format("%.1f", num)
                                holder.bono.setBackgroundColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.red
                                    ))
                            }

                        }
                        // holder.precioT.text = ((producto.precio.toDouble() - (producto.precio.toDouble()*(producto.desc_producto.toDouble()/100)))*3).toString()

                        val productoPrecio = producto.precio.toDouble()
                        val descuentoP = producto.descuento.toDouble()
                        total = productoPrecio-(productoPrecio*(descuentoP/100))
                        total = total* newText.toDouble()
                        holder.precioT.text = formato.format(total)
                        for(pro in selectedItems){
                            if (idP == pro.id_producto){
                                pro.precioTotal = total.toString()
                                pro.bonoT = holder.bono.text.toString()
                                println("total $total")

                            }

                        }


                    } else if (newText == "") {
                        holder.bono.text = "0"
                        holder.bono.setBackgroundColor(ContextCompat.getColor(context, R.color.grey)) // Cambiar al color deseado

                    }
                } catch (e: ArithmeticException) {
                    // Manejo de la excepción (división por cero)
                    // Puedes mostrar un mensaje de error o tomar alguna otra acción apropiada
                    holder.bono.text = "0"
                }
                // Notificar a la actividad principal con el identificador único y el nuevo texto
                (context as? SeleccionProductoB2Activity)?.onEditTextChanged(itemPosition, newText)
                for (i in 0 until selectedItems.size) {
                    if (selectedItems[i].id_producto == idP && newText != "0") {
                        selectedItems[i].cnt = newText // Reemplazar el elemento
                        (context as? SeleccionProductoB2Activity)?.save(selectedItems)
                        println("Sí llegué aquí")
                        break // Salir del bucle una vez que se haya realizado la edición
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Este método se llama después de que el texto cambie
            }
        })

        // Configuración de la selección de elementos
        // Cambiar el color de fondo de acuerdo a si el elemento está seleccionado
        if (selectedItems.contains(producto)) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
        } else {
            if (isEvenRow) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white1))
            } else {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.grey)) // Cambiar al color deseado
            }
            if (producto.cnt != "0" && producto.cnt != "" && rito == 0) {
                rito = 1
                for (uso in productos) {
                    if (uso.cnt != "0") {
                        selectedItems.add(uso)
                        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.green)) // Cambiar al color deseado
                        (context as? SeleccionProductoB2Activity)?.save(selectedItems)
                        (context as? SeleccionProductoB2Activity)?.reportes(selectedItems)
                    }
                }
            }
        }

        // Manejar la lógica de selección/deselección aquí
        holder.itemView.setOnClickListener {
            if (selectedItems.contains(producto)) {
                // Si el elemento ya está seleccionado, deseléccionalo
                selectedItems.remove(producto)
                (context as? SeleccionProductoB2Activity)?.reportes(selectedItems)
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
                println("Elementos deseleccionados en la posición $position: $selectedItems")
                holder.cnt.text = "0"

                holder.bono.text = String.format("%.1f", num)
                holder.bono.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green
                    )
                ) // Cambiar al color deseado
            } else {
                // Si el elemento no está seleccionado, selecciónalo
                selectedItems.add(producto)

                (context as? SeleccionProductoB2Activity)?.reportes(selectedItems)
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.blue))
                idP = producto.id_producto
                println("id es = $idP")
                holder.cnt.text = "0"
            }
            // Notificar cambios en la selección
            notifyItemChanged(position)
        }
    }
    fun resetFilter() {
        filteredProductos = productos
        notifyDataSetChanged()
    }
}