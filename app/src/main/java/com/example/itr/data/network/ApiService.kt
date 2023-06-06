package com.example.itr.data.network

import com.example.itr.models.DestinationResponseItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("destinations")
    fun getDestinations(): Call<List<DestinationResponseItem>>

    @GET("destination/{id}")
    fun getDestinationById(
        @Path("id") id: Int
    ): Call<List<DestinationResponseItem>>
}