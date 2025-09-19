package com.example.parcialarqui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.parcialarqui.categoria.CategoriaActivity

//import com.example.parcialarqui.categoria.CategoriaActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnCategorias: Button
    private lateinit var btnSalir: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inicializarVistas()
        configurarEventos()
    }

    private fun inicializarVistas() {
        btnCategorias = findViewById(R.id.btnCategorias)
        btnSalir = findViewById(R.id.btnSalir)
    }

    private fun configurarEventos() {
        btnCategorias.setOnClickListener {
            val intent = Intent(this, CategoriaActivity::class.java)
            startActivity(intent)
        }

        btnSalir.setOnClickListener {
            finishAffinity() // Cierra la app
        }
    }
}
