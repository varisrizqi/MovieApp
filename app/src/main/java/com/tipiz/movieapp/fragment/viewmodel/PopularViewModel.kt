package com.tipiz.movieapp.fragment.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tipiz.movieapp.data.response.movie.MovieResponse
import com.tipiz.movieapp.data.retrofit.ApiConfig
import com.tipiz.movieapp.fragment.PopularFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PopularViewModel : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
        const val APIKEY = "ed33277fb816ac9730e7cc493f1891d1"
    }

    private val api = ApiConfig.getApiService()

    private val _movieList = MutableLiveData<MovieResponse>()
    val movieList: LiveData<MovieResponse> = _movieList

    private val _movieListNextPage = MutableLiveData<MovieResponse>()
    val movieListNextPage: LiveData<MovieResponse> = _movieListNextPage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading = _isLoading

    private val _isLoadingNextPage = MutableLiveData<Boolean>()
    val isLoadingNextPage: LiveData<Boolean> = _isLoadingNextPage


    fun getMovie() {
        PopularFragment.currentPage = 1
        _isLoading.value = true

        val client = api.getMoviePopular(APIKEY, PopularFragment.currentPage)
        client.enqueue(object : Callback<MovieResponse> {
            override fun onResponse(
                call: Call<MovieResponse>,
                response: Response<MovieResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _movieList.value = response.body()
                    Log.d(TAG, "responseMovie: $response")
                } else {
                    Log.e(TAG, "Response Message: ${response.message()}")
                    Log.d(TAG, "Response Code: ${response.code()}")
                    Log.d(TAG, "Response Body: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                _isLoading.value = true
                Log.e(TAG, "koneksi gagal ${t.message}")
            }

        })
    }

    fun getMovieNextPage() {
        _isLoadingNextPage.value = true

        PopularFragment.currentPage += 1
        val client = api.getMoviePopular(APIKEY, PopularFragment.currentPage)
        client.enqueue(object : Callback<MovieResponse> {
            override fun onResponse(
                call: Call<MovieResponse>,
                response: Response<MovieResponse>
            ) {
                _isLoadingNextPage.value = false
                if (response.isSuccessful) {
                    _movieListNextPage.value = response.body()
                    Log.d(TAG, "responseMovie: $response")
                } else {
                    Log.e(TAG, "Response Message: ${response.message()}")
                    Log.d(TAG, "Response Code: ${response.code()}")
                    Log.d(TAG, "Response Body: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                _isLoadingNextPage.value = false
                Log.e(TAG, "koneksi gagal ${t.message}")
            }

        })
    }

}