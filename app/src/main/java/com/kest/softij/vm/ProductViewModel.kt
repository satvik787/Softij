package com.kest.softij.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kest.softij.SoftijRepository
import com.kest.softij.api.model.Product
import com.kest.softij.api.model.Res

class ProductViewModel : ViewModel() {
    lateinit var product: Product
    private val repo = SoftijRepository.getRepo()
    var inWishlist:Boolean? = null

    val putWishlist:MutableLiveData<Res<Unit>> = MutableLiveData()
    val removeWishlist:MutableLiveData<Res<Unit>> = MutableLiveData()

    lateinit var checkWishLiveData:LiveData<Res<Unit>>

    fun postWishlist(){
        repo.postWishList(31,product.productId,putWishlist)
    }

    fun postRemoveWishlist(){
        repo.postRemoveWishlist(31,product.productId,removeWishlist)
    }

    fun checkWishlist(customerId:Int){
        checkWishLiveData = repo.inWishlist(customerId,product.productId)
    }



}