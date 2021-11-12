package com.example.maletavirtual

interface itemFragmentListenner {
    fun sendItemList(listaElems:MutableList<BagItems>):MutableList<BagItems>
}