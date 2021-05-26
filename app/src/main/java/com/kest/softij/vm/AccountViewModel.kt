package com.kest.softij.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kest.softij.SoftijRepository
import com.kest.softij.api.model.Res
import com.kest.softij.api.model.User

class AccountViewModel : ViewModel() {
    val userData = SoftijRepository.getRepo().getUserInfo()
    val updateStatus: MutableLiveData<Res<Unit>> = MutableLiveData()
    lateinit var user: User

    fun updateUserInfo(){
        SoftijRepository.getRepo().postEditAccount(user,updateStatus)
    }

}