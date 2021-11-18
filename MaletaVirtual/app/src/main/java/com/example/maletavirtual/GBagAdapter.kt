package com.example.maletavirtual

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GBagAdapter(private val pContext: Context, private val maletas: MutableList<String>) : ArrayAdapter<String>(pContext,R.layout.bags,maletas) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var vista=convertView
        if (vista==null){
            vista=LayoutInflater.from(parent?.context).inflate(R.layout.g_bag_item,parent,false)
        }

        val iv=vista?.findViewById<ImageView>(R.id.iv_imagen_bag)
        if (position==0){
                iv!!.setBackgroundResource(R.drawable.ic_baseline_edit_24)
        }else{
                iv!!.setBackgroundResource(R.drawable.ic_baseline_visibility_24)
        }

        val m=vista?.findViewById<TextView>(R.id.txt_nombre_bag)
            m!!.text=maletas[position]

        return  vista!!
    }
}