package com.example.maletavirtual

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class NuevoArticuloActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_articulo)
        /*ocultar barra de accion*/
        supportActionBar?.hide()
        /**/
    }
}