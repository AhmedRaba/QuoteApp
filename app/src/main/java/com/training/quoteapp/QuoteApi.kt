package com.training.quoteapp

import com.training.quoteapp.data.model.QuoteItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface QuoteApi {

    @GET("v1/quotes?category=happiness&X-API-Key=yd2kSBm3mQBhddwdasiEdQ==vknM5PFhUSKX7ugQ")
    fun getQuote(): Call<List<QuoteItem>>


    @GET("v1/quotes")
    fun getCustomQuote(
        @Query("category") category: String,
        @Header("X-API-Key") apiKey: String,
    ): Call<List<QuoteItem>>
}