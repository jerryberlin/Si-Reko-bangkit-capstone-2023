package com.example.itr.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.itr.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashFragment : Fragment() {

    private val splashDuration: Long = 3000
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                handler.postDelayed({
                    if (isAdded) {
                        if (onBoardingFinished()) {
                            findNavController().navigate(R.id.action_splashFragment_to_mainActivity)
                            requireActivity().finish()
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