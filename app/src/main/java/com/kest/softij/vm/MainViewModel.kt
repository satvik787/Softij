package com.kest.softij.vm

import androidx.lifecycle.ViewModel
import java.util.*

class MainViewModel:ViewModel() {
    val titleStack = Stack<Int>()
    var title:String? = null
}