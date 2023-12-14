package com.tipiz.movieapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import com.tipiz.movieapp.R
import com.tipiz.movieapp.adapter.MainAdapter
import com.tipiz.movieapp.data.response.movie.MovieResponse
import com.tipiz.movieapp.databinding.ActivityMainBinding
import com.tipiz.movieapp.activity.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private val mainViewModel by viewModels<MainViewModel>()
    private lateinit var binding: ActivityMainBinding


    companion object {
        const val MOVIE_POPULAR = 0
        const val MOVIE_NOW_PLAYING = 1
        var movieCategory = 0
        var currentPage = 1
        var totalPages = 0
        const val TAG = "MainActivity"
    }

    private var isScrolling = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        movieNextPage()

    }

    private fun setupListener() {
        var lastScrollY = 0

        binding.scrollview.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            val isScrollingDown = scrollY > lastScrollY
            lastScrollY = scrollY

            if (isScrollingDown && scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                if (!isScrolling) {
                    if (currentPage <= totalPages) {
                        mainViewModel.getMovieNextPage()
                    }
                }
            }
        })


    }

    private fun movie() {
        mainViewModel.getMovie()
        mainViewModel.movieList.observe(this) {
            binding.scrollview.scrollTo(0, 0)
            showMovie(it)

        }
        mainViewModel.isLoading.observe(this) {
            showLoading(it)

        }

    }

    private fun movieNextPage() {
        setupListener()
        mainViewModel.movieListNextPage.observe(this) {
            showMovieNextPage(it)
        }
        mainViewModel.isLoadingNextPage.observe(this) {
            showLoadingNextPage(it)
        }

    }


    override fun onStart() {
        super.onStart()
        movie()
        showLoadingNextPage(false)

    }

    private fun showMovie(response: MovieResponse) {
        val layoutManager = GridLayoutManager(this, 2)
        binding.rvMain.layoutManager = layoutManager
        binding.rvMain.setHasFixedSize(true)
        val adapter = MainAdapter(arrayListOf()) { movie ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_DETAIL, movie)
            startActivity(intent)
            showMessage(movie.title)
        }
        binding.rvMain.adapter = adapter
        totalPages = response.totalPages
        adapter.setData(response.results)
    }

    private fun showMovieNextPage(response: MovieResponse) {
        val layoutManager = GridLayoutManager(this, 2)
        binding.rvMain.layoutManager = layoutManager
        binding.rvMain.setHasFixedSize(true)
        val adapter = MainAdapter(arrayListOf()) { movie ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_DETAIL, movie)
            startActivity(intent)
            showMessage(movie.title)
        }
        binding.rvMain.adapter = adapter
        totalPages = response.totalPages
        adapter.setDataNextPage(response.results)
        showMessage("Page $currentPage")
    }

    private fun showLoading(loading: Boolean) {
        when (loading) {
            true -> binding.pgbar.visibility = View.VISIBLE
            false -> binding.pgbar.visibility = View.GONE
        }
    }

    private fun showLoadingNextPage(loading: Boolean) {
        when (loading) {
            true -> {
                isScrolling = true
                binding.progressBarNextPage.visibility = View.VISIBLE
            }

            false -> {
                isScrolling = false
                binding.progressBarNextPage.visibility = View.GONE
            }
        }
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_popular -> {
                showMessage("Movie Popular Selected")
                mainViewModel.setMovieCategory(MOVIE_POPULAR)
            }

            R.id.action_now_playing -> {
                movieCategory = MOVIE_NOW_PLAYING
                mainViewModel.setMovieCategory(MOVIE_NOW_PLAYING)
                showMessage("Movie Now Playing Selected")
            }


        }
        return super.onOptionsItemSelected(item)
    }

}