package com.example.maletavirtual

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NuevaCategoriaActivity : AppCompatActivity() {

    /*declaracion variables*/
    private val db = Firebase.firestore
    private lateinit var nuevaCat: Button
    private lateinit var cancelar: Button
    private lateinit var nombreCat: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nueva_categoria)
        /*ocultando la barra de titulo*/
        supportActionBar?.hide()
        /*relacion con la vista*/
        nuevaCat=findViewById(R.id.create_cat)
        cancelar=findViewById(R.id.cancel_cat_button)
        nombreCat=findViewById(R.id.category_name)
        /*funcionalidad de el boton crear*/
        nuevaCat.setOnClickListener {
            crearCategoria()
        }
        cancelar.setOnClickListener { cancelar() }
    }

    /*funcionalidades*/
    private fun crearCategoria(){
        var nombreC=nombreCat.text.toString().trim()
        if (nombreC.isNotEmpty()) {
            db.collection("categorias").add(
                hashMapOf(
                    "nombre_categoria" to nombreC,
                )
            ).addOnSuccessListener {
                setResult(Activity.RESULT_OK)
                finish()
            }.addOnFailureListener {
                val error = it.toString();
                Toast.makeText(applicationContext, error, Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(applicationContext, "Nombra la categoria para crearla", Toast.LENGTH_LONG).show()
        }
    }
    private fun cancelar(){
        finish()
    }
}