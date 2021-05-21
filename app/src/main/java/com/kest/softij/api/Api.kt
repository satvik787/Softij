package com.kest.softij.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET(Address.productList)
    fun getProducts(
        @Query("limit") limit:Int,
        @Query("start") start:Int):Call<String>
}