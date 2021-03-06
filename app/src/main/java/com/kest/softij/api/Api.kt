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

    @GET(Routes.userId)
    fun getUserId(@Query("email") email:String):Call<String>


    @GET(Routes.address)
    fun getAddress(@Query("customerId") id: Int):Call<String>

    @GET(Routes.searchCount)
    fun getListCount(@Query("query") query:String):Call<String>

    @GET(Routes.search)
    fun search(@Query("query") query: String,
               @Query("limit") limit:Int,
               @Query("start") start: Int):Call<String>


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
    fun checkout(
        @Field("customerId") customerId:Int,
        @Field("productId") productId:Int,
        @Field("quantity") quantity:Int,
        @Field("route") route:String = Routes.checkout):Call<String>

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
    fun postUpdateView(
        @Field("productId") productId:Int,
        @Field("route") route:String = Routes.updateViews):Call<String>

    @POST(Routes.postUrl)
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @FormUrlEncoded
    fun postUpdateSub(
        @Field("customerId") customerId:Int,
        @Field("route") route:String = Routes.updateSub):Call<String>

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




    @POST(Routes.register)
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @FormUrlEncoded
    fun webRegister(
        @Field("firstname") firstname:String,
        @Field("lastname") lastname:String,
        @Field("email") email:String,
        @Field("phone") phone:String,
        @Field("password") password:String):Call<String>

    @POST(Routes.login)
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @FormUrlEncoded
    fun webLogin(
        @Field("email") email: String,
        @Field("password") password: String):Call<String>

    @POST(Routes.forgotPassword)
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @FormUrlEncoded
    fun forgotPassword(
        @Field("email") email: String):Call<String>

    @POST(Routes.changePassword)
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @FormUrlEncoded
    fun webChangePassword(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("newPassword") newPassword: String):Call<String>

    @POST(Routes.webCheckout)
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @FormUrlEncoded
    fun webCheckout(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("addressId") addressId: Int):Call<String>


}