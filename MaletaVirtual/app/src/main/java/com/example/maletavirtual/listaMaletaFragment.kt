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

class listaMaletaFragment : Fragment() {
    private val db = Firebase.firestore
    private var listener: fragmentListener? = null
    private lateinit var bagList: ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
/*preparacion de la vista y maletas para llenar en el listview */
        val vista = inflater.inflate(R.layout.fragment_lista_maleta, container, false)
        /*relacion listview*/
        bagList= vista.findViewById(R.id.lista_moch)

        reloadItems(vista)


        // Inflate the layout for this fragment
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

    fun reloadItems(vista:View){

        val usrMail: String? = listener?.getEmail()
        val bagRef = db.collection("maletas")
        val maletas = mutableListOf<Bags>()
        val maletasID = mutableListOf<String>()
        /*obtencion de los datos para llenar el listView*/
        bagRef
            .whereEqualTo("propietario", usrMail)
            .whereEqualTo("tipo", "regular")
            .get().addOnSuccessListener { result ->
                for (document in result) {
                    maletasID.add(document.id)
                    maletas.add(Bags(document.get("nombre_maleta").toString()))
                    Log.i("documentos", "${document.id} => ${document.data}")
                }
                /*llenando la listview*/
                val adap = BagAdapter(vista.context, maletas)
                bagList?.adapter = adap
                bagList?.setOnItemClickListener { adapterView, view, i, l ->
                    val maletasItem = Intent(view.context, MaletaActivity::class.java).apply {
                        putExtra("bagID", maletasID[i])
                        putExtra("tipoVista", "editar")
                        putExtra("estadoMaleta", "simple")
                    }
                    startActivity(maletasItem)
                }
                bagList?.setOnItemLongClickListener { adapterView, view, i, l ->
                    /*dialogo para cambiar el nombre y/o eliminar la mochila
                    * instancia el dialogo y la vista del dialogo*/
                    val dialogoMochila = AlertDialog.Builder(vista.context)
                    val vistaDialogo = layoutInflater.inflate(R.layout.dialogo_elementos, null)
                    dialogoMochila.setView(vistaDialogo)
                    /*relacion con los elementos del dialogo*/
                    val tvValue = vistaDialogo.findViewById<TextView>(R.id.Nombre_elem)
                    val etValue = vistaDialogo.findViewById<EditText>(R.id.edit_nombre_elem)
                    val btnValue = vistaDialogo.findViewById<Button>(R.id.boton_dialogo)
                    val btnDeleteBag = vistaDialogo.findViewById<Button>(R.id.del_bag_button)
                    val nuevoNombreMochila = maletas[i].NombreMaleta
                    val idMaleta = maletasID[i]
                    /* configuracion y funcionalidad del dialogo */
                    tvValue.text = nuevoNombreMochila
                    /*crea y muestra el dialogo*/
                    val adm: AlertDialog = dialogoMochila.create()
                    adm.show()
                    /*boton de cambio de nombre dentro del dialogo*/
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
                                .addOnFailureListener {
                                    Toast.makeText(
                                        view.context,
                                        "error:" + it.toString(),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        }
                    }
                    /*boton de eliminar dentro del dialogo*/
                    btnDeleteBag.setOnClickListener {

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