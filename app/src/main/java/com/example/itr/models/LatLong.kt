package com.example.itr.models

import java.io.Serializable

data class LatLong(
    var latUser: Double = 0.0,
    var longUser: Double = 0.0
) : Serializable