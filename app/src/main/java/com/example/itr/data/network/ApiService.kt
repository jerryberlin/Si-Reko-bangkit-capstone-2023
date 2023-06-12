package com.example.itr.data.network

import com.example.itr.models.DestinationResponse
import com.example.itr.models.PostRatingResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("location/byLat_Lon")
    fun postCurrentUserLocation1(
        @Field("latitude") lat: Double, @Field("longitude") lon: Double
    ): Call<DestinationResponse>

    @FormUrlEncoded
    @POST("submit_User_Profile")
    fun postRating(
        @Field("user_id") userId: String, @Field("place_id") placeId: Int, @Field("place_ratings") placeRating: Float
    ): Call<PostRatingResponse>
}