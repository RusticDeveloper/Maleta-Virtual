package com.example.maletavirtual

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    /*relación con elementos de la vista*/
    private lateinit var inicio: Button
    private lateinit var registrar: Button
    private lateinit var correo: EditText
    private lateinit var contrasena: EditText
    private lateinit var MOBoton: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /*ocultando la barra de titulo*/
        supportActionBar?.hide()
        /*relacion con la vista*/
        inicio = findViewById(R.id.botonIngreso)
        registrar = findViewById(R.id.botonRegistro)
        correo = findViewById(R.id.correo_ingreso)
        contrasena = findViewById(R.id.contrasena_ingreso)
        MOBoton = findViewById(R.id.m_o_check)

        /*otorgando funcionalidad a los botones*/
        registrar.setOnClickListener {
            irRegistro()
        }
        inicio.setOnClickListener {
            iniciarSecion()
        }

        MOBoton.setOnCheckedChangeListener { compoundButton, isCheched ->
            if (isCheched){
                MOBoton.text="Ocultar Contraseña"
                contrasena.transformationMethod=HideReturnsTransformationMethod.getInstance()
            }else{
                MOBoton.text="Mostrar Contraseña"
                contrasena.transformationMethod=PasswordTransformationMethod.getInstance()
            }
        }
    }

    /*funciones de accion*/
    private fun iniciarSecion() {
        var email = correo.text.toString().trim()
        var password = contrasena.text.toString().trim()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            if (validadarEmail(email)) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {

                        /*iniciar activity y enviar informacion*/
                        val principal = Intent(this, PrincipalActivity::class.java).apply {
                            putExtra("usuario_correo", email)
                        }
                        startActivity(principal)
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(
                            applicationContext,
                            "Sus credenciales son incorrectas",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Correo mal estructurado",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(
                applicationContext,
                "Ingrese su correo y contraseña",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun irRegistro() {
        //iniciar el activity
        val registro = Intent(this, RegistroActivity::class.java)
        startActivity(registro)
    }

    private fun validadarEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showHideText() {

    }
}