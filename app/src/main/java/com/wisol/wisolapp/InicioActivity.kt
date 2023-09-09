package com.wisol.wisolapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class InicioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        val btnPedidos = findViewById<Button>(R.id.btnPedidos)
        btnPedidos.setOnClickListener { navigateToIncio() }
        val btnNewClient = findViewById<Button>(R.id.btnNewClient)

        btnNewClient.setOnClickListener { navigateToNewClient() }

    }
    private fun navigateToIncio() {
        val intent = Intent(this, PedidosActivity::class.java)
        startActivity(intent)


    }

    private fun navigateToNewClient(){
        val intent = Intent(this, NewClientActivity::class.java)
        startActivity(intent)
    }
}