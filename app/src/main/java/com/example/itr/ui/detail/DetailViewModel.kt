package com.example.itr.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.itr.models.PostRatingResponse
import com.example.itr.network.ApiConfig
import com.example.itr.util.Resource
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel : ViewModel() {
    private val _postRating = MutableLiveData<Resource<PostRatingResponse>>()
    val postRating: LiveData<Resource<PostRatingResponse>> = _postRating

    fun postRating(userId: String, placeId: Int, placeRating: Float) {
        _postRating.value = Resource.Loading()
        val client = ApiConfig.getApiService().postRating(userId, placeId, placeRating)
        client.enqueue(object : Callback<PostRatingResponse> {
            override fun onResponse(
                call: Call<PostRatingResponse>,
                response: Response<PostRatingResponse>
            ) {
                if (response.isSuccessful) {
                    _postRating.value = response.body()?.let { Resource.Success(it) }
                    Log.d("TAG", "onResponse: ${_postRating.value?.data}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = errorBody?.let { JSONObject(it).getString("message") }
                    _postRating.value = Resource.Error(errorMessage)
                    Log.e("TAG", "onResponse: $errorMessage")
                }
            }

            override fun onFailure(call: Call<PostRatingResponse>, t: Throwable) {
                _postRating.value = Resource.Error("${t.message}")
                Log.e("TAG", "onFailure: ${t.message}")
            }

        })
    }
}