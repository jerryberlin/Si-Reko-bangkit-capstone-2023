package com.example.itr.models

import com.google.gson.annotations.SerializedName

data class DestinationResponse(

	@field:SerializedName("predictions")
	val predictions: List<PredictionsItem>
)

data class PredictionsItem(

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("place_name")
	val placeName: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("city")
	val city: String,

	@field:SerializedName("rating")
	val rating: Float,

	@field:SerializedName("lat")
	val lat: Double,

	@field:SerializedName("lon")
	val lon: Double,

	@field:SerializedName("image")
	val image: String
)
