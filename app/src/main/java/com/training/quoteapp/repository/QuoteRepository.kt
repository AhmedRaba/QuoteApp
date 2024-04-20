package com.training.quoteapp.repository

import androidx.lifecycle.LiveData
import com.training.quoteapp.data.QuoteDao
import com.training.quoteapp.data.model.QuoteItem

class QuoteRepository(private val quoteDao: QuoteDao) {

    val readAllData: LiveData<List<QuoteItem>> = quoteDao.readAllData()
    suspend fun saveQuote(quote: QuoteItem) {
        quoteDao.saveQuote(quote)
    }

    suspend fun deleteQuote(quote: QuoteItem) {
        quoteDao.deleteQuote(quote)
    }

}