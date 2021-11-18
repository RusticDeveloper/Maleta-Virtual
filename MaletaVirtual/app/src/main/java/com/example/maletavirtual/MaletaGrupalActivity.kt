package com.example.maletavirtual

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MaletaGrupalActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private lateinit var listaMaletas: ListView
    private  val listaNombreMaleta = mutableListOf<String>()
    private  val listaNombreMaletaIds = mutableListOf<String>()

    private val groupRef=db.collection("grupos")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maleta_grupal)
        /*ocultar actionbar*/
        supportActionBar?.hide()
        /*relacion con la vista*/
        listaMaletas=findViewById(R.id.lista_maletas)
        /*Configuracion Inicial*/
        confInicial()
    }
    private fun confInicial(){
        val bagID = intent?.getStringExtra("bagID").toString()
        val bagRef=db.collection("maletas").document(bagID!!)
        val rawBagRef=db.collection("maletas")
        /*lista de maletas*/
        rawBagRef.get()
            .addOnSuccessListener {maletas->
            var ideMaletas= mutableListOf<String>()
            var nombreMaletas= mutableListOf<String>()
            var grupoIDsMaletas= mutableListOf<String>()
              for(m in maletas){
                  nombreMaletas.add(m .get("nombre_maleta").toString())
                  ideMaletas.add(m.id)
              }

                /* obtiene la lista de maletas de los grupos*/
                groupRef
                    .whereArrayContains("id_maletas",bagID)
                    .get()
                    .addOnSuccessListener {maletas->
                        /*convierte la maleta en objeto de tipo ItemsTemplate*/
                        val objetoMaleta = maletas.toObjects(groupTemplate::class.java)
                        val listArticulos = objetoMaleta[0]?.id_maletas
                        if (listArticulos != null) {
                            for (k in listArticulos){
                                grupoIDsMaletas.add(k)
                            }
                        }

                        for(o in grupoIDsMaletas){
                            var idelem= ideMaletas.indexOf(o)
                            if (idelem!=-1){
                                listaNombreMaletaIds.add(ideMaletas[idelem])
                                listaNombreMaleta.add(nombreMaletas[idelem])
                            }
                        }

                        val adaptador = GBagAdapter(this ,listaNombreMaleta)
                        listaMaletas.adapter=adaptador
                        listaMaletas.setOnItemClickListener { adapterView, view, i, l ->
                            //TODO enviar algunos parametros
                            var nombreM=adapterView.getItemAtPosition(i).toString()
                            var indexSelected=listaNombreMaleta.indexOf(nombreM)
                            var idmaletaActual=listaNombreMaletaIds[indexSelected]
                            if(idmaletaActual.equals(bagID)){
                                val elem= Intent(this,MaletaActivity::class.java)
                                    .apply {
                                        putExtra("bagID",bagID)
                                        putExtra("tipoVista","editar")
                                        putExtra("estadoMaleta", "grupal")
                                    }
                                    startActivity(elem)
                            }else{
                                val elem= Intent(this,MaletaActivity::class.java)
                                    .apply {
                                        putExtra("bagID",idmaletaActual)
                                        putExtra("tipoVista","ver")
                                        putExtra("estadoMaleta", "grupal")
                                    }
                                startActivity(elem)
                            }
                        }
                    }
                    .addOnFailureListener { Toast.makeText(this, "Error: "+it.toString(), Toast.LENGTH_LONG).show() }

            }
            .addOnFailureListener { Toast.makeText(this, "Error: "+it.toString(), Toast.LENGTH_LONG).show() }

    }
}