package com.example.maletavirtual

data class ItemsTemplate(
    val propietario:String?="",
    val nombre_maleta:String?="",
    val lider:String?="",
    val tipo:String?="",
    val articulos:MutableList<BagItems>?=null
    )
