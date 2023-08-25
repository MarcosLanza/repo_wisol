package com.wisol.wisolapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

    override fun getItemCount() = pedidos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.id.text = pedidos[position].id
        holder.usuario.text = pedidos[position].usuario
        holder.estado.text = pedidos[position].estado
    }

}