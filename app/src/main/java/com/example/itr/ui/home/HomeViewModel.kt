package com.example.itr.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.itr.models.DestinationResponseItem
import com.example.itr.network.ApiConfig
import com.example.itr.util.Resource
import retrofit2.Call
import retrofit2.Callback

class HomeViewModel : ViewModel() {
    val text: MutableLiveData<String> = MutableLiveData()

    private val _destination = MutableLiveData<Resource<List<DestinationResponseItem>>>()
    val destination: LiveData<Resource<List<DestinationResponseItem>>> = _destination

    init {
        getDestination()
    }

    fun getDestination() {
        _destination.value = Resource.Loading()
        val client = ApiConfig.getApiService().getDestinations()
        client.enqueue(object : Callback<List<DestinationResponseItem>> {
            override fun onResponse(
                call: Call<List<DestinationResponseItem>>,
                response: retrofit2.Response<List<DestinationResponseItem>>
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