package com.example.itr.data.network

import com.example.itr.models.DestinationResponseItem
import com.example.itr.models.PostCurrentUserLocationResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("destinations")
    fun getDestinations(): Call<List<DestinationResponseItem>>

    @GET("destination/{id}")
    fun getDestinationById(
        @Path("id") id: Int
    ): Call<List<DestinationResponseItem>>

    @FormUrlEncoded
    @POST("location")
    fun postCurrentUserLocation(
        @Field("lat") lat: Double, @Field("lon") lon: Double
//        @Field("lat") lat: Double, @Field("lon") lon: Double, @Field("userId") userId: String
    ): Call<PostCurrentUserLocationResponse>
}