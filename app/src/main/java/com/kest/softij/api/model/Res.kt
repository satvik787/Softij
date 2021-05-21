package com.kest.softij.api.model

data class Res<T> (
    val msg: String,
    val code: Int,
    val numRows:Int,
    val data: T? = null
)
