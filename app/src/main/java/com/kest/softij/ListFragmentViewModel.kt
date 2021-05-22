package com.kest.softij

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kest.softij.api.model.Product
import com.kest.softij.api.model.Res

class ListFragmentViewModel:ViewModel() {
    lateinit var products:LiveData<Res<MutableList<Product>>>
    private val repo = SoftijRepository.getRepo()

    fun init(type:Int){
        if(type == ListFragment.LIST_PRODUCTS){
            products = repo.getProducts(3647,50)
        }else if(type == ListFragment.LIST_WISHLIST){
            products = repo.getWishList(2)
        }
    }

}