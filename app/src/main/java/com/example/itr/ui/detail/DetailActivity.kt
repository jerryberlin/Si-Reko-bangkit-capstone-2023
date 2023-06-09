package com.example.itr.ui.detail

import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
        val destination: DestinationItem = args.destination

        showData(destination)
        showMap(latLong, destination)
        showBottomSheet(destination)
        btnBackPressed()

        binding.bookmarkButton.setOnClickListener {
            btnBookmarkPressed(destination)
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
            destination.lat,
            "0",
            currentUserId
        )
        checkBookmarkExists(mDestination)
    }

    private fun checkBookmarkExists(mDestination: MDestination) {
        val db = FirebaseFirestore.getInstance()
        val dbCollection = db.collection("destinations")

        // Cek apakah destinasi sudah ada dalam bookmark pengguna
        dbCollection.whereEqualTo("id", mDestination.id)
            .whereEqualTo("userId", mDestination.userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                binding.bookmarkButton.setImageResource(R.drawable.ic_baseline_bookmarked)
                if (querySnapshot.isEmpty) {
                    // Destinasi belum ada dalam bookmark, tambahkan ke database
                    saveToFirebase(mDestination)
                    Toast.makeText(this, "Menambah ke database", Toast.LENGTH_SHORT).show()
                } else {
                    // Destinasi sudah ada dalam bookmark, hapus dari database
                    val documentSnapshot = querySnapshot.documents[0]
                    val docId = documentSnapshot.id
                    dbCollection.document(docId)
                        .delete()
                        .addOnSuccessListener {
                            binding.bookmarkButton.setImageResource(R.drawable.baseline_bookmark_border_24)
                            Toast.makeText(this, "Menghapus dari database", Toast.LENGTH_SHORT)
                                .show()
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
        val dbCollection = db.collection("destinations")

        if (mDestination.toString().isNotEmpty()) {
            dbCollection
                .add(mDestination)
                .addOnSuccessListener {
                    val docId = it.id
                    dbCollection.document(docId)
//                        .update(hashMapOf("id" to docId) as Map<String, Any>)
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
            Glide.with(applicationContext)
                .load(destination.image)
                .into(binding.imgDestinasi)
        }

        val db = FirebaseFirestore.getInstance()
        val dbCollection = db.collection("destinations")

        // Cek apakah destinasi sudah ada dalam bookmark pengguna
        dbCollection.whereEqualTo("id", destination.id)
            .whereEqualTo("userId", FirebaseAuth.getInstance().currentUser!!.uid)
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
