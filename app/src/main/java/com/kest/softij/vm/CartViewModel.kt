package com.kest.softij.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kest.softij.SoftijRepository
import com.kest.softij.api.model.Address
import com.kest.softij.api.model.CartItem
import com.kest.softij.api.model.Res

class CartViewModel : ViewModel() {
    private val repo = SoftijRepository.getRepo()
    lateinit var addressList:LiveData<Res<MutableList<Address>>>
    val cartItems = repo.databaseGetCartItems()
    val addressId = MutableLiveData<Int>()
    val removedItems = mutableListOf<CartItem>()
    var totalPrice = 0.0
    var cartCount = 0

    fun removeFromCart(item: CartItem) {
        repo.databaseDeleteCartItem(item)
    }

    fun getAddress(){

        addressList = repo.getAddress()
    }

    fun updateCartItems() {
        cartItems.value?.let {
            repo.databaseUpdateCartItems(it)
        }
    }



}