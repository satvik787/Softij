package com.kest.softij.api.model

import org.json.JSONObject

class Order(
    val productId:Int,
    val name:String,
    val description:String,
    val model:String,
    val quantity:Int,
    val price:Double,
    val totalPrice:Double,
    val orderStatusId:Int,
    val imageLink:String,
    val dateAdded:String
){
    class OrderParser:Parser<MutableList<Order>>{
        override fun parseJson(json:String):Res<MutableList<Order>>{
            val list = mutableListOf<Order>()
            val jsonObj = JSONObject(json)
            val res = Res (
                jsonObj.getString("Msg"),
                jsonObj.getInt("code"),
                jsonObj.getInt("numRows"),
                list
            )
            val jsonList = jsonObj.getJSONArray("result")
            for(i in 0 until jsonList.length()){
                val obj = jsonList.getJSONObject(i)
                list.add(
                    Order(
                        obj.getInt("product_id"),
                        obj.getString("name"),
                        obj.getString("description"),
                        obj.getString("model"),
                        obj.getInt("quantity"),
                        obj.getDouble("price"),
                        obj.getDouble("total"),
                        obj.getInt("order_status_id"),
                        obj.getString("image"),
                        obj.getString("date_added")
                    )
                )
            }
            return res
        }
    }
}
