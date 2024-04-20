package com.training.quoteapp.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.training.quoteapp.data.model.QuoteItem
import com.training.quoteapp.databinding.FavQuoteItemBinding

class QuoteAdapter : RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder>() {

    private var quoteList = listOf<QuoteItem>()

    inner class QuoteViewHolder(val binding: FavQuoteItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        val binding =
            FavQuoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuoteViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return quoteList.size
    }

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {

        val currentItem = quoteList[position]

        holder.binding.tvQuote.text = currentItem.quote
        holder.binding.tvAuthor.text = "-"+currentItem.author

    }


    fun setData(quote: List<QuoteItem>) {
        this.quoteList = quote
        notifyDataSetChanged()
    }


}