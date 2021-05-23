package com.kest.softij.api.model

interface Parser<T> {
    fun parseJson(json:String):Res<T>
}