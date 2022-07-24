package com.weather.forecast.ui.dashboard.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.places.api.model.Place
import com.weather.forecast.R
import com.weather.forecast.Utils.getTextChangeStateFlow
import com.weather.forecast.Utils.showToast
import com.weather.forecast.databinding.FragmentSearchBinding
import com.weather.forecast.entities.FavoriteCity
import com.weather.forecast.helper.ResultsWrapper
import com.weather.forecast.ui.dashboard.adapters.LocationSearchAdapter
import com.weather.forecast.ui.dashboard.viewmodels.DashBoardViewModel
import com.weather.forecast.ui.dashboard.viewmodels.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashBoardViewModel by hiltNavGraphViewModels(R.id.app_dashboard)

    private val locationViewModel by viewModels<LocationViewModel>()

    private val favCities = mutableListOf<String>()

    private val adapter: LocationSearchAdapter by lazy {
        LocationSearchAdapter(favCities)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null)
            _binding = FragmentSearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeRecyclerview()

        locationViewModel.initializeSearchObservable(binding.search.getTextChangeStateFlow())
            .observe(viewLifecycleOwner, {
                when (it) {
                    is ResultsWrapper.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is ResultsWrapper.Error -> {
                        binding.progressBar.visibility = View.GONE
                        requireContext().showToast(it.error)
                    }
                    is ResultsWrapper.Success -> {
                        binding.progressBar.visibility = View.GONE
                        adapter.submitList(it.response)
                    }
                }
            })


        viewModel.favObserver.observe(viewLifecycleOwner, {
            favCities.clear()
            favCities.addAll(it)
            adapter.notifyDataSetChanged()
        })

        binding.backArrow.setOnClickListener {
            findNavController().navigateUp()
        }

    }


    private fun initializeRecyclerview() {

        binding.searchResult.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.searchResult.adapter = adapter


        adapter.setItemClickListener { it ->
            locationViewModel.fetchPlaceDetails(it.placeId)
                .observe(viewLifecycleOwner, { updateUI(it) })
        }

        adapter.setFavItemClickListener {
            viewModel.insertOrDeleteFavCity(
                FavoriteCity(
                    it.placeId,
                    it.getPrimaryText(null).toString(),
                    it.getSecondaryText(null).toString()
                )
            )
        }
    }

    private fun updateUI(it: ResultsWrapper<Place>) {
        when (it) {
            is ResultsWrapper.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
            }
            is ResultsWrapper.Error -> {
                binding.progressBar.visibility = View.GONE
                requireContext().showToast(it.error)
            }
            is ResultsWrapper.Success -> {
                binding.progressBar.visibility = View.GONE
                viewModel.setSelectedLocation(it.response)
                findNavController().navigateUp()
            }
        }
    }
}