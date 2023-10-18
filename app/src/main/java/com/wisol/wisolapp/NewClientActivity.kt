package com.wisol.wisolapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class NewClientActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_client)

        val btnBack = findViewById<Button>(R.id.btnBackC)
        btnBack.setOnClickListener { navigateToBackInicio() }

    }

    private fun navigateToBackInicio(){
        val intent = Intent(this, InicioActivity::class.java)
        startActivity(intent)
    }










}