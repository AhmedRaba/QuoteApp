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
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.training.quoteapp.R
import com.training.quoteapp.data.model.QuoteItem
import com.training.quoteapp.databinding.FragmentShowQuoteBinding
import com.training.quoteapp.viewmodel.QuoteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.UUID
import kotlin.random.Random


@AndroidEntryPoint
class ShowQuoteFragment : Fragment() {

    private lateinit var binding: FragmentShowQuoteBinding
    private lateinit var viewModel: QuoteViewModel
    private val apiKey = "yd2kSBm3mQBhddwdasiEdQ==vknM5PFhUSKX7ugQ"

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


        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.fetchCustomQuotes(category.toString(), apiKey)
                .enqueue(object : Callback<List<QuoteItem>> {
                    override fun onResponse(
                        p0: Call<List<QuoteItem>>,
                        p1: Response<List<QuoteItem>>,
                    ) {
                        if (p1.isSuccessful) {
                            animateText()
                            binding.progressBar.isVisible = false
                            binding.quoteLayout.isVisible = true
                            val quote = p1.body()?.get(0)?.quote.toString()
                            val author = p1.body()?.get(0)?.author.toString()

                            saveQuote(quote, author, category.toString())

                            binding.tvQuote.text = quote
                            binding.tvAuthor.text = author
                        } else {
                            Log.d(tag, p1.code().toString())
                        }
                    }


                    override fun onFailure(p0: Call<List<QuoteItem>>, p1: Throwable) {
                        Log.d(tag, p1.message.toString())
                    }
                })
        }

    }

    private fun saveQuote(quote: String, author: String, category: String) {
        var isToggled = false
        binding.btnFav.setImageResource(R.drawable.ic_fav_unchecked)

        val uniqueID = Random.nextInt()

        val quoteItem = QuoteItem(uniqueID, quote, author, category)

        binding.btnFav.setOnClickListener {
            isToggled = !isToggled
            if (isToggled) {
                binding.btnFav.setImageResource(R.drawable.ic_fav_checked)
                addQuoteToFavorites(quoteItem)
            } else {
                binding.btnFav.setImageResource(R.drawable.ic_fav_unchecked)
                removeQuoteFromFavorites(quoteItem)
            }
        }
    }

    private fun addQuoteToFavorites(
        quoteItem: QuoteItem,
    ) {
        viewModel.saveQuote(quoteItem)
        Snackbar.make(binding.root, "Quote saved and added to favorites!", Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun removeQuoteFromFavorites(
        quoteItem: QuoteItem,
    ) {
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
        val screenshot = screenShot(requireView())

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
            ?: return

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
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        requireContext().startActivity(Intent.createChooser(shareIntent, "Share quote"))
    }
}