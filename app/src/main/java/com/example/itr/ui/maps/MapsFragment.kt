package com.example.itr.ui.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.itr.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        val latUser = arguments?.getDouble("latUser", 0.0)
        val lonUser = arguments?.getDouble("lonUser", 0.0)
        val latDest = arguments?.getDouble("latDest", 0.0)
        val lonDest = arguments?.getDouble("lonDest", 0.0)

        if (latUser != null && lonUser != null && latDest != null && lonDest != null) {
            val locationUser = LatLng(latUser, lonUser)
            val locationDest = LatLng(latDest, lonDest)

            val userMarker = googleMap.addMarker(
                MarkerOptions()
                    .position(locationUser)
                    .title("User")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )

            val destMarker = googleMap.addMarker(
                MarkerOptions()
                    .position(locationDest)
                    .title("Destination")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )

            googleMap.setOnMarkerClickListener {
                true // Kembalikan true untuk menandakan bahwa Anda sudah menangani klik pada marker
            }

            val boundsBuilder = LatLngBounds.Builder()
            boundsBuilder.include(locationUser)
            boundsBuilder.include(locationDest)
            val bounds = boundsBuilder.build()

            val padding = 200 // Jarak padding dari tepi peta dalam piksel

            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
            googleMap.animateCamera(cameraUpdate)

            googleMap.uiSettings.apply {
                isScrollGesturesEnabled = false
                isZoomGesturesEnabled = false
                isRotateGesturesEnabled = false
                isTiltGesturesEnabled = false
                isZoomControlsEnabled = false
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}