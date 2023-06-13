package com.example.itr.models

import com.google.gson.annotations.SerializedName

data class DestinationResponse2(

	@field:SerializedName("prediction")
	val prediction: List<PredictionItem>
)

data class PredictionItem(

	@field:SerializedName("place_name")
	val placeName: String,

	@field:SerializedName("image")
	val image: String,

	@field:SerializedName("city")
	val city: String,

	@field:SerializedName("rating")
	val rating: Float,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("lon")
	val lon: Double,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("lat")
	val lat: Double
)
