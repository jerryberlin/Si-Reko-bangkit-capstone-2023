package com.example.itr.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.itr.models.DestinationResponseItem
import com.example.itr.models.PostCurrentUserLocationResponse
import com.example.itr.network.ApiConfig
import com.example.itr.util.Resource
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {
    val text: MutableLiveData<String> = MutableLiveData()

    private val _destination = MutableLiveData<Resource<List<DestinationResponseItem>>>()
    val destination: LiveData<Resource<List<DestinationResponseItem>>> = _destination

    private val _postLocation = MutableLiveData<Resource<PostCurrentUserLocationResponse>>()
    val postLocation: LiveData<Resource<PostCurrentUserLocationResponse>> = _postLocation

    init {
        getDestination()
    }

    fun postUserLocation(lat: Double, lon: Double){
//    fun postUserLocation(lat: Double, lon: Double, userId: String){
        _postLocation.value = Resource.Loading()
        val client = ApiConfig.getApiService().postCurrentUserLocation(lat, lon)
//        val client = ApiConfig.getApiService().postCurrentUserLocation(lat, lon, userId)
        client.enqueue(object : Callback<PostCurrentUserLocationResponse> {
            override fun onResponse(
                call: Call<PostCurrentUserLocationResponse>,
                response: Response<PostCurrentUserLocationResponse>
            ) {
                if (response.isSuccessful) {
                    _postLocation.value = response.body()?.let { Resource.Success(it) }
                    Log.d("TAG", "onResponse: ${_postLocation.value?.data?.message}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = errorBody?.let { JSONObject(it).getString("message") }
                    _postLocation.value = Resource.Error(errorMessage)
                    Log.e("TAG", "onResponse: $errorMessage")
                }
            }

            override fun onFailure(call: Call<PostCurrentUserLocationResponse>, t: Throwable) {
                _postLocation.value = Resource.Error("${t.message}")
                Log.e("TAG", "onFailure: ${t.message}")
            }

        })
    }

    fun getDestination() {
        _destination.value = Resource.Loading()
        val client = ApiConfig.getApiService().getDestinations()
        client.enqueue(object : Callback<List<DestinationResponseItem>> {
            override fun onResponse(
                call: Call<List<DestinationResponseItem>>,
                response: Response<List<DestinationResponseItem>>
            ) {
                if (response.isSuccessful) {
                    _destination.value = response.body()?.let { Resource.Success(it) }
                    Log.d("TAG", "onResponsea: ${_destination.value?.data}")
                } else {
                    _destination.value = Resource.Error(response.message())
                    Log.e("TAG", "onResponseb: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<DestinationResponseItem>>, t: Throwable) {
                _destination.value = Resource.Error("${t.message}")
                Log.e("TAG", "onFailure: ${t.message}")
            }
        })
    }
}