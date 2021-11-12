package com.example.maletavirtual

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BagAdapter(private val pContext: Context, private val maletas: MutableList<Bags>) : ArrayAdapter<Bags>(pContext,R.layout.bags,maletas) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var vista=convertView
        if (vista==null){
            vista=LayoutInflater.from(parent?.context).inflate(R.layout.bags,parent,false)
        }

        val m=vista?.findViewById<TextView>(R.id.backpack_name)
            m!!.text=maletas[position].NombreMaleta

        return  vista!!
    }
}