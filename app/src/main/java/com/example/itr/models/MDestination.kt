package com.example.itr.models

data class MDestination(
    val placeName: String,

    val image: String,

    val rating: Float,

    val lon: Double,

    val id: Int,

    val deskripsi: String,

    val city: String,

    val lat: Double,

    val distance: String,

    val userId: String
) {
    constructor() : this("", "", 0.0f, 0.0, 0, "", "",0.0, "", "")
}
