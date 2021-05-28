package com.kest.softij.api.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class CartItem(
    val productId:Int,
    val name:String,
    val description:String,
    val model:String,
    val image:String,
    val price:Double,
    var stock:Int,
    var quantity:Int = 1,
    @PrimaryKey(autoGenerate = true) val cartId:Int = 0
)
