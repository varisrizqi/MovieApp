package com.tipiz.movieapp.data.response.trailer

import com.google.gson.annotations.SerializedName

data class TrailerResponse(

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("results")
	val results: List<ResultsItem>
)