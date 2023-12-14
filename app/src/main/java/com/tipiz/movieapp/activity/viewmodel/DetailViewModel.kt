package com.tipiz.movieapp.activity.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tipiz.movieapp.data.response.detail.DetailResponse
import com.tipiz.movieapp.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetailViewModel : ViewModel() {

    companion object {
        private const val TAG = "DetailViewModel"
    }

    private val _detailMovie = MutableLiveData<DetailResponse>()
    val detailMove: LiveData<DetailResponse> = _detailMovie

    fun getDetailMovie(id: Int) {
        val client = ApiConfig.getApiService().getMovieDetail(id, MainViewModel.APIKEY)
        client.enqueue(object : Callback<DetailResponse> {
            override fun onResponse(
                call: Call<DetailResponse>,
                response: Response<DetailResponse>
            ) {
                if (response.isSuccessful) {
                    _detailMovie.value = response.body()
                    Log.e(TAG, "DetailMovie: ${response.body()?.overview}")
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                Log.e(TAG, "koneksi gagal ${t.message}")
            }
        })
    }


}