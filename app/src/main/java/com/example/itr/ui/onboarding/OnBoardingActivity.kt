package com.example.itr.ui.onboarding

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.itr.databinding.ActivityOnboardingBinding
import com.example.itr.ui.theme.ThemeFactory
import com.example.itr.ui.theme.ThemeViewModel
import com.example.setting.ThemePref
import dagger.hilt.android.AndroidEntryPoint


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val settings = ThemePref.getInstance(dataStore)
        val theme = ViewModelProvider(this, ThemeFactory(settings))[ThemeViewModel::class.java]
        theme.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}