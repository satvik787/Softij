package com.kest.softij.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kest.softij.SoftijRepository
import com.kest.softij.api.model.CartItem
import com.kest.softij.api.model.Product
import com.kest.softij.api.model.Res
import java.util.*

class MainViewModel:ViewModel() {
    private var repo = SoftijRepository.getRepo()

    val titleStack = Stack<Int>()
    var title:String? = null
    var recentQuery:String = ""
    var addressId:Int = 0;
    val searchData:MutableLiveData<Res<MutableList<Product>>> = MutableLiveData()
    val postCartItem = MutableLiveData<Res<MutableList<Int>>>()
    val passwordLiveData:MutableLiveData<Res<Unit>> = MutableLiveData()
    lateinit var cartItems: MutableList<CartItem>
    val count:MutableLiveData<Int> = MutableLiveData()
    val cartStatus:MutableLiveData<String> = MutableLiveData()



    fun search(query:String,limit:Int,start:Int = 1){
        repo.getListCount(query,count)
        if(query != recentQuery){
            recentQuery = query
            searchData.value = null
        }
        repo.search(query,limit,start,searchData)
    }

    fun checkout(addressId: Int,cartItems:MutableList<CartItem>){
        this.addressId = addressId
        this.cartItems = cartItems
        repo.postCheckout(cartItems,postCartItem)
    }

    fun changePassword(oldPassword:String,password:String){
        SoftijRepository
            .getRepo()
            .webChangePassword(
                SoftijRepository.getRepo().getUser().email,
                oldPassword,
                password,
                passwordLiveData
            )
        val user = repo.getUser()
        user.password = password
        SoftijRepository.getRepo().databaseUpdateUser(user)
    }
}