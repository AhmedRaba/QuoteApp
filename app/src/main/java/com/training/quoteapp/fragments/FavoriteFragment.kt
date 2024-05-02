package com.training.quoteapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.PagerSnapHelper
import com.training.quoteapp.data.QuoteAdapter
import com.training.quoteapp.databinding.FragmentFavoriteBinding
import com.training.quoteapp.viewmodel.QuoteViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var viewModel: QuoteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[QuoteViewModel::class.java]

        val adapter = QuoteAdapter(requireContext(),viewModel)

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerView)
        binding.recyclerView.adapter = adapter

        viewModel.readAllData.observe(viewLifecycleOwner) {
            adapter.setData(it)
        }




        return binding.root
    }

}