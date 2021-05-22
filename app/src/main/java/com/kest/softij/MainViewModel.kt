package com.kest.softij

import androidx.lifecycle.ViewModel
import java.util.*

class MainViewModel:ViewModel() {
    val titleStack = Stack<Int>()
    var title:String? = null
}