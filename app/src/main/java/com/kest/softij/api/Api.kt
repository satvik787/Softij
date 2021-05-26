package com.kest.softij.api

import retrofit2.Call
import retrofit2.http.*

interface Api {
    @GET(Routes.productList)
    fun getProducts(
        @Query("limit") limit:Int,
        @Query("start") start:Int):Call<String>

    @GET(Routes.wishlist)
    fun getWishlist(@Query("customerId") id:Int):Call<String>

    @GET(Routes.inWishlist)
    fun inWishlist(@Query("customerId") customerId: Int,
                   @Query("productId") productId: Int):Call<String>

    @GET(Routes.orders)
    fun getOrders(@Query("customerId") id:Int):Call<String>

    @GET(Routes.userInfo)
    fun getUserInfo(@Query("customerId") id: Int):Call<String>

    @GET(Routes.address)
    fun getAddress(@Query("customerId") id: Int):Call<String>



    @POST(Routes.postUrl)
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @FormUrlEncoded
    fun postWishlist(
        @Field("customerId") customerId:Int,
        @Field("productId") productId:Int,
        @Field("route") route:String = Routes.postWishlist):Call<String>

    @POST(Routes.postUrl)
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @FormUrlEncoded
    fun postRemoveWishlist(
        @Field("customerId") customerId:Int,
        @Field("productId") productId:Int,
        @Field("route") route:String = Routes.postRemoveWishlist):Call<String>

    @POST(Routes.postUrl)
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @FormUrlEncoded
    fun postEditAccount(
        @Field("customerId") customerId:Int,
        @Field("firstname") firstname:String,
        @Field("lastname") lastname:String,
        @Field("email") email:String,
        @Field("phone") phone:String,
        @Field("route") route:String = Routes.editAccount):Call<String>

    @POST(Routes.postUrl)
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @FormUrlEncoded
    fun postEditAddress(
        @Field("addressId") addressId:Int,
        @Field("address1") address1:String,
        @Field("address2") address2:String,
        @Field("firstname") firstname:String,
        @Field("lastname") lastname:String,
        @Field("city") city:String,
        @Field("postcode") postCode:String,
        @Field("zoneId") zoneId:Int = 4231,
        @Field("route") route:String = Routes.editAddress):Call<String>

    @POST(Routes.postUrl)
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @FormUrlEncoded
    fun postDeleteAddress(
        @Field("addressId") addressId:Int,
        @Field("route") route:String = Routes.deleteAddress):Call<String>

    @POST(Routes.postUrl)
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @FormUrlEncoded
    fun postInsertAddress(
        @Field("customerId") customerI:Int,
        @Field("address1") address1:String,
        @Field("address2") address2:String,
        @Field("firstname") firstname:String,
        @Field("lastname") lastname:String,
        @Field("city") city:String,
        @Field("postcode") postCode:String,
        @Field("zoneId") zoneId:Int = 4231,
        @Field("route") route:String = Routes.insertAddress):Call<String>

}