package com.tipiz.movieapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tipiz.movieapp.R
import com.tipiz.movieapp.data.response.movie.ResultsItem
import com.tipiz.movieapp.databinding.ItemMainBinding


class MainAdapter(
    private val movies: ArrayList<ResultsItem>,
    private val onClick: (ResultsItem) -> Unit
) : RecyclerView.Adapter<MainAdapter.ListViewHolder>() {

    companion object {
        const val TAG = "MainAdapter"
        const val backdropPath = "https://www.themoviedb.org/t/p/w500_and_h282_face"
        const val posterPath = "https://www.themoviedb.org/t/p/w220_and_h330_face"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
    }

    inner class ListViewHolder(private val binding: ItemMainBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: ResultsItem) {
            binding.tvTitle.text = movie.title

            val linkPoster = posterPath + movie.posterPath
            Glide.with(itemView.context)
                .load(linkPoster)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_error))
                .into(binding.imgPoster)

            itemView.setOnClickListener {
                onClick(movie)
            }
        }
    }

    fun setData(newMovies: List<ResultsItem>) {
        movies.clear()
        movies.addAll(newMovies)
        notifyDataSetChanged()
    }

    fun setDataNextPage(newMovies: List<ResultsItem>) {
        movies.addAll(newMovies)
        notifyDataSetChanged()
    }
}
