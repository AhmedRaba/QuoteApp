package com.training.quoteapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.training.quoteapp.ApiInterface
import com.training.quoteapp.R
import com.training.quoteapp.data.model.QuoteItem
import com.training.quoteapp.databinding.FragmentShowQuoteBinding
import com.training.quoteapp.viewmodel.QuoteViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID
import kotlin.random.Random

class ShowQuoteFragment : Fragment() {
    private lateinit var binding: FragmentShowQuoteBinding
    private lateinit var viewModel: QuoteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentShowQuoteBinding.inflate(inflater, container, false)



        showQuote()




        return binding.root
    }


    private fun showQuote() {


        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.api-ninjas.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiInterface::class.java)

        retrofit.getQuote().enqueue(object : Callback<List<QuoteItem>> {
            override fun onResponse(p0: Call<List<QuoteItem>>, p1: Response<List<QuoteItem>>) {
                if (p1.isSuccessful) {
                    binding.progressBar.isVisible = false
                    binding.quoteLayout.isVisible = true
                    val quote = p1.body()?.get(0)?.quote.toString()
                    val author = p1.body()?.get(0)?.author.toString()
                    val category = p1.body()?.get(0)?.category.toString()

                    addToFav(quote, author, category)

                    binding.tvQuote.text = quote
                    binding.tvAuthor.text = "-$author"
                } else {
                    Log.d("1234", p1.code().toString())
                }
            }

            override fun onFailure(p0: Call<List<QuoteItem>>, p1: Throwable) {
                Log.d("this", p1.message.toString())
            }

        })

    }

    private fun addToFav(quote: String, author: String, category: String) {
        viewModel = ViewModelProvider(requireActivity())[QuoteViewModel::class.java]
        var isToggled = false
        val uniqueID = Random.nextInt()
        binding.btnFav.setOnClickListener {
            val quoteItem = QuoteItem(uniqueID, author, category, quote)
            isToggled = !isToggled
            if (isToggled) {
                binding.btnFav.setImageResource(R.drawable.ic_fav_checked)

                viewModel.saveQuote(quoteItem)
            } else {
                binding.btnFav.setImageResource(R.drawable.ic_fav_unchecked)
                viewModel.deleteQuote(quoteItem)
            }
        }

    }

}