package com.kest.softij.api.model

import org.json.JSONObject
import java.net.URL

data class User
    (val customerId:Int,
    val storeId:Int,
    val firstName:String,
    val lastName:String,
    val email:String,
    val addressId:Int,
    val telephone:String,
    val ip:URL,
    val fax:String,
    val dateAdded:String)
{
    class UserParser:Parser<User>{
        override fun parseJson(json:String):Res<User>{
            val jsonObj = JSONObject(json)
            val result = jsonObj.getJSONArray("result").getJSONObject(0)
            return Res(
                jsonObj.getString("Msg"),
                jsonObj.getInt("code"),
                jsonObj.getInt("numRows"),
                User(
                    result.getInt("customer_id"),
                    result.getInt("store_id"),
                    result.getString("first_name"),
                    result.getString("last_name"),
                    result.getString("email"),
                    result.getInt("address_id"),
                    result.getString("telephone"),
                    URL(result.getString("ip")),
                    result.getString("fax"),
                    result.getString("date_added")
                )
            )
        }
    }
}