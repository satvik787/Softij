package com.kest.softij

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kest.softij.api.Address
import com.kest.softij.api.Api
import com.kest.softij.api.model.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.lang.IllegalStateException

class SoftijRepository private constructor():Parser<Unit>{
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
        val call: Call<String> = api.getProducts(limit,start)
        return get(call,Product.ProductParser())
    }

    fun getWishList(id:Int):LiveData<Res<MutableList<Product>>>{
        val call: Call<String> = api.getWishlist(id)
        return get(call,Product.ProductParser())
    }

    fun inWishlist(customerId:Int,productId:Int):LiveData<Res<Unit>>{
        val call = api.inWishlist(customerId,productId)
        return get(call,this)
    }

    fun getOrders(customerId: Int):LiveData<Res<MutableList<Order>>>{
        val orderCall: Call<String> = api.getOrders(customerId)
        return get(orderCall,Order.OrderParser())
    }

    fun getUserInfo(customerId: Int):LiveData<Res<User>>{
        val infoCall = api.getUserInfo(customerId)
        return get(infoCall,User.UserParser())
    }

    private fun <T> get(call:Call<String>,parser: Parser<T>):MutableLiveData<Res<T>>{
        val mutableLiveData = MutableLiveData<Res<T>>()

        call.enqueue(object:Callback<String>{
            override fun onResponse(p0: Call<String>, p1: Response<String>) {
                p1.body()?.let{ json ->
                    mutableLiveData.value = parser.parseJson(json)
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

    fun postWishList(customerId:Int,productId:Int):LiveData<Res<Unit>>{
        val postWishlistCall = api.postWishlist(customerId,productId)
        val livedata = MutableLiveData<Res<Unit>>()
        postWishlistCall.enqueue(object :Callback<String>{
            override fun onResponse(p0: Call<String>, p1: Response<String>) {
                p1.body()?.let { json ->
                    val obj = JSONObject(json)
                    livedata.value = Res(
                        obj.getString("Msg"),
                        obj.getInt("code"),
                        obj.getInt("numRows")
                    )
                }?: run {
                    livedata.value = Res(
                        "Response Body Empty",
                        -1,
                        0
                    )
                }
            }

            override fun onFailure(p0: Call<String>, p1: Throwable) {
                livedata.value = Res(
                    "Http Request Failed ${p1.message}",
                    -1,
                    0
                )
            }
        })
        return livedata
    }

    override fun parseJson(json: String): Res<Unit> {
        val jsonObj = JSONObject(json)
        return Res(
            jsonObj.getString("Msg"),
            jsonObj.getInt("code"),
            jsonObj.getInt("numRows")
        )
    }

}