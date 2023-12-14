package com.tipiz.movieapp.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.tipiz.movieapp.activity.DetailActivity
import com.tipiz.movieapp.adapter.MainAdapter
import com.tipiz.movieapp.data.response.movie.MovieResponse
import com.tipiz.movieapp.databinding.FragmentNowPlayingBinding
import com.tipiz.movieapp.fragment.viewmodel.NowPlayingViewModel

class NowPlayingFragment : Fragment() {

    private lateinit var binding: FragmentNowPlayingBinding
    private lateinit var viewModel: NowPlayingViewModel

    companion object {
        const val TAG = "NowPlayingFragment"
        var currentPage = 1
        var totalPages = 0
    }

    private var isScrolling = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNowPlayingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[NowPlayingViewModel::class.java]

        setupListener()
        movieNextPage()
    }

    private fun setupListener() {
        var lastScrollY = 0

        binding.scrollview.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            val isScrollingDown = scrollY > lastScrollY
            lastScrollY = scrollY

            if (isScrollingDown && scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                if (!isScrolling && currentPage <= totalPages) {
                    viewModel.getMovieNextPage()
                }
            }
        })
    }

    private fun movie() {
        binding.scrollview.scrollTo(0, 0)
        viewModel.getMovie()
        observeMovie(viewModel.movieList, ::showMovie)
        observeLoading(viewModel.isLoading, ::showLoading)
    }

    private fun movieNextPage() {
        observeMovie(viewModel.movieListNextPage, ::showMovieNextPage)
        observeLoading(viewModel.isLoadingNextPage, ::showLoadingNextPage)
    }

    private fun observeMovie(data: LiveData<MovieResponse>, action: (MovieResponse) -> Unit) {
        data.observe(viewLifecycleOwner, action)
    }

    private fun observeLoading(data: LiveData<Boolean>, action: (Boolean) -> Unit) {
        data.observe(viewLifecycleOwner, action)
    }

    private fun showMovie(response: MovieResponse) {
        setupRecyclerView(response)
    }

    private fun showMovieNextPage(response: MovieResponse) {
        setupRecyclerView(response)
        showMessage("Page $currentPage")
    }

    private fun setupRecyclerView(results: MovieResponse) {
        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvMainFragment.layoutManager = layoutManager
        binding.rvMainFragment.setHasFixedSize(true)

        val adapter = MainAdapter(arrayListOf()) { movie ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_DETAIL, movie)
            startActivity(intent)
            showMessage(movie.title)
        }

        totalPages = results.totalPages
        binding.rvMainFragment.adapter = adapter
        adapter.setData(results.results)
    }

    private fun showMessage(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun showLoadingNextPage(loading: Boolean) {
        isScrolling = loading
        binding.progressBarNextPage.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun showLoading(loading: Boolean) {
        isScrolling = loading
        binding.pgbarMainFragment.visibility = if (loading) View.VISIBLE else View.GONE
    }

    override fun onStart() {
        super.onStart()
        movie()
    }

}
