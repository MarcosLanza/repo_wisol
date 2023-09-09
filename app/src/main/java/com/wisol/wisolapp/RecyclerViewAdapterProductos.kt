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

class RecyclerViewAdapterProductos : RecyclerView.Adapter<RecyclerViewAdapterProductos.ViewHolder>(){

    var productos : MutableList<ProductosModel> = ArrayList()

    lateinit var context: Context
    var idP = ""


    var selectedItems: MutableList<ProductosModel> = ArrayList() // Lista para rastrear elementos seleccionados


    fun RecyclerViewAdapter(productos:MutableList<ProductosModel>,  context:Context){
        this.productos = productos
        this.context = context
    }


    class ViewHolder (view: View):RecyclerView.ViewHolder(view) {
        val producto:TextView
        val cnt:TextView
        val  bono:TextView

        init {
            producto = view.findViewById(R.id.txtNameProducto)
            cnt = view.findViewById(R.id.txtCNTProdcuto)
            bono = view.findViewById(R.id.txtProductoBono)
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_producto,parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return productos.size + 1 // Agregamos 1 para el encabezado
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            0 // Tipo para el encabezado
        } else {
            1 // Tipo para las filas de datos
        }
    }




    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) {
            // Configuración del encabezado
            holder.producto.text = "PRODUCTO"
            holder.cnt.text = "CNT"
            holder.bono.text = "BONO"
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.blue)) // Cambiar a tu color azul
        } else {
            // Configuración de las filas de datos
            val isEvenRow = (position - 1) % 2 == 0

            if (isEvenRow) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white1))
            } else {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.grey)) // Cambiar al color deseado
            }

            val producto = productos[position - 1] // Restamos 1 para compensar el encabezado
            holder.producto.text = producto.nameP
            holder.bono.text = producto.bono
            holder.cnt.text = holder.cnt.text

            holder.producto.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            holder.cnt.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            holder.bono.setTextColor(ContextCompat.getColor(context, android.R.color.black))


            holder.cnt.tag = "editTextItem_$position"
            holder.cnt.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // Este método se llama antes de que el texto cambie
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Este método se llama cuando el texto está cambiando
                    val newText = s.toString()
                    // Obtener el identificador único del EditText
                    val editTextTag = holder.cnt.tag as String
                    println("este es el new text "+newText)
                    // Notificar a la actividad principal con el identificador único y el nuevo texto
                    (context as? SeleccionProductoActivity)?.onEditTextChanged(editTextTag, newText)
                    for (i in 0 until selectedItems.size) {
                        if (selectedItems[i].idP == idP && newText !="0") {
                            selectedItems[i].cnt = newText // Reemplazar el elemento
                            (context as? SeleccionProductoActivity)?.save(selectedItems)
                            println("si llegue aqui")

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
            }

            // Manejar la lógica de selección/deselección aquí
            holder.itemView.setOnClickListener {
                if (selectedItems.contains(producto)) {
                    // Si el elemento ya está seleccionado, deseléctionalo
                    selectedItems.remove(producto)
                    holder.cnt.text = holder.cnt.text
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.red))





                } else {
                    // Si el elemento no está seleccionado, selecciónalo
                    selectedItems.add(producto)

                    holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.blue))
                    holder.cnt.text = holder.cnt.text
                    idP = producto.idP
                }
                // Notificar cambios en la selección
                notifyItemChanged(position)
            }
    }}

}