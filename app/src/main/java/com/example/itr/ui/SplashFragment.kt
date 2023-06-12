package com.example.itr.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.itr.R
import com.example.itr.ui.theme.ThemeFactory
import com.example.itr.ui.theme.ThemeViewModel
import com.example.setting.ThemePref
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SplashFragment : Fragment() {

    private val splashDuration: Long = 1500
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var settings: ThemePref
    private lateinit var theme: ThemeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        val dataStore = requireContext().dataStore
//        settings = ThemePref.getInstance(dataStore)
//        theme = ViewModelProvider(this, ThemeFactory(settings))[ThemeViewModel::class.java]
//
//        theme.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
//            if (isDarkModeActive) {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//            } else {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//            }
//        }

        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                handler.postDelayed({
                    if (isAdded) {
                        if (onBoardingFinished()) {
                            if (FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()) {
                                findNavController().navigate(R.id.action_splashFragment_to_accountOptions)
                            } else {
                                findNavController().navigate(R.id.action_splashFragment_to_mainActivity)
                                requireActivity().finish()
                            }
                        } else {
                            findNavController().navigate(R.id.action_splashFragment_to_viewPagerFragment)
                        }
                    }
                }, splashDuration)
            }
        }
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    private fun onBoardingFinished(): Boolean {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }
}