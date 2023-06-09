package com.example.itr.models

import com.google.gson.annotations.SerializedName

data class PostCurrentUserLocationResponse(

	@field:SerializedName("lon")
	val lon: Double,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("lat")
	val lat: Double
)
