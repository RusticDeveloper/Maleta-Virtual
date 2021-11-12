package com.example.maletavirtual

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView

class BagItemAdapter(private val itemMaleta:List<Bags>):RecyclerView.Adapter<BagItemAdapter.ViewHolder>(){
    inner class ViewHolder (viewItem:View):RecyclerView.ViewHolder(viewItem){
        private  val itm_name=viewItem.findViewById<CheckBox>(R.id.itemName)
        private  val itm_quantity=viewItem.findViewById<CheckBox>(R.id.itemQuantity)
        fun bind(bagElem: Bags) {
            itm_name.setText(bagElem.NombreMaleta)
            itm_quantity.setText(bagElem.NombreMaleta)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.bag_item,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemMaleta[position])
    }

    override fun getItemCount(): Int =itemMaleta.size

}
//class BagItemAdapter(private val itemMaleta:List<Bags>):RecyclerView.Adapter<BagItemAdapter.ViewHolder>(){}
