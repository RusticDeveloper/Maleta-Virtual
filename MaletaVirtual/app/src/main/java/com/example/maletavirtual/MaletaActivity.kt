package com.example.maletavirtual

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.toptoche.searchablespinnerlibrary.SearchableSpinner

class MaletaActivity : AppCompatActivity(),itemFragmentListenner {
    /*variables globales*/
    private val db = Firebase.firestore
    private lateinit var nombreMoch: TextView
    private lateinit var listaCategorias: SearchableSpinner
    private lateinit var botonAgregarElemento: Button
    private lateinit var botonAgregarGrupo: Button
    private val listaCategory= mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maleta)
        /*desabilitar el barra de actiones*/
        supportActionBar?.hide()
        /*relacion con la vista*/
        nombreMoch = findViewById(R.id.nombreMaleta)
        listaCategorias = findViewById(R.id.sp_categorias)
        botonAgregarElemento = findViewById(R.id.add_item)
        botonAgregarGrupo = findViewById(R.id.add_group)
        /*configuracion inicial e la activity*/
        configuracionInicial()


    }

    /*funciones de accion*/
    private fun configuracionInicial() {
        /*obtener el valor pasado por el activity*/
        val mochID = intent.getStringExtra("bagID")
        /*definiendo collecciones de referencia*/
        val bagRef = db.collection("maletas").document(mochID!!)
        val catRef = db.collection("categorias")
/*obtener el nombre de la mochila*/
        bagRef
            .get()
            .addOnSuccessListener {
                nombreMoch.text = it.get("nombre_maleta").toString()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Ocurrio este error" + it.toString(), Toast.LENGTH_LONG).show()
            }
        /*Funcionalidad de los botones*/
        botonAgregarElemento.setOnClickListener {
            val elem=Intent(this,NuevoArticuloActivity::class.java)
            startActivity(elem)
        }
        botonAgregarGrupo.setOnClickListener {
            Toast.makeText(this, "Tocaste el boton de de agregar a grupo" , Toast.LENGTH_LONG).show()
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
                listaCategorias.setTitle("Categorias")
                listaCategorias.setPositiveButton("Cerrar")

            }
            .addOnFailureListener {
                Toast.makeText(this, "Ocurrio este error" + it.toString(), Toast.LENGTH_LONG).show()
            }
    }

    override fun sendItemList(listaElems: MutableList<BagItems>): MutableList<BagItems> {
            Toast.makeText(this, "hacer que mande una lista de maletas al fragment", Toast.LENGTH_LONG).show()
            return mutableListOf(BagItems("fgdf","fdsfds"))
    }


}