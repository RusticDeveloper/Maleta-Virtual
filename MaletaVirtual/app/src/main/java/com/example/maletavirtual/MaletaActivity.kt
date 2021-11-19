package com.example.maletavirtual

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentContainerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.toptoche.searchablespinnerlibrary.SearchableSpinner

class MaletaActivity : AppCompatActivity(),itemFragmentListenner {

    /*contrato para recibir los cambios de los articulos*/
    private val catResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
            if (result?.resultCode == Activity.RESULT_OK) {
                val catID=listaCategoryIDs[posicionSpinner]
                val catname=listaCategory[posicionSpinner]
                val frag=elementosMaletaFragment()
                val bundle=Bundle()
                bundle.putString("catID",catID)
                bundle.putString("catName",catname)
                bundle.putString("tipoVista",intent.getStringExtra("tipoVista").toString())
                bundle.putString("bagID",intent.getStringExtra("bagID").toString())
                val fragContainer=supportFragmentManager.beginTransaction()
                frag.arguments=bundle
                fragContainer.replace(R.id.itemsViewContainer,frag).commit()
            } else {
                Toast.makeText(
                    this,
                    "No hubo cambios en los articulos existentes",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    /*variables globales*/
    private val db = Firebase.firestore
    private lateinit var nombreMoch: TextView
    private lateinit var listaCategorias: SearchableSpinner
    private lateinit var botonAgregarElemento: Button
    private lateinit var botonAgregarGrupo: Button
    private lateinit var fragmentHost: FragmentContainerView

    private var posicionSpinner: Int=0
    private val listaCategory= mutableListOf<String>()
    private val listaCategoryIDs= mutableListOf<String>()
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
        fragmentHost = findViewById(R.id.itemsViewContainer)
        //TODO hacer que se verifiquen los valores recividos desde las otras activities
        /*obtener el valor pasado por el activity*/
        val viewType = intent.getStringExtra("tipoVista").toString()
        val bagState = intent.getStringExtra("estadoMaleta").toString()
        if(viewType.equals("editar") && bagState.equals("simple")){
            /*configuracion inicial e la activity*/
            configuracionInicial()
        }else if(viewType.equals("editar") && bagState.equals("grupal")){
            fragmentHost.isEnabled=true
            botonAgregarGrupo.visibility=View.GONE
            /*configuracion inicial e la activity*/
            configuracionInicial()
        }else if (viewType.equals("ver") && bagState.equals("grupal")){
            fragmentHost.isEnabled=false
            botonAgregarGrupo.visibility=View.GONE
            botonAgregarElemento.visibility=View.GONE
            /*configuracion inicial e la activity*/
            configuracionInicial()
        }
    }

    /*funciones de accion*/
    private fun configuracionInicial() {
/*obtener el valor pasado por el activity*/
        val mochID = intent.getStringExtra("bagID")
        /*definiendo collecciones de referencia*/
        val bagRef = db.collection("maletas").document(mochID!!)
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
                .apply {
                    putExtra("idMaleta",mochID)
                }
//            startActivity(elem)
            catResult.launch(elem)
//            finish()

        }
        botonAgregarGrupo.setOnClickListener {
            val elemg=Intent(this,CrearUnirseGrupoActivity::class.java)
                .apply {
                    putExtra("idMaleta",mochID)
                }
            catResult.launch(elemg)
        }
        val catRef = db.collection("categorias")
        /*hacer que el spinner se llene con los datos*/
        catRef.get()
            .addOnSuccessListener {result->
                for (document in result){
                    listaCategory.add(document.get("nombre_categoria").toString())
                    listaCategoryIDs.add(document.id)
                }
                val spAdapter=ArrayAdapter(this,R.layout.spinner_item,listaCategory)
                listaCategorias.adapter=spAdapter
                listaCategorias.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, posision: Int, p3: Long) {
                        posicionSpinner=posision
                        val catID=listaCategoryIDs[posision]
                        val catname= p0?.selectedItem.toString()
                        val frag=elementosMaletaFragment()
                        val bundle=Bundle()
                        bundle.putString("catID",catID)
                        bundle.putString("catName",catname)
                        bundle.putString("bagID",mochID)
                        bundle.putString("tipoVista",intent.getStringExtra("tipoVista").toString())
                        val fragContainer=supportFragmentManager.beginTransaction()
                        frag.arguments=bundle
                        fragContainer.replace(R.id.itemsViewContainer,frag).commit()

                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }
                }
                listaCategorias.setTitle("Categorias")
                listaCategorias.setPositiveButton("Cerrar")

            }
            .addOnFailureListener {
                Toast.makeText(this, "Ocurrio este error" + it.toString(), Toast.LENGTH_LONG).show()
            }

    }
    override fun sendBagID(): String {
        //envia el ID de la maleta
        return intent.getStringExtra("bagID").toString()
    }

}