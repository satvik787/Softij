package com.kest.softij.api.model

import org.json.JSONObject

data class Address(
    val addressId:Int,
    val firstname:String,
    val lastname:String,
    val company:String,
    val address1:String,
    val address2:String,
    val city:String,
    val state:String,
    val postCode:String,
    val zoneId:Int,
    val countryId:Int = 99,
) {
    class AddressParser:Parser<MutableList<Address>>{
        override fun parseJson(json: String): Res<MutableList<Address>> {
            val jsonObj = JSONObject(json)
            val result = jsonObj.getJSONArray("result")
            val list = mutableListOf<Address>()
            val res = Res(
                jsonObj.getString("Msg"),
                jsonObj.getInt("code"),
                jsonObj.getInt("numRows"),
                list
            )
            for (i in 0 until result.length()){
                val address = result.getJSONObject(i)
                list.add(
                    Address(
                        address.getInt("address_id"),
                        address.getString("firstname"),
                        address.getString("lastname"),
                        address.getString("company"),
                        address.getString("address_1"),
                        address.getString("address_2"),
                        address.getString("city"),
                        address.getString("name"),
                        address.getString("postcode"),
                        address.getInt("zone_id"),
                    )
                )
            }
            return res
        }
    }
}