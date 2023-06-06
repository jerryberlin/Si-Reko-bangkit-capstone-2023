package com.example.itr.util

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

fun calculateDistance(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double,
    unit: String = "km"
): Double {
    val theta = lon1 - lon2
    var dist = sin(Math.toRadians(lat1)) * sin(Math.toRadians(lat2)) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * cos(Math.toRadians(theta))
    dist = acos(dist)
    dist = Math.toDegrees(dist)
    dist *= 60 * 1.1515

    if (unit == "km") {
        dist *= 1.609344
    } else if (unit == "mi") {
        dist *= 0.8684
    }

    return dist
}
