package com.kest.softij.api

class Routes {
    companion object{
        const val baseURL:String = "http://softij.co/"
        const val productList:String = "api.php?route=list/products"
        const val wishlist:String = "api.php?route=account/wishlist"
        const val inWishlist:String = "api.php?route=account/wishlist/id"
        const val orders:String = "api.php?route=account/orders"
        const val userInfo:String = "api.php?route=account/info"
        const val address:String = "api.php?route=account/address"
        const val search:String = "api.php?route=search"
        const val searchCount:String = "api.php?route=list/count"
        const val userId:String = "api.php?route=account/customerId"

        const val postUrl:String = "api.php"
        const val postWishlist:String = "account/wishlist"
        const val postRemoveWishlist:String = "account/wishlist/remove"
        const val editAccount:String = "account/edit"
        const val insertAddress:String = "account/address"
        const val updateSub:String = "account/update/sub"
        const val updateViews:String = "update/views"
        const val editAddress:String = "account/address/edit"
        const val deleteAddress:String = "account/address/delete"
        const val checkout:String = "checkout"

//        ========================================================
        private const val webBase:String = "http://192.168.1.9:5000/"
        const val login:String = webBase+"login"
        const val register:String = webBase+"register"
        const val forgotPassword:String = webBase+"forgotPassword"
        const val changePassword:String = webBase+"changePassword"
        const val webCheckout:String = webBase+"checkout"
    }
}