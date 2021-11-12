package com.example.maletavirtual

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class listaMaletaFragment : Fragment() {
    private val db = Firebase.firestore
    private var listener:fragmentListener?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
/*preparacion de la vista y maletas para llenar en el listview */
        val vista = inflater.inflate(R.layout.fragment_lista_maleta, container, false)
        val maletas = mutableListOf<Bags>()
        val maletasID = mutableListOf<String>()
        val usrMail: String? =listener?.getEmail()
        val bagRef = db.collection("maletas")
        /*relacion listview*/
        var bagList: ListView = vista.findViewById(R.id.lista_moch)

        /*obtencion de los datos para llenar el listView*/
        bagRef
            .whereEqualTo("propietario",usrMail)
            .whereEqualTo("tipo","regular")
            .get().addOnSuccessListener {result->
                for (document in result){
                    maletasID.add(document.id)
                    maletas.add(Bags(document.get("nombre_maleta").toString()))
                    Log.i("documentos","${document.id} => ${document.data}")
                }
                /*llenando la listview*/
                val adap = BagAdapter(vista.context, maletas)
                bagList.adapter = adap
                bagList.setOnItemClickListener { adapterView, view, i, l ->
                    Toast.makeText(
                        view.context,
                        "tocaste el :" + maletas[i].NombreMaleta,
                        Toast.LENGTH_LONG
                    ).show()
                    val maletasItem = Intent(view.context,MaletaActivity::class.java).apply {
                        putExtra("bagID",maletasID[i])
                    }
                    startActivity(maletasItem)
                }
                bagList.setOnItemLongClickListener { adapterView, view, i, l ->
                    Toast.makeText(
                        view.context,
                        "tocaste el " + maletas[i].NombreMaleta+" Largo y tendido",
                        Toast.LENGTH_LONG
                    ).show()
                    true
                }
            }.addOnFailureListener {
                val error=it.toString()
                Toast.makeText(vista.context, error, Toast.LENGTH_LONG).show()
            }
        // Inflate the layout for this fragment
        return vista
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is fragmentListener){
            listener=context
        }
    }
    override fun onDetach() {
        super.onDetach()
        listener=null
    }

}