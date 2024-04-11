package com.training.quoteapp.fragments

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
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
import kotlin.random.Random

class ShowQuoteFragment : Fragment() {

    private lateinit var binding: FragmentShowQuoteBinding
    private lateinit var viewModel: QuoteViewModel

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.api-ninjas.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(ApiInterface::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentShowQuoteBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[QuoteViewModel::class.java]

        showQuote()

        binding.root.setOnClickListener {
            showQuote()
        }

        return binding.root
    }

    private fun showQuote() {
        retrofit.getQuote().enqueue(object : Callback<List<QuoteItem>> {
            override fun onResponse(p0: Call<List<QuoteItem>>, p1: Response<List<QuoteItem>>) {
                if (p1.isSuccessful) {
                    animateText()
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
        var isToggled = false
        binding.btnFav.setImageResource(R.drawable.ic_fav_unchecked)

        val uniqueID = Random.nextInt()


        binding.btnFav.setOnClickListener {
            isToggled = !isToggled
            if (isToggled) {
                binding.btnFav.setImageResource(R.drawable.ic_fav_checked)
                addQuoteToFavorites(uniqueID, quote, author, category)
            } else {
                binding.btnFav.setImageResource(R.drawable.ic_fav_unchecked)
                removeQuoteFromFavorites(uniqueID, quote, author, category)
            }
        }
    }

    private fun addQuoteToFavorites(
        uniqueID: Int,
        quote: String,
        author: String,
        category: String,
    ) {
        val quoteItem = QuoteItem(uniqueID, author, category, quote)
        viewModel.saveQuote(quoteItem)
        Toast.makeText(
            requireContext(),
            "Quote saved and added to favorites!",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun removeQuoteFromFavorites(
        uniqueID: Int,
        quote: String,
        author: String,
        category: String,
    ) {
        val quoteItem = QuoteItem(uniqueID, author, category, quote)
        viewModel.deleteQuote(quoteItem)
        Toast.makeText(requireContext(), "Quote removed from favorites!", Toast.LENGTH_LONG)
            .show()
    }

    private fun animateText() {
        binding.tvQuote.alpha = 0f
        binding.tvAuthor.alpha = 0f
        binding.btnFav.alpha = 0f
        ObjectAnimator.ofFloat(binding.tvQuote, "alpha", 0f, 1f).apply {
            duration = 1000
            interpolator = AccelerateInterpolator()
            start()
        }
        ObjectAnimator.ofFloat(binding.tvAuthor, "alpha", 0f, 1f).apply {
            duration = 1000
            interpolator = AccelerateInterpolator()
            start()
        }
        ObjectAnimator.ofFloat(binding.btnFav, "alpha", 0f, 1f).apply {
            duration = 1000
            interpolator = AccelerateInterpolator()
            start()
        }
    }
}