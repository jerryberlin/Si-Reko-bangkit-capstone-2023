package com.example.itr.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.setting.ThemePref

class ThemeFactory(private val pref: ThemePref) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
            return ThemeViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}