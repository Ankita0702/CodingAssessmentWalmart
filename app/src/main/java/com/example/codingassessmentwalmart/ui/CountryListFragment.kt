package com.example.codingassessmentwalmart.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.codingassessmentwalmart.R
import com.example.codingassessmentwalmart.adapter.CountryAdapter
import com.example.codingassessmentwalmart.databinding.FragmentCountryListBinding
import com.example.codingassessmentwalmart.viewModel.CountryViewModel

class CountryListFragment : Fragment(R.layout.fragment_country_list) {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentCountryListBinding.bind(requireView())
    }

    private lateinit var viewModel: CountryViewModel
    private val adapter = CountryAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CountryListFragment.adapter
        }

        viewModel = ViewModelProvider(this)[CountryViewModel::class.java]
        binding.buttonRetry.setOnClickListener { viewModel.loadCountries(force = true) }

        viewModel.countries.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.isVisible = loading
            binding.recyclerView.isVisible = !loading
            if (loading) {
                binding.textError.isVisible = false
                binding.buttonRetry.isVisible = false
            }
        }
        viewModel.error.observe(viewLifecycleOwner) { msg ->
            val show = !msg.isNullOrBlank()
            binding.textError.isVisible = show
            binding.buttonRetry.isVisible = show
            binding.textError.text = msg ?: ""
        }

        viewModel.loadCountries()
    }
}
