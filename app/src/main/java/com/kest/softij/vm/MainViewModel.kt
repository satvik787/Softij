package com.kest.softij.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kest.softij.SoftijRepository
import com.kest.softij.api.model.Product
import com.kest.softij.api.model.Res
import java.util.*

class MainViewModel:ViewModel() {
    val titleStack = Stack<Int>()
    var title:String? = null
    private var repo = SoftijRepository.getRepo()
    val searchData:MutableLiveData<Res<MutableList<Product>>> = MutableLiveData()
    var recentQuery:String = ""
    var count:MutableLiveData<Int> = MutableLiveData()
    var cartStatus:MutableLiveData<String> = MutableLiveData()
    fun search(query:String,limit:Int,start:Int = 1){
        repo.getListCount(query,count)
        if(query != recentQuery){
            recentQuery = query
            searchData.value = null
        }
        repo.search(query,limit,start,searchData)
    }



}