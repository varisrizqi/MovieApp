package com.tipiz.movieapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tipiz.movieapp.data.response.trailer.ResultsItem
import com.tipiz.movieapp.databinding.ItemTrailerBinding

class TrailerAdapter(
    private var movies: ArrayList<ResultsItem>,
    private var listener: OnAdapterListener
) :
    RecyclerView.Adapter<TrailerAdapter.ListViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemTrailerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val newTrailer = movies[position]
        holder.bind(newTrailer)

        holder.itemView.setOnClickListener {
            listener.onClick(newTrailer.key)
        }
    }

    inner class ListViewHolder(private val binding: ItemTrailerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(trailer: ResultsItem) {
            binding.tvTrailer.text = trailer.name
        }
    }

    fun setData(newTrailer: List<ResultsItem>) {
        movies.clear()
        movies.addAll(newTrailer)
        notifyDataSetChanged()

        listener.onVideo(newTrailer[0].key)
    }

    interface OnAdapterListener{
        fun onClick(key:String)
        fun onVideo(key:String)
    }

}