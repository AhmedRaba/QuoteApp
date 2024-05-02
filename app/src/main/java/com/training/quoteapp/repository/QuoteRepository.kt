package com.training.quoteapp.repository

import androidx.lifecycle.LiveData
import com.training.quoteapp.QuoteApi
import com.training.quoteapp.data.QuoteDao
import com.training.quoteapp.data.model.QuoteItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class QuoteRepository @Inject constructor(
    private val quoteDao: QuoteDao,
    private val quoteApi: QuoteApi,
) {

    val readAllData: LiveData<List<QuoteItem>> = quoteDao.readAllData()
    suspend fun saveQuote(quote: QuoteItem) {
        quoteDao.saveQuote(quote)
    }

    suspend fun deleteQuote(quote: QuoteItem) {
        quoteDao.deleteQuote(quote)
    }

    fun getCustomQuote(category: String, apiKey: String): Call<List<QuoteItem>> {
        return quoteApi.getCustomQuote(category, apiKey)
    }


}