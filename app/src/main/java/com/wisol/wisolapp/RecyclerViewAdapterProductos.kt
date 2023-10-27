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

class RecyclerViewAdapterProductos : RecyclerView.Adapter<RecyclerViewAdapterProductos.ViewHolder>() {

    var productos: MutableList<ProductosModel> = ArrayList()

    lateinit var context: Context
    var idP = ""
    var conta = 0
    var contaB = 0
    var rito = 0

    var selectedItems: MutableList<ProductosModel> = ArrayList() // Lista para rastrear elementos seleccionados

    fun RecyclerViewAdapter(productos: MutableList<ProductosModel>, context: Context) {
        this.productos = productos
        this.context = context
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val producto: TextView
        val cnt: TextView
        val bono: TextView
        val id_producto: TextView
        val precio : TextView
        val descuento : TextView
        val precioT : TextView

        init {
            producto = view.findViewById(R.id.txtNameProducto)
            cnt = view.findViewById(R.id.txtCNTProdcuto)
            bono = view.findViewById(R.id.txtProductoBono)
            id_producto = view.findViewById(R.id.txtProductoCodigo)
            precio = view.findViewById(R.id.txtProductoPrecio)
            descuento = view.findViewById(R.id.txtProductoDescuento)
            precioT = view.findViewById(R.id.txtProductoPrecioP)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_producto, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return productos.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Configuración de las filas de datos
        val isEvenRow = position % 2 == 0

        if (isEvenRow) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white1))
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.grey)) // Cambiar al color deseado
        }

        val producto = productos[position]
        holder.producto.text = producto.desc_producto
        holder.cnt.text = producto.cnt
        holder.id_producto.text = producto.id_producto
        holder.precio.text = producto.precio
        holder.descuento.text = producto.descuento
        holder.precioT.text = producto.precio

        holder.producto.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        holder.cnt.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        holder.bono.setTextColor(ContextCompat.getColor(context, android.R.color.black))


        holder.cnt.tag = position // Almacena la posición en lugar de una etiqueta de texto
        println("Posición: $position")

        holder.cnt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Este método se llama antes de que el texto cambie
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Este método se llama cuando el texto está cambiando
                val newText = s.toString()
                // Obtener la posición del elemento en lugar de la etiqueta
                val itemPosition = holder.cnt.tag as Int
                println("Nuevo texto en la posición $itemPosition: $newText")
                try {
                    if (newText != "") {
                        holder.bono.text = (newText.toInt() / producto.minimo.toInt()).toString()
                    } else if (newText == "") {
                        holder.bono.text = "0"
                    }
                } catch (e: ArithmeticException) {
                    // Manejo de la excepción (división por cero)
                    // Puedes mostrar un mensaje de error o tomar alguna otra acción apropiada
                    holder.bono.text = "0"
                }
                // Notificar a la actividad principal con el identificador único y el nuevo texto
                (context as? SeleccionProductoActivity)?.onEditTextChanged(itemPosition, newText)
                for (i in 0 until selectedItems.size) {
                    if (selectedItems[i].id_producto == idP && newText != "0") {
                        selectedItems[i].cnt = newText // Reemplazar el elemento
                        (context as? SeleccionProductoActivity)?.save(selectedItems)
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
                        println("Elementos seleccionados en la posición $position: $selectedItems")
                        (context as? SeleccionProductoActivity)?.save(selectedItems)
                        (context as? SeleccionProductoActivity)?.reportes(selectedItems)
                    }
                }
            }
        }

        // Manejar la lógica de selección/deselección aquí
        holder.itemView.setOnClickListener {
            if (selectedItems.contains(producto)) {
                // Si el elemento ya está seleccionado, deseléccionalo
                selectedItems.remove(producto)
                (context as? SeleccionProductoActivity)?.reportes(selectedItems)
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
                println("Elementos deseleccionados en la posición $position: $selectedItems")
                holder.cnt.text = "0"
            } else {
                // Si el elemento no está seleccionado, selecciónalo
                println("Elemento seleccionado en la posición $position: $producto")
                selectedItems.add(producto)
                println("Elementos seleccionados en la posición $position: $selectedItems")
                (context as? SeleccionProductoActivity)?.reportes(selectedItems)
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.blue))
                idP = producto.id_producto
                holder.cnt.text = "0"
            }
            // Notificar cambios en la selección
            notifyItemChanged(position)
        }
    }
}