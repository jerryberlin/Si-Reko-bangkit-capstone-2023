package com.example.itr.adapter

import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.itr.databinding.CardItemBinding
import com.example.itr.models.DestinationItem
import com.example.itr.models.LatLong
import com.example.itr.models.PredictionItem
import com.example.itr.ui.home.HomeFragmentDirections
import com.example.itr.util.calculateDistance

class DestinationListAdapter2(
    private val currentLocation: Location,
    private var listDestination: List<PredictionItem>
) :
    RecyclerView.Adapter<DestinationListAdapter2.ListViewHolder>() {
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
//        translateLocation(destination.lat, destination.lon, holder.binding.textLokasi)
        holder.binding.textLokasi.text = destination.city
        val distanceInKm = calculateDistance(
            currentLocation.latitude, currentLocation.longitude,
            destination.lat, destination.lon
        )
        val formattedDistance = String.format("%.2f", distanceInKm)
        val textDistanceInKm = "$formattedDistance km"
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
                    destination.description,
                    destination.city,
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

}