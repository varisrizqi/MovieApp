package com.tipiz.movieapp.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tipiz.movieapp.R
import com.tipiz.movieapp.adapter.MainAdapter
import com.tipiz.movieapp.data.response.detail.DetailResponse
import com.tipiz.movieapp.data.response.detail.GenresItem
import com.tipiz.movieapp.data.response.movie.ResultsItem
import com.tipiz.movieapp.databinding.ActivityDetailBinding
import com.tipiz.movieapp.activity.viewmodel.DetailViewModel
import com.tipiz.movieapp.databinding.ContentDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var bindingContent: ContentDetailBinding
    private val mainViewModel by viewModels<DetailViewModel>()

    companion object {
        const val TAG = "DetailActivity"
        const val EXTRA_DETAIL = "extra_parcelable"
        const val EXTRA_TRAILER = "extra_parcelable_trailer"
    }

    private var parcelable: ResultsItem? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        bindingContent = binding.contentDetail
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        getParcelable()

        bindingContent.fabTrailer.setOnClickListener {
            val intent = Intent(this, TrailerActivity::class.java)
            intent.putExtra(EXTRA_TRAILER, parcelable!!.id)
            startActivity(intent)
        }

        mainViewModel.detailMove.observe(this) {
            showDetailMovie(it)
        }

    }

    override fun onStart() {
        super.onStart()
        mainViewModel.getDetailMovie(parcelable!!.id)
    }


    private fun getParcelable() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.hide()
        parcelable = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_DETAIL, ResultsItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_DETAIL)
        }
    }

    private fun showDetailMovie(detailMovie: DetailResponse) {

        val linkBackgroundPoster = MainAdapter.backdropPath + parcelable?.backdropPath
        Log.d(TAG, "backdrop_path $linkBackgroundPoster")

        Glide.with(this)
            .load(linkBackgroundPoster)
            .apply(
                RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_error)
            )
            .fitCenter()
            .into(binding.imagePoster)

        supportActionBar?.title = detailMovie.title

        bindingContent.tvTitleDetail.text = detailMovie.title
        bindingContent.tvVote.text = detailMovie.voteAverage.toString()
        bindingContent.tvOverview.text = detailMovie.overview

        //dari chat gpt memanggil semua data, mirip for
        val genresList: List<GenresItem> = detailMovie.genres
        val genresText = genresList.joinToString { it.name }
        bindingContent.tvGenre.text = genresText.ifEmpty { "No genres available" }
    }


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }


}