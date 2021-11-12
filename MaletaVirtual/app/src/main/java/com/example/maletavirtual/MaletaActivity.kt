package com.example.maletavirtual

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MaletaActivity : AppCompatActivity() {
    /*variables globales*/
    private val db = Firebase.firestore
    private lateinit var nombreMoch: TextView
    private lateinit var listaCategorias: Spinner
    private val listaCategory= mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maleta)
        /*desabilitar el barra de actiones*/
        supportActionBar?.hide()
        /*relacion con la vista*/
        nombreMoch = findViewById(R.id.nombreMaleta)
        listaCategorias = findViewById(R.id.sp_categorias)
        /*configuracion inicial e la activity*/
        configuracionInicial()
    }

    /*funciones de accion*/
    private fun configuracionInicial() {
        /*obtener el valor pasado por el activity*/
        val mochID = intent.getStringExtra("bagID")
        /*definiendo collecciones de referencia*/
        val userRef = db.collection("mochilas").document(mochID!!)
        val catRef = db.collection("categorias")

        userRef
            .get()
            .addOnSuccessListener {
                nombreMoch.setText(it.get("nombre_maleta").toString())
            }
            .addOnFailureListener {
                Toast.makeText(this, "Ocurrio este error" + it.toString(), Toast.LENGTH_LONG).show()
            }
        /*hacer que el spinner se llener con los datos*/
        catRef.get()
            .addOnSuccessListener {result->
                for (document in result){
                    listaCategory.add(document.get("nombre_categoria").toString())
                }
                val spAdapter=ArrayAdapter(this,R.layout.spinner_item,listaCategory)
                listaCategorias.adapter=spAdapter
                listaCategorias.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, posision: Int, p3: Long) {
                        Toast.makeText(applicationContext,listaCategory[posision],Toast.LENGTH_LONG).show()
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }

                }
                listaCategorias.onItemClickListener=object:AdapterView.OnItemClickListener{
                    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        Toast.makeText(applicationContext,"usted a tocado el elemento..."+listaCategory[p2],Toast.LENGTH_LONG).show()
                    }

                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Ocurrio este error" + it.toString(), Toast.LENGTH_LONG).show()
            }
    }
}