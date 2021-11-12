package com.example.maletavirtual

interface fragmentListener {
    fun onloadFuntion()
    fun getEmail():String
    fun sendItemList():MutableList<BagItems>
}