package com.example.maletavirtual

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.toptoche.searchablespinnerlibrary.SearchableSpinner

class NuevoArticuloActivity : AppCompatActivity() {
    /*contrato para recibir los cambios de los articulos*/
    val itemResult =registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
            if (result?.resultCode == Activity.RESULT_OK) {
                poblarSpinnerArticulos()
            } else {
                Toast.makeText(
                    this,
                    "No hubo cambios en los articulos existentes",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    private val db = Firebase.firestore
    private lateinit var btnCancelar: Button
    private lateinit var btnAgregarArticulo: Button
    private lateinit var btnCrearArticulo: Button
    private lateinit var categoriaItem: TextView
    private lateinit var sp_item: SearchableSpinner
    private lateinit var cantidadItems: EditText
    private var indiceItemActual: Int = 0
    private var categoriaActual: String = ""
    private val listaItems = mutableListOf<String>()
    private val listaItemsID = mutableListOf<String>()
    private val listaNombresCat = mutableListOf<String>()
    private val listaIdsCat = mutableListOf<String>()
    private val listaItemCat = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_articulo)
        /*ocultar barra de accion*/
        supportActionBar?.hide()
        /*relacion con la vista*/
        btnCancelar = findViewById(R.id.add_item_cancel)
        btnAgregarArticulo = findViewById(R.id.add_item_button)
        btnCrearArticulo = findViewById(R.id.launch_create_button)
        categoriaItem = findViewById(R.id.tv_cat_item)
        sp_item = findViewById(R.id.sp_item_chooser)
        cantidadItems = findViewById(R.id.et_cantidad_items)
        /*configuracion inicial*/
        confInicial()
    }

    /*funcionamiento de los botones*/
    private fun confInicial() {
        /*llenar los articlos dentro del spiner*/
        poblarSpinnerArticulos()
        /*funcionalidad botones*/
        btnCancelar.setOnClickListener {
            cancelar()
        }
        btnCrearArticulo.setOnClickListener {
            irCrearArticulo()
        }
        btnAgregarArticulo.setOnClickListener {
            AgregarAMaleta()
        }

    }

    /*funcionalidades*/
    private fun irCrearArticulo() {
        val nuevoArt = Intent(this, CrearNuevoArticuloActivity::class.java)
        itemResult.launch(nuevoArt)
    }

    private fun cancelar() {

        finish()
    }

    private fun poblarSpinnerArticulos() {

        /*referencia a la base de datos*/
        val artRef = db.collection("articulos")
        val catRef = db.collection("categorias")

        /*obtiene una lista de ariculos*/
        artRef.get()
            .addOnSuccessListener { documents ->
                for (item in documents) {
                    listaItemsID.add(item.id)
                    listaItems.add(item.get("nombre_articulo").toString())
                    listaItemCat.add(item.get("id_categoria").toString())
                }
                /*llena el spinner por medio de un adaptador*/
                val adaptador = ArrayAdapter(this, R.layout.spinner_item, listaItems)
                sp_item.adapter = adaptador
                /*obtiene una lista de categorias*/
                catRef.get()
                    .addOnSuccessListener { cats ->
                        for (catI in cats) {
                            listaNombresCat.add(catI.get("nombre_categoria").toString())
                            listaIdsCat.add(catI.id)
                        }
                        categoriaActual=listaNombresCat[0]
                        /*funcionalidad del spinner*/
                        sp_item.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                @SuppressLint("SetTextI18n")
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    indiceItemActual = position
                                    val idcat=listaItemCat[indiceItemActual]
                                    val posidcat=listaIdsCat.indexOf(idcat)
                                    val catname=listaNombresCat[posidcat]
                                    categoriaActual=catname
                                    categoriaItem.text = "Categoria: $catname"

                                }
                                override fun onNothingSelected(p0: AdapterView<*>?) {}
                            }
                        sp_item.setTitle("Selecciona un articulo")
                        sp_item.setPositiveButton("Cerrar")
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Ocurrio este error" + it.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                Log.i("listaCategorias", listaNombresCat.toString())

            }
            .addOnFailureListener {
                Toast.makeText(this, "Ocurrio este error" + it.message, Toast.LENGTH_LONG).show()
            }
    }

    private fun AgregarAMaleta() {
/*obtener el valor pasado por el activity*/
        val mochID = intent.getStringExtra("idMaleta").toString()
        val ItemID = listaItemsID[indiceItemActual]
        val nombreItem=sp_item.selectedItem.toString()
        val cantidad = cantidadItems.text.toString().trim()
        /*referencia a la base de datos*/
        val bagRef = db.collection("maletas").document(mochID)
        if (nombreItem.isNotEmpty() && cantidad.isNotEmpty() && categoriaActual.isNotBlank()) {
            bagRef.update(
                "articulos", FieldValue.arrayUnion(
                    BagItems(ItemID,nombreItem,categoriaActual,cantidad,false)
                )
            )
                .addOnSuccessListener {
                    Toast.makeText(this, "Articulo agregado a la maleta", Toast.LENGTH_LONG).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Ocurrio este error" + it.message, Toast.LENGTH_LONG)
                        .show()
                }
        } else {
            Toast.makeText(this, "El articulo debe tener una cantidad y categoria seleccionada", Toast.LENGTH_LONG).show()
        }
    }
}