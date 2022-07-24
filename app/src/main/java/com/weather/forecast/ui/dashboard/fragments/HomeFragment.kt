package com.weather.forecast.ui.dashboard.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.weather.forecast.PreferenceManager
import com.weather.forecast.R
import com.weather.forecast.Utils.showToast
import com.weather.forecast.Utils.toMilliSecond
import com.weather.forecast.base.ComponentsAdapter
import com.weather.forecast.data.LocationModel
import com.weather.forecast.data.base.Components
import com.weather.forecast.data.base.CurrentLocationTemperatureInfo
import com.weather.forecast.data.base.DailyForeCastInfo
import com.weather.forecast.data.base.HourlyForeCastInfo
import com.weather.forecast.data.location.model.LocationDetails
import com.weather.forecast.data.weather.model.WeatherForeCastList
import com.weather.forecast.databinding.FragmentHomeBinding
import com.weather.forecast.helper.LocationListenerLiveData
import com.weather.forecast.helper.ResultsWrapper
import com.weather.forecast.ui.IComponentClickListener
import com.weather.forecast.ui.dashboard.viewmodels.DashBoardViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(), IComponentClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val adapter: ComponentsAdapter by lazy {
        ComponentsAdapter(requireContext(), mutableListOf(), this)
    }

    private val viewModel: DashBoardViewModel by hiltNavGraphViewModels(R.id.app_dashboard)

    @Inject
    lateinit var preference: PreferenceManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        if (_binding == null) {
            _binding = FragmentHomeBinding.inflate(inflater, container, false)

            viewModel.locationData.observe(viewLifecycleOwner, {
                it.getContentIfNotHandled()?.let {
                    preference.lastKnowCurrentLocation = it
                    viewModel.fetchWeatherForeCastList(it, preference.temperatureUnit)
                }
            })
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeRecyclerView()
        initObservable()

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.favorite -> {
                    findNavController().navigate(R.id.action_home_to_favorite)
                    true
                }
                R.id.settings -> {
                    findNavController().navigate(R.id.action_home_to_settings)
                    true
                }
                else -> false
            }
        }

    }

    private fun initObservable() {

        viewModel.weatherLocationData.observe(viewLifecycleOwner, {
            when (it) {
                is ResultsWrapper.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is ResultsWrapper.Error -> {
                    binding.progressBar.visibility = View.GONE
                    requireContext().showToast(it.error)
                }
                is ResultsWrapper.Success -> {
                    if (it.response.weatherForeCastList != null && it.response.locationDetails != null)
                        setComponent(it.response.weatherForeCastList, it.response.locationDetails)
                    binding.progressBar.visibility = View.GONE
                }
            }
        })

        viewModel.searchLocationSelectedObserver.observe(viewLifecycleOwner, {
            it?.let {
                viewModel.fetchWeatherForeCastList(
                    LocationModel(it.longitude, it.latitude),
                    preference.temperatureUnit
                )
                viewModel.clear()
            }

        })

        viewModel.temperateUnitChange.observe(viewLifecycleOwner, {
            it?.let {
                preference.lastFetchedLocation?.let {
                    viewModel.fetchWeatherForeCastList(
                        LocationModel(it.longitude, it.latitude),
                        preference.temperatureUnit
                    )
                }
                viewModel.clear()
            }
        })

    }


    private fun initializeRecyclerView() {
        binding.componentList.setHasFixedSize(true)
        binding.componentList.layoutManager = LinearLayoutManager(requireContext())
    }


    private fun setComponent(response: WeatherForeCastList, locationDetails: LocationDetails) {

        preference.lastFetchedLocation = LocationModel(response.lon, response.lat)

        val components = mutableListOf<Components>()
        components.add(
            CurrentLocationTemperatureInfo(
                response.current,
                locationDetails,
                preference.temperatureUnit
            )
        )
        components.add(HourlyForeCastInfo(response.hourly.filter { DateUtils.isToday(it.dt.toMilliSecond()) }))
        components.add(DailyForeCastInfo(response.daily))

        adapter.setComponents(components)
        binding.componentList.adapter = adapter.getAdapter()

        adapter.notifyAdapter()

    }

    override fun onLocationClick() {
        findNavController().navigate(R.id.action_home_to_search)
    }
}