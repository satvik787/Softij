package com.kest.softij

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kest.softij.api.model.Order
import com.kest.softij.api.model.Product
import com.kest.softij.api.model.Res

class ListViewModel:ViewModel() {
    lateinit var products:LiveData<Res<MutableList<Product>>>
    lateinit var orders:LiveData<Res<MutableList<Order>>>
    private val repo = SoftijRepository.getRepo()

    fun init(type:Int){
        when (type) {
            ListFragment.LIST_PRODUCTS -> products = repo.getProducts(3647,50)
            ListFragment.LIST_WISHLIST -> products = repo.getWishList(31)
            ListFragment.LIST_ORDERS -> orders = repo.getOrders(31)
        }
    }

}