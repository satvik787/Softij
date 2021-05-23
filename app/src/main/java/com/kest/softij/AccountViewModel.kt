package com.kest.softij

import androidx.lifecycle.ViewModel

class AccountViewModel : ViewModel() {
    val userData = SoftijRepository.getRepo().getUserInfo(31)
}