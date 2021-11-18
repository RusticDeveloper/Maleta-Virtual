package com.example.maletavirtual

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CrearUnirseGrupoActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private lateinit var et_groupName:EditText
    private lateinit var btn_createGroup:Button
    private lateinit var et_userMail:EditText
    private lateinit var btn_joinGroup:Button
    private var tv_userMail=""
    private val groupRef=db.collection("grupos")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_unirse_grupo)
        /*coultar la barra de accion*/
        supportActionBar?.hide()
        /*relacion con la vista*/
        et_groupName=findViewById(R.id.et_group_name)
        btn_createGroup=findViewById(R.id.button_create_group)
        et_userMail=findViewById(R.id.et_group_input_user_mail)
        btn_joinGroup=findViewById(R.id.button_join_group)
/*cofiguracion iniciañ*/
        confInicial()
    }
    private fun confInicial(){
        /*obtener el valor pasado por el activity*/
        val bagID = intent?.getStringExtra("idMaleta").toString()
        val bagRef=db.collection("maletas").document(bagID)
        /*referencia a la base de datos*/
        bagRef.get()
            .addOnSuccessListener { bag->
                tv_userMail=bag.get("propietario").toString()

            }
            .addOnFailureListener { Toast.makeText(this, "Error: "+it.toString() , Toast.LENGTH_LONG).show() }
        /*funcion de los botones*/
        btn_createGroup.setOnClickListener { CrearGrupo() }
        btn_joinGroup.setOnClickListener { UnirseGrupo() }
    }
    private fun CrearGrupo(){
        /*obtener el valor pasado por el activity*/
        val bagID = intent?.getStringExtra("idMaleta").toString()
        val bagRef=db.collection("maletas").document(bagID)
        /*recupera el texto de el nombre de grupo*/
        val txt_name_group:String=et_groupName.text.toString().trim()
    /* para saber si esta vacio o no */
        if (txt_name_group.isEmpty()){
            Toast.makeText(this, "El grupo debe tener un nombre!!" , Toast.LENGTH_LONG).show()
        }else{
            groupRef.document(txt_name_group).set(
                hashMapOf(
                    "lider" to tv_userMail,
                    "id_maletas" to mutableListOf(bagID)
                )
            )
                .addOnSuccessListener {
                    Toast.makeText(this, "Grupo creado" , Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { Toast.makeText(this, "Error: "+it.toString() , Toast.LENGTH_LONG).show() }
            bagRef.update("lider",tv_userMail,"tipo","grupo")
                .addOnSuccessListener { Toast.makeText(this, "Maleta Actualizada" , Toast.LENGTH_LONG).show() }
                .addOnFailureListener { Toast.makeText(this, "Error: "+it.toString() , Toast.LENGTH_LONG).show() }
        }
    }
    private fun UnirseGrupo(){
        /*obtener el valor pasado por el activity*/
        val bagID = intent?.getStringExtra("idMaleta").toString()
        val bagRef=db.collection("maletas").document(bagID)
        /*recupera el texto de el nombre de grupo*/
        val txt_g_name=et_userMail.text.trim()
        var lider:String=""
        groupRef.document(txt_g_name.toString()).get()
            .addOnSuccessListener { lider=it.get("lider").toString() }
            .addOnFailureListener { Toast.makeText(this, "Error: "+it.toString() , Toast.LENGTH_LONG).show() }
        if (txt_g_name.isEmpty()){
            Toast.makeText(this, "Escribe el nombre del grupo" , Toast.LENGTH_LONG).show()
        }else{
            groupRef.document(txt_g_name.toString())
                .update("id_maletas",FieldValue.arrayUnion(bagID))
                .addOnSuccessListener {
                    Toast.makeText(this, "Te uniste al grupo" , Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { Toast.makeText(this, "Error: "+it.toString() , Toast.LENGTH_LONG).show() }
            bagRef.update("lider",lider,"tipo","grupo")
                .addOnSuccessListener { Toast.makeText(this, "Maleta Actualizada" , Toast.LENGTH_LONG).show() }
                .addOnFailureListener { Toast.makeText(this, "Error: "+it.toString() , Toast.LENGTH_LONG).show() }
        }
    }

}