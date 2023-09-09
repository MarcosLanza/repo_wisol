package com.wisol.wisolapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapterClientes : RecyclerView.Adapter<RecyclerViewAdapterClientes.ViewHolder>(){

    var clientes : MutableList<ClientesProductosModel> = ArrayList()
    lateinit var context: Context
    private val selectedItems = mutableSetOf<Int>()
    private var selectedItemPosition = RecyclerView.NO_POSITION

    var SeleccionClienteActivity: SeleccionClienteActivity? = null


    var selectedClienteText: String? = null // Propiedad para almacenar el texto del cliente seleccionado
    var selectedCodigotext: String? = null




    fun RecyclerViewAdapterP(clientes:MutableList<ClientesProductosModel>,  context:Context){
        this.clientes = clientes
        this.context = context
    }
    inner class ViewHolder (view: View):RecyclerView.ViewHolder(view) {
        val cliente: TextView

        init {
            cliente = view.findViewById(R.id.txtCliente)
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    toggleSelection(position)
                }
            }

        }


    }
    fun hayClienteSeleccionado(): String? {
        return selectedClienteText
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_item_cliente,parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = clientes.size



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val clienteProducto = clientes[position]
        holder.cliente.text = clienteProducto.cliente
        val isSelected = holder.adapterPosition == selectedItemPosition
        holder.itemView.isActivated = isSelected

        val isEvenRow = (position - 1) % 2 == 0



        // Cambiar el color de fondo según si está seleccionado o no
        if (isSelected) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.blue))
        } else {
            if (isEvenRow) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white1))
            } else {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.grey)) // Cambiar al color deseado
            }
        }

        holder.itemView.setOnClickListener {
            if (selectedItemPosition == position) {
                selectedItemPosition = RecyclerView.NO_POSITION
                SeleccionClienteActivity?.click(null, null)


            } else {
                selectedItemPosition = holder.adapterPosition
                selectedClienteText = clienteProducto.cliente
                selectedCodigotext = clienteProducto.id
                SeleccionClienteActivity?.click(selectedClienteText, selectedCodigotext)

            }
            notifyDataSetChanged()
        }

    }

    private fun toggleSelection(position: Int) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(position)
        } else {
            selectedItems.add(position)
        }

        notifyItemChanged(position)
    }

    fun updatelista(clientes:MutableList<ClientesProductosModel>, context:Context){
        this.clientes = clientes
        this.context = context



        notifyDataSetChanged()
    }

}