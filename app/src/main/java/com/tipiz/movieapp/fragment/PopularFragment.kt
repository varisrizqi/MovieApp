package com.tipiz.movieapp.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.tipiz.movieapp.activity.DetailActivity
import com.tipiz.movieapp.adapter.MainAdapter
import com.tipiz.movieapp.data.response.movie.MovieResponse
import com.tipiz.movieapp.databinding.FragmentPopularBinding
import com.tipiz.movieapp.fragment.viewmodel.PopularViewModel


class PopularFragment : Fragment() {

    private var _binding: FragmentPopularBinding? = null
    private val binding get() = _binding
    private lateinit var viewModel: PopularViewModel

    companion object {
        const val TAG = "PopularFragment"
        var currentPage = 1
        var totalPages = 0

    }
    private var isScrolling = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPopularBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[PopularViewModel::class.java]
    }


    private fun setupListener() {
        var lastScrollY = 0

        binding?.scrollview?.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            val isScrollingDown = scrollY > lastScrollY
            lastScrollY = scrollY

            if (isScrollingDown && scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                if (!isScrolling) {
                    if (currentPage <= totalPages) {
                        viewModel.getMovieNextPage()
                          }
                }
            }
        })
    }

    private fun movie() {
        binding?.scrollview?.scrollTo(0, 0)
        viewModel.getMovie()
        viewModel.movieList.observe(viewLifecycleOwner){
            showMovie(it)
        }

        viewModel.isLoading.observe(viewLifecycleOwner){
            showLoading(it)
        }

    }

    private fun movieNextPage() {

        setupListener()
        viewModel.movieListNextPage.observe(viewLifecycleOwner) {
            showMovieNextPage(it)
        }
        viewModel.isLoadingNextPage.observe(viewLifecycleOwner) {
            showLoadingNextPage(it)
        }

    }

    override fun onStart() {
        super.onStart()
        movie()
        showLoadingNextPage(false)
        movieNextPage()
    }

    private fun showMovie(response: MovieResponse) {
        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding?.rvMainFragment?.layoutManager = layoutManager
        binding?.rvMainFragment?.setHasFixedSize(true)
        val adapter = MainAdapter(arrayListOf()) { movie ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_DETAIL, movie)
            startActivity(intent)
            showMessage(movie.title)
        }
        totalPages = response.totalPages
        binding?.rvMainFragment?.adapter = adapter
        adapter.setData(response.results)
    }

    private fun showMovieNextPage(response: MovieResponse) {
        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding?.rvMainFragment?.layoutManager = layoutManager
        binding?.rvMainFragment?.setHasFixedSize(true)
        val adapter = MainAdapter(arrayListOf()) { movie ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_DETAIL, movie)
            startActivity(intent)
            showMessage(movie.title)
        }
        totalPages = response.totalPages
        binding?.rvMainFragment?.adapter = adapter
        adapter.setDataNextPage(response.results)
        showMessage("Page $currentPage")
    }

    private fun showMessage(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(loading: Boolean) {
        when (loading) {
            true -> binding?.pgbarMainFragment?.visibility = View.VISIBLE
            false -> binding?.pgbarMainFragment?.visibility = View.GONE
        }
    }

    private fun showLoadingNextPage(loading: Boolean) {
        when (loading) {
            true -> {
                isScrolling = true
                binding?.progressBarNextPage?.visibility = View.VISIBLE
            }

            false -> {
                isScrolling = false
                binding?.progressBarNextPage?.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}