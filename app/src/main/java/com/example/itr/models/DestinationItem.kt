package com.example.itr.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DestinationItem(

    val placeName: String,

    val image: String,

    val rating: Float,

    val lon: Double,

    val id: Int,

    val deskripsi: String,

    val city: String,

    val lat: Double,

    val distance: String
) : Parcelable
