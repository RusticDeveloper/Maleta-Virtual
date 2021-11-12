package com.example.maletavirtual

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegistroActivity : AppCompatActivity() {

    /*instanciando firestore*/
    private val db = Firebase.firestore
    private val db1=FirebaseFirestore.getInstance()

    /*relación con elementos de la vista*/
    private lateinit var registro: Button
    private lateinit var cancelar: Button
    private lateinit var correo: EditText
    private lateinit var contrasena: EditText
    private lateinit var repeatContrasena: EditText
    private lateinit var nombre: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        /*ocultando la barra de titulo*/
        supportActionBar?.hide()
        /*Relacion con la vista*/
        registro= findViewById(R.id.registro_boton_registro)
        cancelar = findViewById(R.id.cancel_registro)
        correo = findViewById(R.id.correo_registro)
        contrasena = findViewById(R.id.contrasena_registro)
        repeatContrasena = findViewById(R.id.repeat_contrasena_registro)
        nombre = findViewById(R.id.nombre_registro)

        /*funcionalidad botones*/
        cancelar.setOnClickListener {
            val inicio = Intent(this, LoginActivity::class.java)
            startActivity(inicio)
        }
        registro.setOnClickListener {
            registrar()
        }


    }
    /*fin oncreate*/
    /*funcion de registro*/
    private fun registrar(){
        /*valores de los inputs*/
        val email: String = correo.text.toString().trim()
        val name: String = nombre.text.toString().trim()
        val password: String = contrasena.text.toString().trim()
        val repeatpassword: String = repeatContrasena.text.toString().trim()
        /*condicional para hacer el registro*/
        if (email.isNotEmpty() && name.isNotEmpty() && password.isNotEmpty() && repeatpassword.isNotEmpty()) {
            /*compacion de contraseñas*/
            if (password.equals(repeatpassword)) {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            /*guarda datos de cada usuario de un usuario, dentro de la colleccion usuarios*/
                            db1.collection("usuarios").document(email)
                                .set(hashMapOf(
                                    "nombre" to name,
                                    "proveedor" to "email"
                                ))
                            /*mensaje de confirmacion y finalizacion*/
                            Toast.makeText(
                                applicationContext,
                                "Usuario creado Correctamente",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }else{
                            Toast.makeText(
                                applicationContext,
                                "No se pudo completar el registro",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.i("error",it.toString())
                        }
                    }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Las contraseñas no coinciden",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(applicationContext, "Ingrese todos los campos", Toast.LENGTH_LONG)
                .show()
        }
    }

}