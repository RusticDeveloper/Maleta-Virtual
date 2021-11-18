package com.example.maletavirtual

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PrincipalActivity : AppCompatActivity(),fragmentListener {
    private val db = Firebase.firestore
    private lateinit var botonSalir: Button
    private lateinit var newBag: FloatingActionButton
    private lateinit var bienvenida: TextView
    private lateinit var tabs: TabLayout
    private lateinit var fragmentView: ViewPager2
    private val maletas= mutableListOf<Bags>()
    private val maletasID= mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        /*ocultando la barra de titulo*/
        supportActionBar?.hide()
        /*relacion con la vista*/
        bienvenida = findViewById(R.id.text_bienbenida)
        botonSalir = findViewById(R.id.logout_boton)
        newBag = findViewById(R.id.nueva_maleta_boton)
        tabs = findViewById(R.id.tab_bag)
        fragmentView = findViewById(R.id.view_frm)

        /*configuracion inicial*/
        configuracionInicial()
        /*cerrar secion*/
        botonSalir.setOnClickListener {
            cerrarSesion()
        }
/*funcionalidad nueva maleta*/
        newBag.setOnClickListener {
            val userMail = intent.getStringExtra("usuario_correo")
            val nuevaMaleta=Intent(this,NuevaMaletaActivity::class.java).apply {
                putExtra("user_mail",userMail)
            }
            startActivity(nuevaMaleta)
            finish()
        }

    }

    private fun configuracionInicial() {
        /*obtener el valor pasado por el activity*/
        val userMail = intent.getStringExtra("usuario_correo")

        /*definiendo collecciones de referencia*/
        val userRef = db.collection("usuarios").document(userMail!!)

        /*configurando los fragments con info*/
        val fragAdapter=FragmentAdapter(supportFragmentManager,lifecycle)
        fragmentView.adapter=fragAdapter
        TabLayoutMediator(tabs,fragmentView){tab,position->
            when(position){
                0->{tab.text="Maletas"}
                1->{tab.text="Maletas de Grupo"}
            }
        }.attach()
        /*obteniendo y mostrando el nombre para mostrarlo en pantalla*/
        userRef.get()
            .addOnSuccessListener {
                bienvenida.setText("Bienvenido " + it.get("nombre").toString())
            }
            .addOnFailureListener {
                val resultado = it.toString()
                bienvenida.setText("Bienvenido ..." .toString())
            }
    }

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        onBackPressed()
    }

    override fun getEmail(): String {
        val userMail = intent.getStringExtra("usuario_correo")
        return userMail.toString()
    }
}