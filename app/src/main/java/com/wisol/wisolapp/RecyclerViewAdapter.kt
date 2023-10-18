package com.wisol.wisolapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(){

    var pedidos : MutableList<PedidosModel> = ArrayList()
    lateinit var context: Context
    var selectedItemsA: MutableList<PedidosModel> = ArrayList() // Lista para rastrear elementos seleccionados
    private var selectedItemPosition = RecyclerView.NO_POSITION
    private val selectedItems = mutableSetOf<Int>()



    fun RecyclerViewAdapter(pedidos:MutableList<PedidosModel>,  context:Context){
        this.pedidos = pedidos
        this.context = context
    }
    inner class ViewHolder (view: View):RecyclerView.ViewHolder(view) {
        val usuario:TextView
        val estado:TextView

        init {
            usuario = view.findViewById(R.id.txtUsuario)
            estado = view.findViewById(R.id.txtEstado)

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    toggleSelection(position)
                }
            }


        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pedido,parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return pedidos.size // Agregamos 1 para el encabezado
    }






    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val isSelected = holder.adapterPosition == selectedItemPosition
        holder.itemView.isActivated = isSelected

        // Configuración de las filas de datos
        val isEvenRow = position % 2 == 0
        val pedido = pedidos[position]

        holder.usuario.text = pedido.desc_cliente
        holder.estado.text = pedido.estado
        holder.usuario.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        holder.estado.setTextColor(ContextCompat.getColor(context, android.R.color.black))

        // Verificar si el pedido está en la lista de elementos seleccionados
        if (isSelected) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
        } else {
            if (isEvenRow) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.grey))
            } else {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white1))
            }
        }

        holder.itemView.setOnClickListener {
            if (selectedItemPosition == position) {
                selectedItemPosition = RecyclerView.NO_POSITION
                (context as? PedidosActivity)?.obtenerIdPe("", "")
            } else {
                selectedItemPosition = holder.adapterPosition
                var idPodido = pedido.idPedido
                var idCliente = pedido.id_cliente
                (context as? PedidosActivity)?.obtenerIdPe(idPodido, idCliente)
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
}