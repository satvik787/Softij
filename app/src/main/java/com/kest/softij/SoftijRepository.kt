package com.kest.softij

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.kest.softij.api.Api
import com.kest.softij.api.Routes
import com.kest.softij.api.SoftijDatabase
import com.kest.softij.api.model.*
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern


class SoftijRepository private constructor(context: Context){
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()
    private val retrofit = Retrofit
        .Builder()
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(Routes.baseURL)
        .build()

    private val api = retrofit.create(Api::class.java)
    private val database:SoftijDatabase = Room.databaseBuilder(
        context,
        SoftijDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val apiParser = object :Parser<Unit>{
        override fun parseJson(json: String): Res<Unit> {
            val jsonObj = JSONObject(json)
            return Res(
                jsonObj.getString("Msg"),
                jsonObj.getInt("code"),
                jsonObj.getInt("numRows")
            )
        }
    }

    private val webParser = object :Parser<Unit>{
        override fun parseJson(json: String): Res<Unit> {
            val jsonObj = JSONObject(json)
            return Res(
                jsonObj.getString("Msg"),
                if(jsonObj.getBoolean("status")) 1 else 0,
                0
            )
        }

    }

    private val threadPool = Executors.newFixedThreadPool(2)
    private lateinit var user:User


    companion object{
        private var obj:SoftijRepository? = null
        private const val DATABASE_NAME = "softij_01"
        fun init(context: Context){
            if(obj == null) obj = SoftijRepository(context)
        }
        fun getRepo():SoftijRepository{
            obj?.let {
                return it
            }
            throw IllegalStateException("init method not called")
        }
        private val VALID_EMAIL_ADDRESS_REGEX: Pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)

        fun validateEmail(emailStr: String): Boolean {
            val matcher: Matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr)
            return matcher.find()
        }
    }

    fun setUser(user: User){
        this.user = user
    }

    fun getUser() = user


    fun databaseGetCartItems():LiveData<MutableList<CartItem>>{
        return database.softijDao().getCartItems()
    }

    fun databasePutCartItem(item:CartItem,liveData: MutableLiveData<String>){
        threadPool.execute {
            database.softijDao().insertIntoCart(item)
            liveData.postValue("Added to cart")
        }
    }

    fun databaseDeleteCartItem(item: CartItem){
        threadPool.execute {
            database.softijDao().deleteCartItem(item)
        }
    }

    fun databaseUpdateCartItems(items:MutableList<CartItem>){
        threadPool.execute {
            database.softijDao().updateCartItem(items)
        }
    }

    fun databasePutUser(user: User){
        threadPool.execute {
            database.softijDao().insertUser(user)
        }
    }

    fun databaseGetUser():LiveData<User>{
        return database.softijDao().getUser()
    }

    fun databaseUpdateUser(user: User){
        threadPool.execute {
            database.softijDao().updateUser(user)
        }
    }

    fun getProducts(start:Int,limit:Int):LiveData<Res<MutableList<Product>>>{
        val call: Call<String> = api.getProducts(limit,start)
        return executeCall(call,Product.ProductParser())
    }

    fun getWishList():LiveData<Res<MutableList<Product>>>{
        val call: Call<String> = api.getWishlist(user.customerId)
        return executeCall(call,Product.ProductParser())
    }

    fun inWishlist(productId:Int):LiveData<Res<Unit>>{
        val call = api.inWishlist(user.customerId,productId)
        return executeCall(call,apiParser)
    }

    fun postCheckout(items:MutableList<CartItem>,livedata: MutableLiveData<Res<MutableList<Int>>>){
        threadPool.execute {
            val list = mutableListOf<Int>()
            for (i in 0 until items.size){
                val call = api.checkout(user.customerId,items[i].productId,items[i].quantity)
                val body = call.execute().body()
                body?.let{
                    val json = apiParser.parseJson(it)
                    if(json.code > 0) list.add(i)
                }
            }
            if(list.size == items.size) {
               livedata.postValue(Res("Order Placed successfully",1,0,list))
            }else{
                livedata.postValue(Res("unsuccessful",-1,list.size,list))
            }

        }
    }

    fun getOrders():LiveData<Res<MutableList<Order>>>{
        val orderCall: Call<String> = api.getOrders(user.customerId)
        return executeCall(orderCall,Order.OrderParser())
    }

    fun getUserInfo():LiveData<Res<User>>{
        val infoCall = api.getUserInfo(user.customerId)
        return executeCall(infoCall,User.UserParser())
    }

    fun getUserId(email: String):LiveData<Res<User>>{
        val call = api.getUserId(email)
        return executeCall(call,User.UserParser())
    }

    fun getAddress():LiveData<Res<MutableList<Address>>>{
        val addressCall = api.getAddress(user.customerId)
        return executeCall(addressCall,Address.AddressParser())
    }
    fun getListCount(query: String,livedata: MutableLiveData<Int>) {
        val call = api.getListCount(query)
        threadPool.execute {
            val json = call.execute().body()
            json?.let {
                livedata.postValue(
                    JSONObject(json)
                        .getJSONArray("result")
                        .getJSONObject(0)
                        .getInt("listSize")
                )
            }
        }
    }

    fun search(query:String,limit: Int,start: Int,livedata:MutableLiveData<Res<MutableList<Product>>>){
        val searchCall = api.search(query,limit,start)
        threadPool.execute {
            val json = searchCall.execute().body()
            json?.let{
                var res = Product.ProductParser().parseJson(json)
                if(res.code > 0){
                    livedata.value?.data?.let { list->
                        for(i in res.data ?: mutableListOf()){
                            list.add(i)
                        }
                        res = Res(
                            res.msg,
                            res.code,
                            res.numRows,
                            list
                        )
                    }
                    livedata.postValue(res)
                }else livedata.postValue(res)
            }?:run{
                livedata.postValue(Res(
                "Empty Response Body",
                -1,
                0))
            }
        }
    }

    fun postWishList(productId:Int,mutableLiveData: MutableLiveData<Res<Unit>>){
        val postWishlistCall = api.postWishlist(user.customerId,productId)
        executeCall(postWishlistCall,apiParser,mutableLiveData)
    }

    fun postUpdateSub(mutableLiveData: MutableLiveData<Res<Unit>>){
        val postUpdateCall = api.postUpdateSub(user.customerId)
        executeCall(postUpdateCall,apiParser,mutableLiveData)
    }

    fun postUpdateViews(productId: Int,mutableLiveData: MutableLiveData<Res<Unit>>){
        val call = api.postUpdateView(productId)
        executeCall(call,apiParser,mutableLiveData)
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
        executeCall(editAddressCall,apiParser,mutableLiveData)
    }

    fun postEditAccount(user:User,mutableLiveData: MutableLiveData<Res<Unit>>){
        val editAccCall = api.postEditAccount(
            user.customerId,
            user.firstName,
            user.lastName,
            user.email,
            user.telephone
        )
        executeCall(editAccCall,apiParser,mutableLiveData)
    }

    fun postRemoveWishlist(productId: Int,liveData: MutableLiveData<Res<Unit>>){
        val removeCall = api.postRemoveWishlist(user.customerId,productId)
        executeCall(removeCall,apiParser,liveData)
    }

    fun postDeleteAddress(addressId:Int,liveData: MutableLiveData<Res<Unit>>){
        val deleteCall = api.postDeleteAddress(addressId)
        executeCall(deleteCall,apiParser,liveData)
    }

    fun postInsertAddress(address: Address,mutableLiveData: MutableLiveData<Res<Unit>>){
        val insertAddressCall  = api.postInsertAddress(
            user.customerId,
            address.address1,
            address.address2,
            address.firstname,
            address.lastname,
            address.city,
            address.postCode,
        )
        executeCall(insertAddressCall,apiParser,mutableLiveData)
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


    fun login(email: String,password:String,liveData: MutableLiveData<Res<Unit>>){
        val call = api.webLogin(email,password)
        executeCall(call,webParser,liveData)
    }

    fun register(firstName:String,
                 lastName:String,
                 password: String,
                 email: String,
                 telephone:String,liveData: MutableLiveData<Res<Unit>>){
        val call = api.webRegister(
            firstName,
            lastName,
            email,
            telephone,
            password
        )
        executeCall(call,webParser,liveData)
    }

    fun forgotPassword(email: String,liveData: MutableLiveData<Res<Unit>>){
        val call = api.forgotPassword(email)
        executeCall(call,webParser,liveData)
    }

    fun webChangePassword(email: String,password: String,newPassword:String,liveData: MutableLiveData<Res<Unit>>){
        val call = api.webChangePassword(email,password,newPassword)
        executeCall(call,webParser,liveData)
    }

    fun webCheckout(email: String,password: String,addressId: Int):LiveData<Res<Unit>>{
        val call = api.webCheckout(email,password,addressId)
        return executeCall(call,webParser)
    }

}