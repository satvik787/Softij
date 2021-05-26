package com.kest.softij

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kest.softij.api.Routes
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
        .baseUrl(Routes.baseURL)
        .build()

    private val api = retrofit.create(Api::class.java)
    private val customerId = 31
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
        return executeCall(call,Product.ProductParser())
    }

    fun getWishList():LiveData<Res<MutableList<Product>>>{
        val call: Call<String> = api.getWishlist(customerId)
        return executeCall(call,Product.ProductParser())
    }

    fun inWishlist(productId:Int):LiveData<Res<Unit>>{
        val call = api.inWishlist(customerId,productId)
        return executeCall(call,this)
    }

    fun getOrders():LiveData<Res<MutableList<Order>>>{
        val orderCall: Call<String> = api.getOrders(customerId)
        return executeCall(orderCall,Order.OrderParser())
    }

    fun getUserInfo():LiveData<Res<User>>{
        val infoCall = api.getUserInfo(customerId)
        return executeCall(infoCall,User.UserParser())
    }

    fun getAddress():LiveData<Res<MutableList<Address>>>{
        val addressCall = api.getAddress(customerId)
        return executeCall(addressCall,Address.AddressParser())
    }

    fun postWishList(productId:Int,mutableLiveData: MutableLiveData<Res<Unit>>){
        val postWishlistCall = api.postWishlist(customerId,productId)
        executeCall(postWishlistCall,this,mutableLiveData)
    }

    fun postEditAddress(address: Address,mutableLiveData: MutableLiveData<Res<Unit>>){
        val editAddressCall  = api.postEditAddress(
            address.addressId,
            address.address1,
            address.address2,
            address.firstname,
            address.lastname,
            address.city,
            address.postCode,
        )
        executeCall(editAddressCall,this,mutableLiveData)
    }

    fun postEditAccount(user:User,mutableLiveData: MutableLiveData<Res<Unit>>){
        val editAccCall = api.postEditAccount(
            user.customerId,
            user.firstName,
            user.lastName,
            user.email,
            user.telephone
        )
        executeCall(editAccCall,this,mutableLiveData)
    }

    fun postRemoveWishlist(productId: Int,liveData: MutableLiveData<Res<Unit>>){
        val removeCall = api.postRemoveWishlist(customerId,productId)
        executeCall(removeCall,this,liveData)
    }

    fun postDeleteAddress(addressId:Int,liveData: MutableLiveData<Res<Unit>>){
        val deleteCall = api.postDeleteAddress(addressId)
        executeCall(deleteCall,this,liveData)
    }

    fun postInsertAddress(address: Address,mutableLiveData: MutableLiveData<Res<Unit>>){
        val insertAddressCall  = api.postInsertAddress(
            customerId,
            address.address1,
            address.address2,
            address.firstname,
            address.lastname,
            address.city,
            address.postCode,
        )
        executeCall(insertAddressCall,this,mutableLiveData)
    }

    private fun <T> executeCall(call:Call<String>,parser: Parser<T>,liveData: MutableLiveData<Res<T>>? = null):MutableLiveData<Res<T>>{
        val mutableLiveData = liveData ?: MutableLiveData<Res<T>>()
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



    override fun parseJson(json: String): Res<Unit> {
        val jsonObj = JSONObject(json)
        return Res(
            jsonObj.getString("Msg"),
            jsonObj.getInt("code"),
            jsonObj.getInt("numRows")
        )
    }

}