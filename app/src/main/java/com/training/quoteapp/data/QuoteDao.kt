package com.training.quoteapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.training.quoteapp.data.model.QuoteItem

@Dao
interface QuoteDao {


    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun saveQuote(quote: QuoteItem)

    @Delete
    suspend fun deleteQuote(quote: QuoteItem)




}