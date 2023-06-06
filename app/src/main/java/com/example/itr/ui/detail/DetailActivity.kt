package com.example.itr.ui.detail

import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.navArgs
import com.example.itr.R
import com.example.itr.databinding.ActivityDetailBinding
import com.example.itr.models.LatLong
import com.example.itr.ui.maps.MapsFragment
import com.google.android.material.bottomsheet.BottomSheetDialog

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val args: DetailActivityArgs by navArgs()
    private lateinit var dialog: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val latLong: LatLong = args.latLong

        showMap(latLong)
        showBottomSheet()
    }

    private fun showMap(latLong: LatLong) {
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


    private fun showBottomSheet() {
        binding.btnRate.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.bottomsheetlayout, null)
            dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
            dialog.setContentView(dialogView)
            dialog.show()
            showSubmitButton()
        }
    }

    private fun showSubmitButton() {
        val ratingBar = dialog.findViewById<RatingBar>(R.id.rating_bar)
        val buttonSubmit = dialog.findViewById<Button>(R.id.btn_submit)

        ratingBar?.setOnRatingBarChangeListener { rBar, _, _ ->
            buttonSubmit?.isEnabled = rBar.rating.toDouble() != 0.0
        }

        btnSubmitPressed(buttonSubmit, ratingBar)
    }

    private fun btnSubmitPressed(buttonSubmit: Button?, ratingBar: RatingBar?) {
        buttonSubmit?.setOnClickListener {
            if (buttonSubmit.isEnabled) {
                val ratingValue = ratingBar?.rating
                Toast.makeText(
                    this,
                    "Tombol Submit Ditekan! Rating: $ratingValue",
                    Toast.LENGTH_SHORT
                ).show()
            }
            dialog.hide()
        }
    }
}