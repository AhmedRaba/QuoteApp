package com.training.quoteapp.fragments

import android.animation.ObjectAnimator
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
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
import java.io.IOException
import java.util.UUID
import kotlin.random.Random


class ShowQuoteFragment : Fragment() {

    private lateinit var binding: FragmentShowQuoteBinding
    private lateinit var viewModel: QuoteViewModel
    private val apiKey = "yd2kSBm3mQBhddwdasiEdQ==vknM5PFhUSKX7ugQ"

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

        binding.quoteLayout.setOnClickListener {
            showQuote()
        }

        binding.btnShare.setOnClickListener {
            shareQuote()
        }

        return binding.root
    }

    private fun showQuote() {
        val category = arguments?.getString("quoteCategory")
        retrofit.getCustomQuote(category.toString(), apiKey)
            .enqueue(object : Callback<List<QuoteItem>> {
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
                        Log.d(tag, p1.code().toString())
                    }
                }

                override fun onFailure(p0: Call<List<QuoteItem>>, p1: Throwable) {
                    Log.d(tag, p1.message.toString())
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
        Snackbar.make(binding.root, "Quote saved and added to favorites!", Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun removeQuoteFromFavorites(
        uniqueID: Int,
        quote: String,
        author: String,
        category: String,
    ) {
        val quoteItem = QuoteItem(uniqueID, author, category, quote)
        viewModel.deleteQuote(quoteItem)

        Snackbar.make(binding.root, "Quote removed from favorites!", Snackbar.LENGTH_SHORT).show()
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
        ObjectAnimator.ofFloat(binding.btnShare, "alpha", 0f, 1f).apply {
            duration = 1000
            interpolator = AccelerateInterpolator()
            start()
        }
    }

    private fun shareQuote() {
        val screenshot = screenShot(requireView())  // Use requireView() for fragment context

        share(screenshot)
    }


    private fun screenShot(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun share(bitmap: Bitmap) {
        val resolver = requireContext().contentResolver
        val fileName = UUID.randomUUID().toString() + ".png"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: return  // Handle potential failure

        try {
            resolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
            }
        } catch (e: IOException) {
            Log.d(tag, e.toString())
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_SUBJECT, "Quote App")
            putExtra(Intent.EXTRA_TEXT, "")
            putExtra(Intent.EXTRA_STREAM, uri) // Grant temporary access
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)  // Required for sharing
        }
        requireContext().startActivity(Intent.createChooser(shareIntent, "Share quote"))
    }
}