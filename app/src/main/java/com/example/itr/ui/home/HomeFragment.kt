package com.example.itr.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.itr.R
import com.example.itr.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var currentLocation: Location? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var homeViewModel: HomeViewModel
    private var shouldUpdateLocation: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        homeViewModel.text.observe(requireActivity()) { lattitude ->
            binding.textHome.text = lattitude.toString()
        }

        fetchLocation()

        binding.btnTodetail.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionNavigationHomeToDetailActivity(
                    currentLocation!!.latitude.toFloat(),
                    currentLocation!!.longitude.toFloat()
                )
            )
        }

    }

    private fun fetchLocation() {
        if (
            ActivityCompat.checkSelfPermission
                (
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLocation()
        } else {
            requestPermissionLauncher1.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val task = fusedLocationClient?.lastLocation
        task?.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
//                val destinationLat = -2.969198
//                val destinationLon = 104.763651
//
//                val distanceInKm = calculateDistance(currentLocation!!.latitude, currentLocation!!.longitude, destinationLat, destinationLon)
//                println("Jarak antara kedua titik: $distanceInKm km")
                Log.d("TAG", "getLocation: $currentLocation")
                getAddressFromLocation(
                    currentLocation?.latitude ?: 0.0,
                    currentLocation?.longitude ?: 0.0
                )
            } else {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.location_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private val requestPermissionLauncher1 = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getLocation()
            }
            else -> {
                val builder = AlertDialog.Builder(requireActivity())
                val customView = LayoutInflater.from(requireActivity())
                    .inflate(R.layout.layout_custom_dialog, null)
                builder.setView(customView)
                builder.setCancelable(false)

                val myDialog = builder.create()
                myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                myDialog.show()

                val buttonLater = myDialog.findViewById<Button>(R.id.button_later)
                val buttonEnable = myDialog.findViewById<Button>(R.id.button_enable)


                buttonLater.setOnClickListener {
                    myDialog.dismiss()
                    requireActivity().finish()
                }

                buttonEnable.setOnClickListener {
                    myDialog.dismiss()
                    shouldUpdateLocation = true
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also { intent ->
                        val uri = Uri.fromParts("package", activity?.packageName, null)
                        intent.data = uri
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }

                }
            }
        }
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latitude, longitude, 1, object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        if (addresses.isNotEmpty()) {
                            val address = addresses[0]
                            val cityName = address.subAdminArea
                            val provinceName = address.adminArea

                            val locationText = "$cityName, $provinceName"
                            binding.textHome.text = locationText
                        }
                    }

                    override fun onError(errorMessage: String?) {
                        super.onError(errorMessage)
                        // Tangani kesalahan di sini jika diperlukan
                    }
                })
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (addresses!!.isNotEmpty()) {
                    val address = addresses[0]
                    val cityName = address.subAdminArea
                    val provinceName = address.adminArea

                    val locationText = "$cityName, $provinceName"
                    binding.textHome.text = locationText
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    override fun onResume() {
        super.onResume()
        if (shouldUpdateLocation) {
            fetchLocation() // Panggil fetchLocation() jika shouldUpdateLocation bernilai true
            shouldUpdateLocation =
                false // Setelah memperbarui lokasi, atur shouldUpdateLocation kembali ke false
        }
    }
}