package com.kest.softij.api.model

import androidx.lifecycle.MutableLiveData
import org.json.JSONObject
import java.io.Serializable

open class Product(
    val productId:Int,
    val name:String,
    val description:String,
    val model:String,
    var stock:Int,
    val price:Double,
    val dateAdded:String,
    val imageLink:String,
    var viewed:Int
    ):Serializable
{

    class ProductParser:Parser<MutableList<Product>>{
        override fun parseJson(json:String):Res<MutableList<Product>>{
            val data = mutableListOf<Product>()
            val resJson = JSONObject(json)
            val res = Res(
                resJson.getString("Msg"),
                resJson.getInt("code"),
                resJson.getInt("numRows"),
                data
            )
            val result = resJson.getJSONArray("result")
            for (i in 0 until result.length()){
                val product = result.getJSONObject(i)
                data.add(
                    Product(
                        product.getInt("product_id"),
                        product.getString("name"),
                        product.getString("description"),
                        product.getString("model"),
                        product.getInt("quantity"),
                        product.getDouble("price"),
                        product.getString("date_added"),
                        product.getString("image"),
                        product.getInt("viewed")
                    )
                )
            }
            return res;
        }
    }
}