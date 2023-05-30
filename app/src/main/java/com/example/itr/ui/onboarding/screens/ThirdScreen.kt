package com.example.itr.ui.onboarding.screens

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.itr.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ThirdScreen : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_third_screen, container, false)

        view?.findViewById<FloatingActionButton>(R.id.btn_finish)?.setOnClickListener {
//            findNavController().navigate(R.id.action_viewPagerFragment_to_mainActivity)
            findNavController().navigate(R.id.action_viewPagerFragment_to_accountOptions)
//            requireActivity().finish()
            onBoardingFinished()
        }

        return view
    }

    private fun onBoardingFinished() {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }
}