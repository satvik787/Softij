package com.kest.softij

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kest.softij.api.model.Product
import com.kest.softij.api.model.Res

class ProductViewModel : ViewModel() {
    lateinit var product: Product
    lateinit var postLiveData:LiveData<Res<Unit>>
    lateinit var checkWishLiveData:LiveData<Res<Unit>>
    var inWishlist:Boolean? = null

    fun postWishlist(customerId:Int){
        postLiveData = SoftijRepository
            .getRepo()
            .postWishList(customerId,product.productId)
    }

    fun checkWishlist(customerId:Int){
        checkWishLiveData = SoftijRepository
            .getRepo()
            .inWishlist(customerId,product.productId)
    }

}