package com.example.itr.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.navArgs
import com.example.itr.R
import com.example.itr.databinding.ActivityDetailBinding
import com.example.itr.ui.maps.MapsFragment

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val args: DetailActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            test.text = args.lat.toString()
            test2.text = args.long.toString()
        }

        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        val mapsFragment = MapsFragment()
        val bundle = Bundle()
        bundle.putDouble("lat", args.lat.toDouble())
        bundle.putDouble("lon", args.long.toDouble())
        mapsFragment.arguments = bundle

        fragmentTransaction.add(R.id.fragment_map, mapsFragment)
        fragmentTransaction.commit()
    }
}