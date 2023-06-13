package com.example.itr.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.itr.models.DestinationResponse
import com.example.itr.models.DestinationResponse2
import com.example.itr.network.ApiConfig
import com.example.itr.util.Resource
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {
    val text: MutableLiveData<String> = MutableLiveData()

    private val _postLocation1 = MutableLiveData<Resource<DestinationResponse>>()
    val postLocation1: LiveData<Resource<DestinationResponse>> = _postLocation1

    private val _postUserId1 = MutableLiveData<Resource<DestinationResponse2>>()
    val postUserId1: LiveData<Resource<DestinationResponse2>> = _postUserId1

    fun postUserLocation1(lat: Double, lon: Double){
        _postLocation1.value = Resource.Loading()
        val client = ApiConfig.getApiService().postCurrentUserLocation1(lat, lon)
        client.enqueue(object : Callback<DestinationResponse> {
            override fun onResponse(
                call: Call<DestinationResponse>,
                response: Response<DestinationResponse>
            ) {
                if (response.isSuccessful) {
                    _postLocation1.value = response.body()?.let { Resource.Success(it) }
                    Log.d("TAG", "onResponse: ${_postLocation1.value?.data}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = errorBody?.let { JSONObject(it).getString("message") }
                    _postLocation1.value = Resource.Error(errorMessage)
                    Log.e("TAG", "onResponse: $errorMessage")
                }
            }

            override fun onFailure(call: Call<DestinationResponse>, t: Throwable) {
                _postLocation1.value = Resource.Error("${t.message}")
                Log.e("TAG", "onFailure: ${t.message}")
            }

        })
    }

    fun postUserId(userId: String){
        _postUserId1.value = Resource.Loading()
        val client = ApiConfig.getApiService().postUserId(userId)
        Log.d("TAG", "postUserId: $userId")
        client.enqueue(object : Callback<DestinationResponse2> {
            override fun onResponse(
                call: Call<DestinationResponse2>,
                response: Response<DestinationResponse2>
            ) {
                if (response.isSuccessful) {
                    _postUserId1.value = response.body()?.let { Resource.Success(it) }
                    Log.d("TAG", "onResponseee: ${_postUserId1.value?.data}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = errorBody?.let { JSONObject(it).getString("message") }
                    _postUserId1.value = Resource.Error(errorMessage)
                    Log.e("TAG", "onResponseeee: $errorMessage")
                }
            }

            override fun onFailure(call: Call<DestinationResponse2>, t: Throwable) {
                _postUserId1.value = Resource.Error("${t.message}")
                Log.e("TAG", "onFailure: ${t.message}")
            }

        })
    }
}