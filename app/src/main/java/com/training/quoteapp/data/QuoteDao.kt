package com.training.quoteapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.training.quoteapp.data.model.QuoteItem

@Dao
interface QuoteDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveQuote(quote: QuoteItem)


    @Delete
    suspend fun deleteQuote(quote: QuoteItem)


    @Query("SELECT * FROM QUOTE_TABLE ORDER BY id ASC")
    fun readAllData(): LiveData<List<QuoteItem>>

}