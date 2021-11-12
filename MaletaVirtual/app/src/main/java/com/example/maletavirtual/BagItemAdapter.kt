package com.example.maletavirtual

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView

class BagItemAdapter(private val itemMaleta:List<Bags>):RecyclerView.Adapter<BagItemAdapter.ViewHolder>(){
    inner class ViewHolder (viewItem:View):RecyclerView.ViewHolder(viewItem){
        private  val itm_bag=viewItem.findViewById<CheckBox>(R.id.item_check)
        fun bind(bagElem: Bags) {
            itm_bag.setText(bagElem.NombreMaleta)
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
