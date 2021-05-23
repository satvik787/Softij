package com.kest.softij.api

import retrofit2.Call
import retrofit2.http.*

interface Api {
    @GET(Address.productList)
    fun getProducts(
        @Query("limit") limit:Int,
        @Query("start") start:Int):Call<String>

    @GET(Address.wishlist)
    fun getWishlist(@Query("customerId") id:Int):Call<String>

    @GET(Address.inWishlist)
    fun inWishlist(@Query("customerId") customerId: Int,
                   @Query("productId") productId: Int):Call<String>

    @GET(Address.orders)
    fun getOrders(@Query("customerId") id:Int):Call<String>

    @GET(Address.userInfo)
    fun getUserInfo(@Query("customerId") id: Int):Call<String>

    @POST(Address.postUrl)
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @FormUrlEncoded
    fun postWishlist(
        @Field("customerId") customerId:Int,
        @Field("productId") productId:Int,
        @Field("route") route:String = Address.postWishlist):Call<String>

}