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
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.itr.R
import com.example.itr.adapter.DestinationListAdapter
import com.example.itr.adapter.DestinationListAdapter2
import com.example.itr.databinding.FragmentHomeBinding
import com.example.itr.models.PredictionItem
import com.example.itr.models.PredictionsItem
import com.example.itr.util.Resource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import java.io.IOException
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var shouldUpdateLocation: Boolean = false
    private val homeViewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.setHasFixedSize(true)

        fetchLocation()

        observeTextHomeFromViewModel()

        Log.d("TAG", "onViewCreated: dipanggil")

        if (currentLocation != null) {
            Log.d("TAG", "onViewCreated: location tidak null")
        }

    }

    private fun observeTextHomeFromViewModel() {
        homeViewModel.text.observe(requireActivity()) { latitude ->
            binding.textHome.text = latitude.toString()
        }
    }

    private fun fetchLocation() {
        if (
            ActivityCompat.checkSelfPermission
                (
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLocation()
        } else {
            requestPermissionLauncher1.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val task = fusedLocationClient?.lastLocation
        task?.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                Log.d("TAG", "getLocation: $currentLocation")
                sendCurrentUserLocation1(currentLocation!!)
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

    private fun sendCurrentUserLocation1(currentLocation: Location) {
        homeViewModel.postUserId(FirebaseAuth.getInstance().currentUser!!.uid)
        homeViewModel.postUserId1.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.progressBarSesuaiDenganAnda.visibility = View.INVISIBLE
                    Log.d("TAG", "sasaas: ${it.data}")
                    it.data?.let { data ->
                        setUserData2(Companion.currentLocation!!, data.prediction)
                    }

                }
                is Resource.Error -> {
                    binding.progressBarSesuaiDenganAnda.visibility = View.INVISIBLE
                    Toast.makeText(
                        requireActivity(), it.data.toString(), Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Loading -> {
                    binding.progressBarSesuaiDenganAnda.visibility = View.VISIBLE
                }
                else -> Unit
            }
        }
        homeViewModel.postUserLocation1(currentLocation.latitude, currentLocation.longitude)
        homeViewModel.postLocation1.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.progressBarTempatTerdekat.visibility = View.INVISIBLE
                    Log.d("TAG", "sendCurrentUserLocation: ${it.data}")
                    it.data?.let { data ->
                        setUserData1(Companion.currentLocation!!, data.predictions)
                    }
                }
                is Resource.Error -> {
                    binding.progressBarTempatTerdekat.visibility = View.INVISIBLE
                }
                is Resource.Loading -> {
                    binding.progressBarTempatTerdekat.visibility = View.VISIBLE
                }
                else -> Unit
            }
        }
    }


    private val requestPermissionLauncher1 = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
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

    @Suppress("DEPRECATION")
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
                    }
                })
            } else {
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

    private fun setUserData1(
        currentLocation: Location,
        listDestination: List<PredictionsItem>
    ) {
        val adapter = DestinationListAdapter(currentLocation, listDestination)
        binding.recyclerView.adapter = adapter
    }

    private fun setUserData2(
        currentLocation: Location,
        listDestination: List<PredictionItem>
    ) {
        val adapter = DestinationListAdapter2(currentLocation, listDestination)
        binding.rvSesuaiDenganAnda.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        if (shouldUpdateLocation) {
            fetchLocation() // Panggil fetchLocation() jika shouldUpdateLocation bernilai true
            shouldUpdateLocation =
                false // Setelah memperbarui lokasi, atur shouldUpdateLocation kembali ke false
        }
    }

    companion object {
        var currentLocation: Location? = null
    }
}