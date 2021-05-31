package com.example.navigationcomponent.ui.tourguide

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShareViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is tour Fragment"
    }
    val text: LiveData<String> = _text
}