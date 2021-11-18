package com.example.maletavirtual

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.zip.Inflater


class listaMaletaGrupalFragment : Fragment() {
    private val db = Firebase.firestore
    private var listener: fragmentListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /*preparacion de la vista y maletas para llenar en el listview */
        val vista = inflater.inflate(R.layout.fragment_lista_maleta_grupal, container, false)
        /*llenando los datos*/
        reloadItems(vista)
        /*regresara la vista inflada y con los ajustes hechos*/
        return vista
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is fragmentListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun reloadItems(vista: View) {
        val maletas = mutableListOf<Bags>()
        val maletasID = mutableListOf<String>()
        val lideresID = mutableListOf<String>()
        val usrMail: String? = listener?.getEmail()
        var isLeader:String
        val bagRef = db.collection("maletas")
        /*relacion listview*/
        var bagList: ListView = vista.findViewById(R.id.lista_moch_grupo)

        /*obtencion de los datos para llenar el listView*/
        bagRef
            .whereEqualTo("propietario", usrMail)
            .whereEqualTo("tipo", "grupo")
            .get().addOnSuccessListener { result ->
                for (document in result) {
                    lideresID.add(document.get("lider").toString())
                    maletasID.add(document.id)
                    maletas.add(Bags(document.get("nombre_maleta").toString()))
                    Log.i("documentos", "${document.id} => ${document.data}")
                }
                /*llenando la listview*/
                val adap = BagAdapter(vista.context, maletas)
                bagList.adapter = adap
                bagList.setOnItemClickListener { adapterView, view, i, l ->
                    Log.i("listaLideres",lideresID.toString())
                    //TODO-- hacer que pregunte si va a ir a la lista de maletas normal o la de grupo
                    if (lideresID[i].trim().equals(usrMail)){
                        Toast.makeText(adapterView.context, "Eres Lider" , Toast.LENGTH_LONG).show()
                    val maletasItem = Intent(view.context, MaletaGrupalActivity::class.java).apply {
                        putExtra("bagID", maletasID[i])
                    }
                    startActivity(maletasItem)
                    }else{
                        Toast.makeText(adapterView.context, "No eres lider" , Toast.LENGTH_LONG).show()
                    val maletasItem = Intent(view.context, MaletaActivity::class.java).apply {
                        putExtra("bagID", maletasID[i])
                        putExtra("tipoVista", "editar")
                        putExtra("estadoMaleta", "grupal")
                    }
                    startActivity(maletasItem)
                    }

                }
                bagList.setOnItemLongClickListener { adapterView, view, i, l ->
                    /*dialogo para cambiar el nombre y/o eliminar la mochila
                    * instancia el dialogo y la vista del dialogo*/
                    val dialogoMochila = AlertDialog.Builder(vista.context)
                    val vistaDialogo = layoutInflater.inflate(R.layout.dialogo_elementos, null)
                    dialogoMochila.setView(vistaDialogo)
                    /*relacion con los elementos del dialogo*/
                    val tvValue = vistaDialogo.findViewById<TextView>(R.id.Nombre_elem)
                    val etValue = vistaDialogo.findViewById<EditText>(R.id.edit_nombre_elem)
                    val btnValue = vistaDialogo.findViewById<Button>(R.id.boton_dialogo)
                    val btnDeleteGBag = vistaDialogo.findViewById<Button>(R.id.del_bag_button)
                    val nuevoNombreMochila = maletas[i].NombreMaleta
                    val idMaleta = maletasID[i]
                    /* configuracion y funcionalidad del dialogo */
                    tvValue.text = nuevoNombreMochila
                    /*crea y muestra el dialogo*/
                    val adm: AlertDialog = dialogoMochila.create()
                    adm.show()
                    btnValue.setOnClickListener {
                        if (etValue.text.toString().isEmpty()) {
                            Toast.makeText(
                                view.context,
                                "Ingrese un nuevo nombre para la mochila",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            bagRef.document(idMaleta).update(
                                "nombre_maleta", etValue.text.toString().trim()
                            )
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        view.context,
                                        "Nombre cambiado exitosamente",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    /*cerrando el dialog*/
                                    adm.dismiss()
                                    /*recargando los elementos*/
                                    reloadItems(vista)
                                }
                        }
                    }
                    btnDeleteGBag.setOnClickListener {
                        bagRef.document(idMaleta).delete()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    view.context,
                                    "Mochila eliminada",
                                    Toast.LENGTH_LONG
                                ).show()
                                /*cerrando el dialog*/
                                adm.dismiss()
                                /*recargando los elementos*/
                                reloadItems(vista)

                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    view.context,
                                    "error:" + it.toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }

                    true
                }

            }.addOnFailureListener {
                val error = it.toString()
                Toast.makeText(vista.context, error, Toast.LENGTH_LONG).show()
            }
    }
}