package com.example.itr.data.network

import com.example.itr.models.DestinationResponseItem
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("destinations")
    fun getDestinations(): Call<List<DestinationResponseItem>>
}