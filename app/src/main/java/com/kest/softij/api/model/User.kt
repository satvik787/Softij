package com.kest.softij.api.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject
import java.net.URL

@Entity
data class User
    (@PrimaryKey val customerId:Int,
     val storeId:Int,
     var firstName:String,
     var lastName:String,
     var email:String,
     val addressId:Int,
     var telephone:String,
     val ip:String,
     var newsletter:Int,
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
                    result.getString("firstname"),
                    result.getString("lastname"),
                    result.getString("email"),
                    result.getInt("address_id"),
                    result.getString("telephone"),
                    result.getString("ip"),
                    result.getInt("newsletter"),
                    result.getString("date_added")
                )
            )
        }
    }
}