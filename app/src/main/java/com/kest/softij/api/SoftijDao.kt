package com.kest.softij.api

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kest.softij.api.model.CartItem
import com.kest.softij.api.model.User

@Dao
interface SoftijDao {

    @Query("SELECT * FROM CartItem")
    fun getCartItems():LiveData<MutableList<CartItem>>

    @Update
    fun updateUser(user: User)

    @Insert
    fun insertIntoCart(item:CartItem)

    @Delete
    fun deleteCartItem(item: CartItem)

    @Update
    fun updateCartItem(items:MutableList<CartItem>)

    @Query("SELECT * FROM User")
    fun getUser():LiveData<User>

    @Insert
    fun insertUser(user:User)

}