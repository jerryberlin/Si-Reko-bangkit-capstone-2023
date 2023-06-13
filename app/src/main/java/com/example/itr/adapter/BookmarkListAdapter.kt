package com.example.itr.adapter

import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.itr.databinding.CardSmallItemBinding
import com.example.itr.models.DestinationItem
import com.example.itr.models.LatLong
import com.example.itr.models.MDestination
import com.example.itr.ui.bookmark.BookmarkFragmentDirections
import com.example.itr.util.calculateDistance

class BookmarkListAdapter(
    private val currentLocation: Location,
    private var listDestination: ArrayList<MDestination>
) : RecyclerView.Adapter<BookmarkListAdapter.ListViewHolder>() {
    class ListViewHolder(var binding: CardSmallItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            CardSmallItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val destination = listDestination[position]
        Glide.with(holder.itemView.context)
            .load(destination.image)
            .into(holder.binding.imageView)
        holder.binding.textNamaTempat.text = destination.placeName
        holder.binding.textRating.text = destination.rating.toString()
        val distanceInKm = calculateDistance(
            currentLocation.latitude, currentLocation.longitude,
            destination.lat, destination.lon
        )
        holder.binding.textLokasi.text = destination.city
        val formattedDistance = String.format("%.2f", distanceInKm)
        val textDistanceInKm = "$formattedDistance km"
        holder.binding.textJarak.text = textDistanceInKm
        holder.binding.textJarak.text = textDistanceInKm
        holder.binding.cardView.setOnClickListener { view ->
            val action = BookmarkFragmentDirections.actionNavigationBookmarkToDetailActivity(
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