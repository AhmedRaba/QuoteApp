package com.training.quoteapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.training.quoteapp.R
import com.training.quoteapp.databinding.FragmentChooseQuoteBinding

class ChooseQuoteFragment : Fragment() {

    private lateinit var binding: FragmentChooseQuoteBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentChooseQuoteBinding.inflate(inflater, container, false)


        binding.btnGetQuote.setOnClickListener {
            findNavController().navigate(R.id.action_chooseQuoteFragment_to_showQuoteFragment)
        }



        return binding.root
    }

}