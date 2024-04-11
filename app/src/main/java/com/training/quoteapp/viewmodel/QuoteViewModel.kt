package com.training.quoteapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.training.quoteapp.data.QuoteDatabase
import com.training.quoteapp.data.model.QuoteItem
import com.training.quoteapp.repository.QuoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuoteRepository


    init {
        val quoteDao = QuoteDatabase.getDatabase(application).quoteDao()
        repository = QuoteRepository(quoteDao)
    }


    fun saveQuote(quote: QuoteItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveQuote(quote)
        }

    }

    fun deleteQuote(quote: QuoteItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteQuote(quote)
        }

    }


}