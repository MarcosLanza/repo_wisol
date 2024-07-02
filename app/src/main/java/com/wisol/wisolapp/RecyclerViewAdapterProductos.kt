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

class RecyclerViewAdapterProductos : RecyclerView.Adapter<RecyclerViewAdapterProductos.ViewHolder>() {

    private var productos: MutableList<ProductosModel> = ArrayList()
    private var filteredProductos: MutableList<ProductosModel> = ArrayList()

    private lateinit var filtro: String

    lateinit var context: Context
    var idP = ""
    private var rito = 0
    var num = 0.0
    var total = 0.0

    var selectedItems: MutableList<ProductosModel> = ArrayList()

    init {
        filteredProductos = productos
    }

    fun RecyclerViewAdapter(productos: MutableList<ProductosModel>, context: Context) {
        this.productos = productos
        this.filteredProductos = productos
        this.context = context
    }

    fun updateFiltro(filtro: String) {
        this.filtro = filtro
        applyFilter(filtro)
    }

    private fun applyFilter(newFilter: String) {
        filtro = newFilter
        filteredProductos = productos.filter { it.desc_producto.contains(filtro, ignoreCase = true) }.toMutableList()
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val producto: TextView = view.findViewById(R.id.txtNameProducto)
        val cnt: TextView = view.findViewById(R.id.txtCNTProdcuto)
        val bono: TextView = view.findViewById(R.id.txtProductoBono)
        val id_producto: TextView = view.findViewById(R.id.txtProductoCodigo)
        val precio: TextView = view.findViewById(R.id.txtProductoPrecio)
        val descuento: TextView = view.findViewById(R.id.txtProductoDescuento)
        val precioT: TextView = view.findViewById(R.id.txtProductoPrecioP)
        val impuesto: TextView = view.findViewById(R.id.txtProductoImpuestoto)
        val uc: TextView = view.findViewById(R.id.txtUC)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_producto, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredProductos.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val isEvenRow = position % 2 == 0
        val producto = filteredProductos[position]

        holder.itemView.setBackgroundColor(
            ContextCompat.getColor(
                context,
                if (isEvenRow) R.color.white1 else R.color.grey
            )
        )

        val formato = NumberFormat.getNumberInstance(Locale.getDefault())
        holder.producto.text = producto.desc_producto
        holder.cnt.text = producto.cnt
        holder.id_producto.text = producto.id_producto
        holder.precio.text = formato.format(producto.precio.toDouble())
        holder.descuento.text = producto.descuento
        holder.impuesto.text = producto.impuesto
        holder.uc.text = producto.uc
        holder.bono.text = producto.bonoT
        holder.precioT.text = producto.precioTotal
        holder.producto.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        holder.cnt.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        holder.bono.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        holder.cnt.tag = position

        holder.cnt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val newText = s.toString()
                val itemPosition = holder.cnt.tag as Int
                try {
                    if (newText.isNotEmpty() && newText != "0") {
                        num = newText.toDouble() / producto.minimo.toDouble()
                        val esEntero = num == num.toInt().toDouble()
                        if (esEntero) {
                            if (num != 0.0) {
                                val hola = producto.bono.toDouble()
                                num *= hola
                                holder.bono.text = String.format("%.1f", num)
                                holder.bono.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
                            }
                        } else {
                            val hola = producto.bono.toDouble()
                            num *= hola
                            if (producto.minimo == "0") {
                                holder.bono.text = "0"
                                holder.bono.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
                            } else {
                                holder.bono.text = String.format("%.1f", num)
                                holder.bono.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
                            }
                        }
                        val productoPrecio = producto.precio.toDouble()
                        val descuentoP = producto.descuento.toDouble()
                        total = productoPrecio - (productoPrecio * (descuentoP / 100))
                        total *= newText.toDouble()
                        holder.precioT.text = formato.format(total)
                        for (pro in selectedItems) {
                            if (idP == pro.id_producto) {
                                pro.precioTotal = total.toString()
                                pro.bonoT = holder.bono.text.toString()
                            }
                        }
                    } else if (newText.isEmpty()) {
                        holder.bono.text = "0"
                        holder.bono.setBackgroundColor(ContextCompat.getColor(context, R.color.grey))
                    }
                } catch (e: ArithmeticException) {
                    holder.bono.text = "0"
                }
                (context as? SeleccionProductoActivity)?.onEditTextChanged(itemPosition, newText)
                for (i in selectedItems.indices) {
                    if (selectedItems[i].id_producto == idP && newText != "0") {
                        selectedItems[i].cnt = newText
                        (context as? SeleccionProductoActivity)?.save(selectedItems)
                        break
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        if (selectedItems.contains(producto)) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
        } else {
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    if (isEvenRow) R.color.white1 else R.color.grey
                )
            )
            if (producto.cnt != "0" && producto.cnt.isNotEmpty() && rito == 0) {
                rito = 1
                for (uso in productos) {
                    if (uso.cnt != "0") {
                        selectedItems.add(uso)
                        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
                        (context as? SeleccionProductoActivity)?.save(selectedItems)
                        (context as? SeleccionProductoActivity)?.reportes(selectedItems)
                    }
                }
            }
        }

        holder.itemView.setOnClickListener {
            if (selectedItems.contains(producto)) {
                selectedItems.remove(producto)
                (context as? SeleccionProductoActivity)?.reportes(selectedItems)
                var cantidad = holder.cnt.text.toString()
                println("el elemnot deseleccionad es $cantidad")
                if (cantidad != "0"){
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.blue))
                    holder.bono.setBackgroundColor(ContextCompat.getColor(context, R.color.blue))


                }else{
                    if (isEvenRow) {
                        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white1))
                        holder.bono.setBackgroundColor(ContextCompat.getColor(context, R.color.white1))

                    } else {
                        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.grey)) // Cambiar al color deseado
                        holder.bono.setBackgroundColor(ContextCompat.getColor(context, R.color.grey))

                    }
                }




                for (pts in selectedItems) {
                    holder.cnt.text = if (idP == pts.id_producto) "0" else "0"
                }
                holder.bono.text = "0"


                //  holder.bono.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
            } else {
                selectedItems.add(producto)
                (context as? SeleccionProductoActivity)?.reportes(selectedItems)
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
                holder.bono.setBackgroundColor(ContextCompat.getColor(context, R.color.green))

                idP = producto.id_producto
                for (pts in selectedItems) {
                    holder.cnt.text = if (idP == pts.id_producto) pts.cnt else "0"
                }
            }
        }
    }

    fun resetFilter() {
        filteredProductos = productos
        notifyDataSetChanged()
    }
}
