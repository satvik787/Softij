package com.kest.softij

import androidx.lifecycle.ViewModel
import java.util.*

class MainActivityViewModel:ViewModel() {
    val titleStack = Stack<Int>()
    var title:String? = null
}