package com.weather.forecast.ui.dashboard.fragments

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
import com.weather.forecast.Utils.showToast
import com.weather.forecast.databinding.FragmentFavBinding
import com.weather.forecast.entities.FavoriteCity
import com.weather.forecast.helper.ResultsWrapper
import com.weather.forecast.ui.dashboard.adapters.FavoriteCityAdapter
import com.weather.forecast.ui.dashboard.viewmodels.DashBoardViewModel
import com.weather.forecast.ui.dashboard.viewmodels.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CityFavoriteFragment : Fragment() {

    private var _binding: FragmentFavBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashBoardViewModel by hiltNavGraphViewModels(R.id.app_dashboard)

    private val locationViewModel by viewModels<LocationViewModel>()

    private val adapter: FavoriteCityAdapter by lazy {
        FavoriteCityAdapter()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null)
            _binding = FragmentFavBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeRecyclerview()

        viewModel.getAllFav().observe(viewLifecycleOwner, { updateUI(it) })

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun updateUI(it: ResultsWrapper<List<FavoriteCity>>) {
        when (it) {
            is ResultsWrapper.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
            }
            is ResultsWrapper.Error -> {
                binding.progressBar.visibility = View.GONE
                requireContext().showToast(it.error)
            }
            is ResultsWrapper.Success -> {
                adapter.submitList(it.response)
                binding.progressBar.visibility = View.GONE
            }
        }
    }


    private fun initializeRecyclerview() {

        binding.favoriteList.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.favoriteList.adapter = adapter


        adapter.setItemClickListener {
            locationViewModel.fetchPlaceDetails(it.placeID)
                .observe(viewLifecycleOwner, { updatePlaceData(it) })
        }
    }

    private fun updatePlaceData(it: ResultsWrapper<Place>) {
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