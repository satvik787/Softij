package com.kest.softij.vm

import androidx.lifecycle.ViewModel
import com.kest.softij.SoftijRepository

class AddressViewModel : ViewModel() {
    val addressList = SoftijRepository.getRepo().getAddress(31)
}