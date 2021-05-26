package com.kest.softij.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kest.softij.SoftijRepository
import com.kest.softij.api.model.Address
import com.kest.softij.api.model.Res

class AddressViewModel : ViewModel() {
    private val repo = SoftijRepository.getRepo()
    val addressList = SoftijRepository.getRepo().getAddress()
    val postLiveData:MutableLiveData<Res<Unit>> = MutableLiveData()

    fun postEditAddress(address: Address){
        repo.postEditAddress(address,postLiveData)
    }

    fun postDeleteAddress(addressId:Int){
        repo.postDeleteAddress(addressId,postLiveData)
    }

    fun postInsertAddress(address: Address){
        repo.postInsertAddress(address,postLiveData)
    }
}