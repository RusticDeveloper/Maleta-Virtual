package com.example.maletavirtual

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class BagItemAdapter(private val pContext: Context, private val elementos: MutableList<BagItems>):
    ArrayAdapter<BagItems>(pContext,R.layout.bag_item,elementos){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var vista=convertView
        if (vista==null){
            vista=LayoutInflater.from(parent?.context).inflate(R.layout.bag_item,parent,false)
        }

        val itmName=vista?.findViewById<TextView>(R.id.itemName)
        itmName!!.text=elementos[position].nombreArticulo
        val itmQuantity=vista?.findViewById<TextView>(R.id.itemQuantity)
        itmQuantity!!.text=elementos[position].cantidadItem

        if (elementos[position].empacado){
            itmName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG)
            itmName!!.setTextColor(Color.parseColor("#eaf6f5"))
            itmName!!.setBackgroundColor(Color.parseColor("#b0c9c8"))
            itmQuantity.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG)
            itmQuantity!!.setTextColor(Color.parseColor("#eaf6f5"))
            itmQuantity!!.setBackgroundColor(Color.parseColor("#b0c9c8"))
        }else{
            itmName.setPaintFlags(Paint.CURSOR_BEFORE)
            itmName!!.setTextColor(Color.parseColor("#270A0A"))
            itmName!!.setBackgroundColor(Color.TRANSPARENT)
            itmQuantity.setPaintFlags(Paint.CURSOR_BEFORE)
            itmQuantity!!.setTextColor(Color.parseColor("#270A0A"))
            itmQuantity!!.setBackgroundColor(Color.TRANSPARENT)
        }

//        val itmPacked=vista?.findViewById<CheckBox>(R.id.checkBox_empacado)
//        itmPacked!!.isChecked=elementos[position].empacado

        return  vista!!

    }
}
