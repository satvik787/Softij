package com.kest.softij.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kest.softij.SoftijRepository
import com.kest.softij.api.model.Res
import com.kest.softij.api.model.User

class AccountViewModel : ViewModel() {
    val userData = SoftijRepository.getRepo().databaseGetUser()
    val updateStatus: MutableLiveData<Res<Unit>> = MutableLiveData()
    val updateSubData:MutableLiveData<Res<Unit>> = MutableLiveData()
    lateinit var user: User
    lateinit var updateUser:User

    fun updateUserInfo(){
        SoftijRepository.getRepo().postEditAccount(updateUser,updateStatus)

    }

    fun updateLocal(user:User){
        SoftijRepository.getRepo().databaseUpdateUser(user)
    }

    fun postUpdateSub(){
        SoftijRepository.getRepo().postUpdateSub(updateSubData)
    }

}