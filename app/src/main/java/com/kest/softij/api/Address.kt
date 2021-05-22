package com.kest.softij.api

class Address {
    companion object{
        const val baseURL:String = "http://192.168.1.9/"
        const val productList:String = "api.php?route=list/products"
        const val wishlist:String = "api.php?route=account/wishlist"
        const val inWishlist:String = "api.php?route=account/wishlist/id"

        const val postBaseUrl:String = "api.php"
        const val postWishlist:String = "account/wishlist"
    }
}