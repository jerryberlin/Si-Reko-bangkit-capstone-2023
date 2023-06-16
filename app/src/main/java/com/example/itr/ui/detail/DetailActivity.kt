package com.example.itr.ui.detail

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.navArgs
import com.bumptech.glide.Glide
import com.example.itr.R
import com.example.itr.databinding.ActivityDetailBinding
import com.example.itr.models.DestinationItem
import com.example.itr.models.LatLong
import com.example.itr.models.MDestination
import com.example.itr.ui.maps.MapsFragment
import com.example.itr.util.Resource
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val args: DetailActivityArgs by navArgs()
    private lateinit var dialog: BottomSheetDialog
    private val detailViewModel by viewModels<DetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val latLong: LatLong = args.latLong
        val destination: DestinationItem = args.destination

        showData(destination)
        showMap(latLong, destination)
        showBottomSheet(destination)
        btnBackPressed()

        binding.bookmarkButton.setOnClickListener {
            btnBookmarkPressed(destination)
        }

    }

    private fun showBottomSheet(destination: DestinationItem) {
        binding.btnRate.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.bottomsheetlayout, null)
            dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
            dialog.setContentView(dialogView)
            dialog.show()
            showSubmitButton(destination)
        }
    }

    private fun showSubmitButton(destination: DestinationItem) {
        val ratingBar = dialog.findViewById<RatingBar>(R.id.rating_bar)
        val buttonSubmit = dialog.findViewById<Button>(R.id.btn_submit)
        val text = dialog.findViewById<TextView>(R.id.textView4)

        text?.text = "Bagaimana pengalaman liburan anda di ${destination.placeName}?"

        ratingBar?.setOnRatingBarChangeListener { rBar, _, _ ->
            buttonSubmit?.isEnabled = rBar.rating.toDouble() != 0.0
        }

        btnSubmitPressed(buttonSubmit, ratingBar, destination)
    }


    private fun btnSubmitPressed(
        buttonSubmit: Button?,
        ratingBar: RatingBar?,
        destination: DestinationItem
    ) {
        buttonSubmit?.setOnClickListener {
            if (buttonSubmit.isEnabled) {
                val ratingValue = ratingBar?.rating
                detailViewModel.postRating(
                    FirebaseAuth.getInstance().currentUser!!.uid,
                    destination.id,
                    ratingValue!!
                )
                detailViewModel.postRating.observe(this) {
                    when (it) {
                        is Resource.Success -> {
                            Toast.makeText(
                                this,
                                "Tombol Submit Ditekan! Rating: $ratingValue",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d("TAG", "sendCurrentUserLocation: ${it.data}")
                        }
                        is Resource.Error -> {
                            Toast.makeText(
                                this, it.data.toString(), Toast.LENGTH_SHORT
                            ).show()
                        }
                        is Resource.Loading -> {

                        }
                        else -> Unit
                    }
                }
            }
            dialog.hide()
        }
    }

    private fun btnBookmarkPressed(destination: DestinationItem) {
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val mDestination = MDestination(
            destination.placeName,
            destination.image,
            destination.rating,
            destination.lon,
            destination.id,
            destination.deskripsi,
            destination.city,
            destination.lat,
            "0",
            currentUserId
        )
        checkBookmarkExists(mDestination)
    }

    private fun checkBookmarkExists(mDestination: MDestination) {
        val db = FirebaseFirestore.getInstance()
        val dbCollection =
            db.collection("user").document(FirebaseAuth.getInstance().currentUser!!.uid)
                .collection("destination")

        // Cek apakah destinasi sudah ada dalam bookmark pengguna
        dbCollection.whereEqualTo("id", mDestination.id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                binding.bookmarkButton.setImageResource(R.drawable.ic_baseline_bookmarked)
                if (querySnapshot.isEmpty) {
                    // Destinasi belum ada dalam bookmark, tambahkan ke database
                    saveToFirebase(mDestination)
                } else {
                    // Destinasi sudah ada dalam bookmark, hapus dari database
                    val documentSnapshot = querySnapshot.documents[0]
                    val docId = documentSnapshot.id
                    dbCollection.document(docId)
                        .delete()
                        .addOnSuccessListener {
                            binding.bookmarkButton.setImageResource(R.drawable.baseline_bookmark_border_24)
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                            // Penanganan kesalahan saat menghapus dari Firestore.
                        }
                }
            }
            .addOnFailureListener { exception ->
                // Penanganan kesalahan saat mengambil data dari Firestore.
            }
    }


    private fun saveToFirebase(mDestination: MDestination) {
        val db = FirebaseFirestore.getInstance()
        val dbCollection =
            db.collection("user").document(FirebaseAuth.getInstance().currentUser!!.uid)
                .collection("destination")

        if (mDestination.toString().isNotEmpty()) {
            dbCollection
                .add(mDestination)
                .addOnSuccessListener {
                    val docId = it.id
                    dbCollection.document(docId)
                }
        } else {

        }
    }


    private fun btnBackPressed() {
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun showData(destination: DestinationItem) {
        binding.apply {
            binding.placeNameTextView.text = destination.placeName
            binding.distanceTextView.text = destination.distance
            binding.ratingTextView.text = destination.rating.toString()
            binding.descriptionTextView.text = destination.deskripsi
            binding.locationTextView.text = destination.city
            Glide.with(applicationContext)
                .load(destination.image)
                .into(binding.imgDestinasi)
        }

        val db = FirebaseFirestore.getInstance()
        val dbCollection =
            db.collection("user").document(FirebaseAuth.getInstance().currentUser!!.uid)
                .collection("destination")

        // Cek apakah destinasi sudah ada dalam bookmark pengguna
        dbCollection.whereEqualTo("id", destination.id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    // Destinasi belum ada dalam bookmark
                    binding.bookmarkButton.setImageResource(R.drawable.baseline_bookmark_border_24)
                } else {
                    // Destinasi sudah ada dalam bookmark
                    binding.bookmarkButton.setImageResource(R.drawable.ic_baseline_bookmarked)
                }
            }
            .addOnFailureListener { exception ->
                // Penanganan kesalahan saat mengambil data dari Firestore.
            }
    }

    private fun showMap(latLong: LatLong, destination: DestinationItem) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        val mapsFragment = MapsFragment()
        val bundle = Bundle()
        bundle.putDouble("latUser", latLong.latUser)
        bundle.putDouble("lonUser", latLong.longUser)
        bundle.putDouble("latDest", destination.lat)
        bundle.putDouble("lonDest", destination.lon)
        mapsFragment.arguments = bundle

        fragmentTransaction.add(R.id.fragment_map, mapsFragment)
        fragmentTransaction.commit()
    }

}
