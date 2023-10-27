package com.wisol.wisolapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapterClientes : RecyclerView.Adapter<RecyclerViewAdapterClientes.ViewHolder>() {
    private var clientes: MutableList<ClientesProductosModel> = ArrayList()
    lateinit var context: Context
    private var selectedItemPosition = RecyclerView.NO_POSITION

    fun RecyclerViewAdapterP(clientes: MutableList<ClientesProductosModel>, context: Context) {
        this.clientes = clientes
        this.context = context
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cliente: TextView

        init {
            cliente = view.findViewById(R.id.txtCliente)
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    selectItem(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_item_cliente, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = clientes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val clienteProducto = clientes[position]
        holder.cliente.text = clienteProducto.desc_cliente
        val isSelected = position == selectedItemPosition

        if (isSelected) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
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

    private fun selectItem(position: Int) {
        if (position == selectedItemPosition) {
            // Si se selecciona el mismo elemento, deselecciónalo
            selectedItemPosition = RecyclerView.NO_POSITION
        } else {
            // De lo contrario, selecciona el nuevo elemento
            val previousSelectedItemPosition = selectedItemPosition
            selectedItemPosition = position
            notifyItemChanged(previousSelectedItemPosition)
            notifyItemChanged(selectedItemPosition)
            // Aquí puedes notificar a la actividad sobre la selección, si es necesario
            val selectedCliente = clientes[selectedItemPosition]
            (context as? SeleccionClienteActivity)?.click(selectedCliente.desc_cliente, selectedCliente.id_cliente, selectedCliente.id_producto)


        }
    }
}
