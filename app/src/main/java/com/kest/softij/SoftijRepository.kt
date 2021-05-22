package com.kest.softij

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kest.softij.api.Address
import com.kest.softij.api.Api
import com.kest.softij.api.model.Product
import com.kest.softij.api.model.Res
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.lang.IllegalStateException

class SoftijRepository private constructor()
{
    private val retrofit = Retrofit
        .Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(Address.baseURL)
        .build()

    private val api = retrofit.create(Api::class.java)

    companion object{
        private var obj:SoftijRepository? = null
        fun init(){
            if(obj == null) obj = SoftijRepository()
        }
        fun getRepo():SoftijRepository{
            obj?.let {
                return it
            }
            throw IllegalStateException("init method not called")
        }
    }

    fun getProducts(start:Int,limit:Int):LiveData<Res<MutableList<Product>>>{
        val productCall: Call<String> = api.getProducts(limit,start)
        val mutableLiveData = MutableLiveData<Res<MutableList<Product>>>()

        productCall.enqueue(object:Callback<String>{
            override fun onResponse(p0: Call<String>, p1: Response<String>) {
                p1.body()?.let{ json ->
                    mutableLiveData.value = Product.getProducts(json)
                }?:run{
                    mutableLiveData.value = Res(
                        "Response Body Empty",
                        -1,
                        0
                    )
                }
            }

            override fun onFailure(p0: Call<String>, p1: Throwable) {
                mutableLiveData.value = Res(
                    "Http Request Failed ${p1.message}",
                    -1,
                    0)
            }
        })
        return mutableLiveData
    }

    fun getWishList(id:Int):LiveData<Res<MutableList<Product>>>{
        val wishListCall: Call<String> = api.getWishlist(id)
        val mutableLiveData = MutableLiveData<Res<MutableList<Product>>>()
        wishListCall.enqueue(object:Callback<String>{
            override fun onResponse(p0: Call<String>, p1: Response<String>) {
                p1.body()?.let { json ->
                    mutableLiveData.value = Product.getProducts(json)
                }?: run {
                    mutableLiveData.value = Res(
                        "Response Body Empty",
                        -1,
                        0
                    )
                }
            }

            override fun onFailure(p0: Call<String>, p1: Throwable) {
                mutableLiveData.value = Res(
                    "Http Request Failed ${p1.message}",
                    -1,
                    0
                )
            }

        })
        return mutableLiveData
    }
}