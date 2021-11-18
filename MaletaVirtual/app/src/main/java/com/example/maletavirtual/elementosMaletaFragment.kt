package com.example.maletavirtual

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class elementosMaletaFragment : Fragment() {
    private val db = Firebase.firestore
    private var listener: itemFragmentListenner? = null
    private lateinit var listViewItems:ListView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val vista = inflater.inflate(R.layout.fragment_elementos_maleta, container, false)
        listViewItems = vista.findViewById(R.id.lista_items)
        val habilitado=arguments?.getString("tipoVista").toString()
        if(habilitado.equals("ver")){
            listViewItems.isEnabled=false
            /*estableciendo la funcion de la vista*/
            llenarLista(vista)
        }else{
            /*estableciendo la funcion de la vista*/
            llenarLista(vista)
        }


        return vista
    }

    override fun onAttach(context: Context) {
        if (context is itemFragmentListenner) {
            listener = context
        }
        super.onAttach(context)
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    fun llenarLista(vista:View) {
//TODO-- hacer que esta funcion cargue los datos cuando quiera
        val mochID = arguments?.getString("bagID").toString()
        val catname = arguments?.getString("catName").toString()
        val itemsID = mutableListOf<String>()
        val listaItems = mutableListOf<BagItems>()
        val bagRef = db.collection("maletas").document(mochID)
        Toast.makeText(vista?.context, "nombre de la categoria: " + catname, Toast.LENGTH_LONG).show()

        bagRef
            .get()
            .addOnSuccessListener { document ->
                /*convierte la maleta en objeto de tipo ItemsTemplate*/
                val objetoMaleta = document.toObject(ItemsTemplate::class.java)
                val listArticulos = objetoMaleta?.articulos
                Log.i("valores", listArticulos.toString())
                if (listArticulos != null) {
                    for (art in listArticulos) {
                        if (art.nombreCategoria.equals(catname)){
                            listaItems.add(art)
                            itemsID.add(art.idArticulo)
                        }
                    }
                }
                //TODO-- hacer un metodo que llene el listview de acuerdo con el valor elegido dentro del listview de categorias
                /*agregar datos al listview*/
                val adaptador = BagItemAdapter(vista.context, listaItems)
                listViewItems.adapter = adaptador
                /*Accion al dar click */
                listViewItems.setOnItemClickListener { adapterView, view, i, l ->
                    val item:BagItems= adapterView.getItemAtPosition(i) as BagItems
                    //TODO-- hacer que se muesatre tachado el texto
                    if(item.empacado){
                        val nuevoItem=BagItems(item.idArticulo,item.nombreArticulo,item.nombreCategoria,item.cantidadItem,false)
                        bagRef.update("articulos",FieldValue.arrayRemove(item))
                            .addOnSuccessListener {
                                bagRef.update("articulos",FieldValue.arrayUnion(nuevoItem))
                                    .addOnSuccessListener {
                                        Toast.makeText(vista?.context, "Funciono el cambio de estado", Toast.LENGTH_LONG).show()
                                        llenarLista(vista)
                                    }
                                    .addOnFailureListener { Toast.makeText(vista?.context, "Error: "+it.toString(), Toast.LENGTH_LONG).show() }
                            }
                            .addOnFailureListener { Toast.makeText(vista?.context, "Error: "+it.toString(), Toast.LENGTH_LONG).show() }
                    }else{
                        val nuevoItem=BagItems(item.idArticulo,item.nombreArticulo,item.nombreCategoria,item.cantidadItem,true)
                        bagRef.update("articulos",FieldValue.arrayRemove(item))
                            .addOnSuccessListener {
                                bagRef.update("articulos",FieldValue.arrayUnion(nuevoItem))
                                    .addOnSuccessListener {
                                        Toast.makeText(vista?.context, "Funciono el cambio de estado a verdadero", Toast.LENGTH_LONG).show()
                                        llenarLista(vista)
                                    }
                                    .addOnFailureListener { Toast.makeText(vista?.context, "Error: "+it.toString(), Toast.LENGTH_LONG).show() }
                            }
                            .addOnFailureListener { Toast.makeText(vista?.context, "Error: "+it.toString(), Toast.LENGTH_LONG).show() }
                    }

                }
                /*accion al dar un click sostenido*/
                listViewItems.setOnItemLongClickListener { adapterView, view, i, l ->
                    val item:BagItems= adapterView.getItemAtPosition(i) as BagItems
                    //TODO-- hacer que se muestre el dialogo de actualizacion eliminacion de nombre

                    /*dialogo para cambiar la cantidad y/o eliminar la mochila
                   * instancia el dialogo y la vista del dialogo*/
                    val dialogoMochila = AlertDialog.Builder(vista.context)
                    val vistaDialogo = layoutInflater.inflate(R.layout.dialogo_elementos_maleta, null)
                    dialogoMochila.setView(vistaDialogo)
                    /*relacion con los elementos del dialogo*/
                    val tvElemName = vistaDialogo.findViewById<TextView>(R.id.tv_nombre_articulo)
                    val etCantidad = vistaDialogo.findViewById<EditText>(R.id.et_cantidad)
                    val btnDelete = vistaDialogo.findViewById<Button>(R.id.eliminar_boton)
                    val btnUpdateNumber = vistaDialogo.findViewById<Button>(R.id.actualizar_boton)
                    val nuevoNombreArticulo = item.nombreArticulo
                    val idMaleta = item.idArticulo
                    /* texto para saber que articulo modificamos o eliminamos */
                    tvElemName.text = nuevoNombreArticulo
                    /*crea y muestra el dialogo*/
                    val adm: AlertDialog = dialogoMochila.create()
                    adm.show()
                    btnUpdateNumber.setOnClickListener {
                        if (etCantidad.text.toString().isEmpty()) {
                            Toast.makeText(
                                view.context,
                                "Ingrese un numero",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            bagRef.update(
                                "articulos", FieldValue.arrayRemove(item)
                            )
                                .addOnSuccessListener {
                                    val nuevoItem=BagItems(item.idArticulo,item.nombreArticulo,item.nombreCategoria,etCantidad.text.toString(),item.empacado)
                                    bagRef.update("articulos",FieldValue.arrayUnion(nuevoItem))
                                        .addOnSuccessListener {
                                            Toast.makeText(vista?.context, "Funciono el cambio de cantidad", Toast.LENGTH_LONG).show()
                                            /*cerrando el dialog*/
                                            adm.dismiss()
                                            /*recargando los elementos*/
                                            llenarLista(vista)
                                        }
                                        .addOnFailureListener { Toast.makeText(vista?.context, "Error: "+it.toString(), Toast.LENGTH_LONG).show() }

                                }
                                .addOnFailureListener {Toast.makeText(view.context,"error:" + it.toString(),Toast.LENGTH_LONG).show()
                                }
                        }
                    }
                    btnDelete.setOnClickListener {
                        bagRef.update(
                            "articulos", FieldValue.arrayRemove(item)
                        )
                            .addOnSuccessListener {
                                Toast.makeText(view.context,"Articulo eliminado de la maleta",Toast.LENGTH_LONG).show()
                                /*cerrando el dialog*/
                                adm.dismiss()
                                /*recargando los elementos*/
                                llenarLista(vista)
                            }
                            .addOnFailureListener {Toast.makeText(view.context,"error:" + it.toString(),Toast.LENGTH_LONG).show()}
                    }


                    true
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    vista.context,
                    "Ocurrio este error" + it.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
    }

}