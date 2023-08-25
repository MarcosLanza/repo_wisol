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

    }
    private fun navigateToIncio() {
        val intent = Intent(this, PedidosActivity::class.java)
        startActivity(intent)


    }
}