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


    fun RecyclerViewAdapter(pedidos:MutableList<PedidosModel>,  context:Context){
        this.pedidos = pedidos
        this.context = context
    }
    class ViewHolder (view: View):RecyclerView.ViewHolder(view) {
        val usuario:TextView
        val estado:TextView
        val  id:TextView

        init {
            id = view.findViewById(R.id.txtId)
            usuario = view.findViewById(R.id.txtUsuario)
            estado = view.findViewById(R.id.txtEstado)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pedido,parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return pedidos.size + 1 // Agregamos 1 para el encabezado
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
            holder.id.text = "ID"
            holder.usuario.text = "Cliente"
            holder.estado.text = "Estado"
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.blue)) // Cambiar a tu color azul
        } else {
            // Configuración de las filas de datos
            val isEvenRow = (position - 1) % 2 == 0

            if (isEvenRow) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white1))
            } else {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.grey)) // Cambiar al color deseado
            }

            val pedido = pedidos[position - 1] // Restamos 1 para compensar el encabezado
            holder.id.text = pedido.id
            holder.usuario.text = pedido.usuario
            holder.estado.text = pedido.estado
            holder.id.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            holder.usuario.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            holder.estado.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        }
    }

}