package com.example.itr.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    val text: MutableLiveData<String> = MutableLiveData()
}