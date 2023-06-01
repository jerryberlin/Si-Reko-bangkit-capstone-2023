package com.example.itr.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.navArgs
import com.example.itr.R
import com.example.itr.databinding.ActivityDetailBinding
import com.example.itr.models.LatLong
import com.example.itr.ui.maps.MapsFragment

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val args: DetailActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val latLong: LatLong = args.latLong
//        binding.apply {
//            test.text = latLong.lat.toString()
//            test2.text = latLong.long.toString()
//        }

        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        val mapsFragment = MapsFragment()
        val bundle = Bundle()
        bundle.putDouble("lat", latLong.lat)
        bundle.putDouble("lon", latLong.long)
        mapsFragment.arguments = bundle

        fragmentTransaction.add(R.id.fragment_map, mapsFragment)
        fragmentTransaction.commit()
    }
}