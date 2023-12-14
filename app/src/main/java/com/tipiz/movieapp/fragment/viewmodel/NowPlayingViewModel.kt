package com.tipiz.movieapp.fragment.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tipiz.movieapp.data.response.movie.MovieResponse
import com.tipiz.movieapp.data.retrofit.ApiConfig
import com.tipiz.movieapp.fragment.NowPlayingFragment.Companion.currentPage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NowPlayingViewModel : ViewModel() {

    companion object {
        private const val TAG = "NowPlayingViewModel"
        const val APIKEY = "ed33277fb816ac9730e7cc493f1891d1"
    }

    private val api = ApiConfig.getApiService()

    private val _movieList = MutableLiveData<MovieResponse>()
    val movieList: LiveData<MovieResponse> = _movieList

    private val _movieListNextPage = MutableLiveData<MovieResponse>()
    val movieListNextPage: LiveData<MovieResponse> = _movieListNextPage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLoadingNextPage = MutableLiveData<Boolean>()
    val isLoadingNextPage: LiveData<Boolean> = _isLoadingNextPage

    fun getMovie() {
        currentPage = 1
        _isLoading.value = true

        api.getMovieNowPlaying(APIKEY, currentPage).enqueue(handleMovieResponse(_movieList))
    }

    fun getMovieNextPage() {
        _isLoadingNextPage.value = true

        currentPage += 1
        api.getMovieNowPlaying(APIKEY, currentPage).enqueue(handleMovieResponse(_movieListNextPage))
    }

    private fun handleMovieResponse(data: MutableLiveData<MovieResponse>): Callback<MovieResponse> {
        return object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                _isLoading.value = false
                _isLoadingNextPage.value = false

                if (response.isSuccessful) {
                    data.value = response.body()
                    Log.d(TAG, "responseMovie: $response")
                } else {
                    Log.e(TAG, "Response Message: ${response.message()}")
                    Log.d(TAG, "Response Code: ${response.code()}")
                    Log.d(TAG, "Response Body: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                _isLoading.value = true
                _isLoadingNextPage.value = false
                Log.e(TAG, "koneksi gagal ${t.message}")
            }
        }
    }
}
