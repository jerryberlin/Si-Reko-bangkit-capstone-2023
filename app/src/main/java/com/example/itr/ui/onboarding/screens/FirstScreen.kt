package com.example.itr.ui.onboarding.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.itr.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FirstScreen : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_first_screen, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)

        view?.findViewById<FloatingActionButton>(R.id.btn_next)?.setOnClickListener {
            viewPager?.currentItem = 1
        }

        return view
    }

}