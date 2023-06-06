package com.example.itr.adapter

import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.itr.databinding.CardItemBinding
import com.example.itr.models.DestinationItem
import com.example.itr.models.DestinationResponseItem
import com.example.itr.models.LatLong
import com.example.itr.ui.home.HomeFragmentDirections
import com.example.itr.util.calculateDistance
import java.io.IOException
import java.util.*

class DestinationListAdapter(
    private val currentLocation: Location,
    private var listDestination: List<DestinationResponseItem>
) :
    RecyclerView.Adapter<DestinationListAdapter.ListViewHolder>() {
    class ListViewHolder(var binding: CardItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = CardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val destination = listDestination[position]
        Glide.with(holder.itemView.context)
            .load(destination.image)
            .into(holder.binding.imageView)
        holder.binding.textNamaTempat.text = destination.placeName
        holder.binding.textRating.text = destination.rating.toString()
        translateLocation(destination.lat, destination.lon, holder.binding.textLokasi)
        val distanceInKm = calculateDistance(
            currentLocation.latitude, currentLocation.longitude,
            destination.lat, destination.lon
        )
        val textDistanceInKm = "$distanceInKm km"
        holder.binding.textJarak.text = textDistanceInKm
        holder.binding.cardView.setOnClickListener { view ->
            val action = HomeFragmentDirections.actionNavigationHomeToDetailActivity(
                LatLong(
                    currentLocation.latitude,
                    currentLocation.longitude
                ),
                DestinationItem(
                    destination.placeName,
                    destination.image,
                    destination.rating,
                    destination.lon,
                    destination.id,
                    destination.deskripsi,
                    destination.lat,
                    textDistanceInKm
                )
            )
            view.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return listDestination.size
    }

    private fun translateLocation(latitude: Double, longitude: Double, textView: TextView) {
        val geocoder = Geocoder(textView.context, Locale.getDefault())
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latitude, longitude, 1, object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        if (addresses.isNotEmpty()) {
                            val address = addresses[0]
                            val cityName = address.subAdminArea
                            val provinceName = address.adminArea

                            val locationText = "$cityName, $provinceName"
                            textView.text = locationText
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
                    textView.text = locationText
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}