package com.tipiz.movieapp.activity.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tipiz.movieapp.data.response.trailer.TrailerResponse
import com.tipiz.movieapp.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrailerViewModel : ViewModel(){
    companion object{
        const val TAG = "TrailerViewModel"
    }

    private val _trailerMovie = MutableLiveData<TrailerResponse>()
    var trailerMovie: LiveData<TrailerResponse> = _trailerMovie

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading = _isLoading

    fun getTrailerMovie(id: Int){
        _isLoading.value = true
        val client = ApiConfig.getApiService().getMovieTrailer(id,MainViewModel.APIKEY)
        client.enqueue(object: Callback<TrailerResponse> {
            override fun onResponse(
                call: Call<TrailerResponse>,
                response: Response<TrailerResponse>
            ) {
                isLoading.value = false
                if (response.isSuccessful){
                    _trailerMovie.value = response.body()
                    Log.d(TAG, "Trailer Success Body ${response.body()}")
                    Log.d(TAG, "Trailer KEY ${response.body()?.results}")
                    for (res in response.body()!!.results){
                        Log.d(TAG, "Trailer Success name: ${res.name}")

                    }
                }
            }

            override fun onFailure(call: Call<TrailerResponse>, t: Throwable) {
                isLoading.value = true
                Log.e(TAG, "Trailer Fail: ${t.message}")
            }

        })
    }
}