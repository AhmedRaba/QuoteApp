package com.training.quoteapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "QUOTE_TABLE")
data class QuoteItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val quote: String,
    val author: String,
    val category: String,
)