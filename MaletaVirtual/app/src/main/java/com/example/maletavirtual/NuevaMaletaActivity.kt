package com.example.maletavirtual

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NuevaMaletaActivity : AppCompatActivity() {
    /*declaracion variables*/
    private val db = Firebase.firestore
    private lateinit var nuevaMaleta: Button
    private lateinit var cancela: Button
    private lateinit var nombreMaleta: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nueva_maleta)
        /*ocultando la barra de titulo*/
        supportActionBar?.hide()
        /*relacion con la vista*/
        nuevaMaleta=findViewById(R.id.create_cat)
        cancela=findViewById(R.id.cncel_btn)
        nombreMaleta=findViewById(R.id.category_name)
        /*funcionalidad de el boton crear*/
        nuevaMaleta.setOnClickListener {
            crearMaleta()
        }
        cancela.setOnClickListener { cancelar() }
    }
    /*funcionalidades*/
    private fun crearMaleta(){
        val userMail = intent.getStringExtra("user_mail")
        var nombreM=nombreMaleta.text.toString().trim()
        if (nombreM.isNotEmpty()) {
            db.collection("maletas").add(
                hashMapOf(
                    "nombre_maleta" to nombreM,
                    "propietario" to userMail,
                    "lider" to "",
                    "tipo" to "regular",
                    "articulos" to mutableListOf<BagItems>()
                )
            ).addOnSuccessListener {
                val nuevaMaleta= Intent(this,PrincipalActivity::class.java).apply {
                    putExtra("usuario_correo",userMail)
                }
                startActivity(nuevaMaleta)
                finish()
            }.addOnFailureListener {
                val error = it.toString();
                Toast.makeText(applicationContext, error, Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(applicationContext, "Pon un nombre a tu maleta", Toast.LENGTH_LONG).show()
        }
    }
    private fun cancelar(){
         val userMail = intent.getStringExtra("user_mail")
        val nuevaMaleta= Intent(this,PrincipalActivity::class.java).apply {
            putExtra("usuario_correo",userMail)
        }
        startActivity(nuevaMaleta)
        finish()
    }

}