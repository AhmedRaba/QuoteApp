package com.training.quoteapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.training.quoteapp.QuoteApi
import com.training.quoteapp.data.QuoteDao
import com.training.quoteapp.data.model.QuoteItem
import com.training.quoteapp.repository.QuoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class QuoteViewModel @Inject constructor(
    private val application: Application,private val repository: QuoteRepository
) : AndroidViewModel(application) {

    val readAllData: LiveData<List<QuoteItem>> = repository.readAllData

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

    suspend fun fetchCustomQuotes(category: String, apiKey: String): Call<List<QuoteItem>> {
        return repository.getCustomQuote(category, apiKey)
    }

}
