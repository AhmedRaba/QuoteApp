package com.training.quoteapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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


        categoryList()
        binding.btnGetQuote.setOnClickListener {

            val bundle = Bundle()
            bundle.putString("quoteCategory", binding.etQuoteCategory.text.toString())

            if (binding.etQuoteCategory.text.isEmpty()) {
                binding.tiQuoteCategory.error = "Please select category"
            } else {
                binding.tiQuoteCategory.isErrorEnabled=false
                findNavController().navigate(
                    R.id.action_chooseQuoteFragment_to_showQuoteFragment,
                    bundle
                )
            }

        }



        return binding.root
    }


    private fun categoryList() {

        val quoteCategories = listOf(
            "age",
            "alone",
            "amazing",
            "anger",
            "architecture",
            "art",
            "attitude",
            "beauty",
            "best",
            "birthday",
            "business",
            "car",
            "change",
            "communication",
            "computers",
            "cool",
            "courage",
            "dad",
            "dating",
            "death",
            "design",
            "dreams",
            "education",
            "environmental",
            "equality",
            "experience",
            "failure",
            "faith",
            "family",
            "famous",
            "fear",
            "fitness",
            "food",
            "forgiveness",
            "freedom",
            "friendship",
            "funny",
            "future",
            "god",
            "good",
            "government",
            "graduation",
            "great",
            "happiness",
            "health",
            "history",
            "home",
            "hope",
            "humor",
            "imagination",
            "inspirational",
            "intelligence",
            "jealousy",
            "knowledge",
            "leadership",
            "learning",
            "legal",
            "life",
            "love",
            "marriage",
            "medical",
            "men",
            "mom",
            "money",
            "morning",
            "movies",
            "success"
        )

        val quoteAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                quoteCategories
            )

        binding.etQuoteCategory.setAdapter(quoteAdapter)
    }


}