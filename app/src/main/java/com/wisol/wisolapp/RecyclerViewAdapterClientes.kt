package com.wisol.wisolapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapterClientes : RecyclerView.Adapter<RecyclerViewAdapterClientes.ViewHolder>() {
    var filteredProductos: MutableList<ClientesProductosModel> = ArrayList() // Lista filtrada
    var clientes: MutableList<ClientesProductosModel> = ArrayList()

    lateinit var filtro: String

    lateinit var context: Context
    private var selectedItemPosition = RecyclerView.NO_POSITION


    init {
        filteredProductos = clientes // Inicializa la lista filtrada con todos los productos
    }

    fun RecyclerViewAdapter(clientes: MutableList<ClientesProductosModel>, context: Context) {
        this.clientes = clientes
        this.filteredProductos = clientes
        this.context = context
    }
    fun updateFiltro(filtro: String, a:Int) {
        this.filtro = filtro
        applyFilter(filtro, a)
    }

    fun applyFilter(newFilter: String, a: Int) {
        filtro = newFilter
        println("hola filtro $filtro")
        println(filteredProductos)
        if (a == 1){
            filteredProductos = clientes.filter { it.desc_cliente.contains(filtro, ignoreCase = true) }.toMutableList()
            notifyDataSetChanged()
        }
        else{
            filteredProductos = clientes.filter { it.id_cliente.contains(filtro, ignoreCase = true) }.toMutableList()
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cliente: TextView
        val codigo: TextView

        init {
            cliente = view.findViewById(R.id.txtCliente)
            codigo = view.findViewById(R.id.txtCODIGOS)
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    toggleItemSelection(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_item_cliente, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredProductos.size // Usar la lista filtrada en lugar de la lista original
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val clienteProducto = filteredProductos[position]

        holder.cliente.text = clienteProducto.desc_cliente
        holder.codigo.text = clienteProducto.id_cliente
        val isSelected = position == selectedItemPosition

        if (isSelected) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
            println("HolaFiltro "+clienteProducto.id_cliente )
            (context as? SeleccionClienteActivity)?.click(clienteProducto.desc_cliente, clienteProducto.id_cliente)



        } else {

            val isEvenRow = position % 2 == 0
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    if (isEvenRow) R.color.white1 else R.color.grey

                )
            )
        }
    }

    private fun toggleItemSelection(position: Int) {
        if (position == selectedItemPosition) {
            // Si se selecciona el mismo elemento, deselecciónalo
            selectedItemPosition = RecyclerView.NO_POSITION
            (context as? SeleccionClienteActivity)?.click(null, null)

        } else {
            // De lo contrario, selecciona el nuevo elemento
            selectedItemPosition = position

        }

        notifyDataSetChanged()
    }
    fun resetFilter() {
        filteredProductos = clientes
        notifyDataSetChanged()
    }
}