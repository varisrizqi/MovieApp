package com.tipiz.movieapp.activity

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.tipiz.movieapp.R
import com.tipiz.movieapp.adapter.TrailerAdapter
import com.tipiz.movieapp.data.response.trailer.TrailerResponse
import com.tipiz.movieapp.databinding.ActivityTrailerBinding
import com.tipiz.movieapp.activity.viewmodel.TrailerViewModel

class TrailerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrailerBinding
    private val mainViewModel by viewModels<TrailerViewModel>()

    private lateinit var youTubePlayer: YouTubePlayer

    private var cueKey: String? = null
    private var getTrailer: Int? = null


     //untuk fitur fullscreen
    private var isFullscreen = false
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (isFullscreen) {
                // if the player is in fullscreen, exit fullscreen
                youTubePlayer.toggleFullscreen()
            } else {
                finish()
            }
        }
    }

    companion object {
        const val TAG = "TrailerActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrailerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getTrailer = intent.getIntExtra(DetailActivity.EXTRA_TRAILER, 0)

        youTube()
        Log.d(TAG,"Apakah cueKey null? : $cueKey")
        mainViewModel.trailerMovie.observe(this) { trailerResponse ->
            showTrailer(trailerResponse)

        }

        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

    }

    private fun showTrailer(trailer: TrailerResponse) {
        val layoutManager = LinearLayoutManager(this)
        binding.rvTrailer.layoutManager = layoutManager
        binding.rvTrailer.setHasFixedSize(true)

        val trailerAdapter =
            TrailerAdapter(arrayListOf(), object : TrailerAdapter.OnAdapterListener {
                override fun onClick(key: String) {
                    youTubePlayer.cueVideo(key, 0f)
                }

                override fun onVideo(key: String) {
                    cueKey = key
                    Log.d(TAG,"Apakah cueKey masih null? : $cueKey")
                }

            })

        binding.rvTrailer.adapter = trailerAdapter
        trailerAdapter.setData(trailer.results)
    }


    private fun youTube() {


         onBackPressedDispatcher.addCallback(onBackPressedCallback)

         val youTubePlayerView = findViewById<YouTubePlayerView>(R.id.youtube_player_view)
         val fullscreenViewContainer = findViewById<FrameLayout>(R.id.full_screen_view_container)

         val iFramePlayerOptions = IFramePlayerOptions.Builder()
             .controls(1)
             .fullscreen(1) // enable full screen button
             .autoplay(0)
             .build()

         // we need to initialize manually in order to pass IFramePlayerOptions to the player
         youTubePlayerView.enableAutomaticInitialization = false

         youTubePlayerView.addFullscreenListener(object : FullscreenListener {
             override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
                 isFullscreen = true

                 // the video will continue playing in fullscreenView
                 youTubePlayerView.visibility = View.GONE
                 fullscreenViewContainer.visibility = View.VISIBLE
                 fullscreenViewContainer.addView(fullscreenView)

                 // optionally request landscape orientation
                 requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
             }

             override fun onExitFullscreen() {
                 isFullscreen = false

                 // the video will continue playing in the player
                 youTubePlayerView.visibility = View.VISIBLE
                 fullscreenViewContainer.visibility = View.GONE
                 fullscreenViewContainer.removeAllViews()
             }
         })
         youTubePlayerView.initialize(object : AbstractYouTubePlayerListener() {
             override fun onReady(youTubePlayer: YouTubePlayer) {
                 this@TrailerActivity.youTubePlayer = youTubePlayer
                 cueKey?.let {
                     this@TrailerActivity.youTubePlayer.cueVideo(it, 0f)
                     Log.d(TAG,"Apakah cueKey masih, tetap null? : $cueKey")
                 }
             }
         }, iFramePlayerOptions)

         lifecycle.addObserver(youTubePlayerView)

     }


    override fun onStart() {
        super.onStart()
        mainViewModel.getTrailerMovie(getTrailer!!)
    }


    private fun showLoading(loading: Boolean) {
        when (loading) {
            true -> binding.progressBar.visibility = View.VISIBLE
            false -> binding.progressBar.visibility = View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

}