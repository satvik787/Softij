package com.kest.softij.vm

import androidx.lifecycle.ViewModel
import com.kest.softij.SoftijRepository
import com.kest.softij.api.model.CartItem

class CartViewModel : ViewModel() {
    private val repo = SoftijRepository.getRepo()
    val cartItems = repo.databaseGetCartItems()
    val removedItems = mutableListOf<CartItem>()
    var totalPrice = 0.0
    var cartCount = 0

    fun removeFromCart(item: CartItem) {
        repo.databaseDeleteCartItem(item)
    }

    fun updateCartItems(items:MutableList<CartItem>) {
        repo.databaseUpdateCartItems(items)
    }

}