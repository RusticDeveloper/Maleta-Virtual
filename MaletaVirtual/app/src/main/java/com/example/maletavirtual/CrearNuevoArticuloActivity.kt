package com.example.maletavirtual

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.toptoche.searchablespinnerlibrary.SearchableSpinner

class CrearNuevoArticuloActivity : AppCompatActivity() {
    private val catResult=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult?->
        if (result?.resultCode == Activity.RESULT_OK){
            poblarSpineer()
        }else{
            Toast.makeText(this, "No se hicieron cambios en categorias", Toast.LENGTH_LONG).show()
        }
    }

    private val db = Firebase.firestore
    private lateinit var btnCancelar: Button
    private lateinit var btnCrearCategoria: Button
    private lateinit var btnCrearArticulo: Button
    private lateinit var nombreItem: EditText
    private lateinit var spinnerNombreCategoria: SearchableSpinner
    private var indiceCategoriaActual: Int = 0
    private val listaCategorias = mutableListOf<String>()
    private val listaCategoriaIDs = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_nuevo_articulo)
        /*ocultar barra de accion*/
        supportActionBar?.hide()
        /*relacion con la vista*/
        btnCancelar = findViewById(R.id.cancel_create_item_button)
        btnCrearArticulo = findViewById(R.id.create_item_button)
        btnCrearCategoria = findViewById(R.id.create_cat_item_button)
        nombreItem = findViewById(R.id.et_item_name)
        spinnerNombreCategoria = findViewById(R.id.sp_item_cat)

        /*Configuracion de inicio de la pantalla*/
        confInicial()
    }

    private fun confInicial() {
        /*llena el spinner de categorias*/
        poblarSpineer()
        /*funcion botones*/
        btnCancelar.setOnClickListener {
            cancelar()
        }
        btnCrearCategoria.setOnClickListener {
            irCategoria()
        }
        btnCrearArticulo.setOnClickListener {
            crearArticulo()
        }
        spinnerNombreCategoria.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                indiceCategoriaActual=position
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }
        spinnerNombreCategoria.setTitle("Selecciona una categoria")
        spinnerNombreCategoria.setPositiveButton("Cerrar")
    }

    private fun cancelar() {
        finish()
    }

    private fun irCategoria() {
        val nuevaCat = Intent(this, NuevaCategoriaActivity::class.java)
        catResult.launch(nuevaCat)
    }

    private fun poblarSpineer(){
        listaCategorias.clear()
        /*referencia a la base de datos*/
        val catRef = db.collection("categorias")
        /*llenado el spinner*/
        catRef.get()
            .addOnSuccessListener { categ ->
                for (catgrItem in categ) {
                    listaCategoriaIDs.add(catgrItem.id)
                    listaCategorias.add(catgrItem.get("nombre_categoria").toString())
                }
                val adaptador = ArrayAdapter(this, R.layout.spinner_item, listaCategorias)
                spinnerNombreCategoria.adapter = adaptador
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Ocurrio un error mientras se recuperaban los datos",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
    private fun crearArticulo() {
//        val categoria=spinnerNombreCategoria.selectedItem.toString()
        val categoria=listaCategoriaIDs[indiceCategoriaActual]
        val nombreArriculo=nombreItem.text.toString().trim()
        if (nombreArriculo.isNotEmpty()) {
            db.collection("articulos").add(
                hashMapOf(
                    "nombre_articulo" to nombreArriculo,
                    "id_categoria" to categoria
                )
            )
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Articulo creado correctamente",
                        Toast.LENGTH_LONG
                    ).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Error:"+it.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
        }else{
            Toast.makeText(
                this,
                "Nombra el nuevo Articulo",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}