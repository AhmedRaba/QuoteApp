package com.training.quoteapp.data


import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.training.quoteapp.R
import com.training.quoteapp.data.model.QuoteItem
import com.training.quoteapp.databinding.FavQuoteItemBinding
import com.training.quoteapp.viewmodel.QuoteViewModel
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuoteAdapter(private val context: Context, private val viewModel: QuoteViewModel) :
    RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder>() {
    private var quoteList = listOf<QuoteItem>()

    inner class QuoteViewHolder(val binding: FavQuoteItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        val binding =
            FavQuoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuoteViewHolder(binding)
    }

    override fun getItemCount(): Int = quoteList.size

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        val currentItem = quoteList[position]
        val binding = holder.binding

        binding.tvQuote.text = currentItem.quote
        binding.tvAuthor.text = currentItem.author

        saveQuote(binding, currentItem)


        binding.btnShare.setOnClickListener {
            shareQuote(binding.root)
        }

    }

    fun setData(quotes: List<QuoteItem>) {
        quoteList = quotes
        notifyDataSetChanged()
    }

    private fun saveQuote(binding: FavQuoteItemBinding, quoteItem: QuoteItem) {
        var isToggled = false
        binding.btnFav.setImageResource(R.drawable.ic_fav_unchecked)

        binding.btnFav.setOnClickListener {
            isToggled = !isToggled
            if (isToggled) {

                binding.btnFav.setImageResource(R.drawable.ic_fav_unchecked)
                removeQuoteFromFavorites(binding, quoteItem)
            } else {

                binding.btnFav.setImageResource(R.drawable.ic_fav_checked)
                addQuoteToFavorites(binding, quoteItem)
            }
        }
    }

    private fun addQuoteToFavorites(binding: FavQuoteItemBinding, quoteItem: QuoteItem) {
        viewModel.saveQuote(quoteItem)
        Snackbar.make(binding.root, "Quote saved and added to favorites!", Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun removeQuoteFromFavorites(binding: FavQuoteItemBinding, quoteItem: QuoteItem) {

        val recentlyDeleted = mutableListOf<QuoteItem>()

        recentlyDeleted.add(quoteItem)

        val position = quoteList.indexOf(quoteItem)

        viewModel.deleteQuote(quoteItem)
        notifyItemRemoved(position)
        Snackbar.make(binding.root, "Quote removed from favorites!", Snackbar.LENGTH_SHORT)
            .setAction("Undo") {
                viewModel.saveQuote(recentlyDeleted.removeLast())
                notifyDataSetChanged()
            }.setActionTextColor(ContextCompat.getColor(context, R.color.black))
            .show()
    }

    private fun shareQuote(view: View) {
        val screenshot = screenShot(view)
        share(screenshot)
    }


    private fun screenShot(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun share(bitmap: Bitmap) {
        val resolver = context.contentResolver
        val fileName =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()) + ".png"
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
            Log.d("QuoteAdapter", e.toString())
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_SUBJECT, "Quote App")
            putExtra(Intent.EXTRA_TEXT, "")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share quote"))
    }

}