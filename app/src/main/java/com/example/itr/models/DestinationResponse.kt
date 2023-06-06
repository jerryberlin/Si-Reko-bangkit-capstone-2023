package com.example.itr.models

import com.google.gson.annotations.SerializedName

data class DestinationResponseItem(

    @field:SerializedName("place_name")
    val placeName: String,

    @field:SerializedName("image")
    val image: String,

    @field:SerializedName("rating")
    val rating: Any,

    @field:SerializedName("lon")
    val lon: Double,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("deskripsi")
    val deskripsi: String,

    @field:SerializedName("lat")
    val lat: Double
)
