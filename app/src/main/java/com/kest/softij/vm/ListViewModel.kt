package com.kest.softij.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kest.softij.ListFragment
import com.kest.softij.SoftijRepository
import com.kest.softij.api.model.CartItem
import com.kest.softij.api.model.Order
import com.kest.softij.api.model.Product
import com.kest.softij.api.model.Res

class ListViewModel:ViewModel() {
    lateinit var products:LiveData<Res<MutableList<Product>>>
    lateinit var orders:LiveData<Res<MutableList<Order>>>
    val postLiveData = MutableLiveData<Res<Unit>>()
    private val repo = SoftijRepository.getRepo()

    fun init(type:Int){
        when (type) {
            ListFragment.LIST_PRODUCTS -> products = repo.getProducts(3647,50)
            ListFragment.LIST_WISHLIST -> products = repo.getWishList()
            ListFragment.LIST_ORDERS -> orders = repo.getOrders()
        }
    }

    fun postRemoveWishlist(productId:Int){
        repo.postRemoveWishlist(productId,postLiveData)
    }


    fun addToCart(product:Product,cartStatus :MutableLiveData<String>){
        SoftijRepository.getRepo().databasePutCartItem(
            CartItem(
                product.productId,
                product.name,
                product.description,
                product.model,
                product.imageLink,
                product.price,
                product.stock
            ),
            cartStatus
        )
    }

}