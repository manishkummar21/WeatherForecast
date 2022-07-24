package com.weather.forecast.ui.dashboard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.weather.forecast.PreferenceManager
import com.weather.forecast.R
import com.weather.forecast.databinding.FragmentSettingsBinding
import com.weather.forecast.ui.dashboard.viewmodels.DashBoardViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SettingsFragment : Fragment() {


    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var preference: PreferenceManager

    private val viewModel: DashBoardViewModel by hiltNavGraphViewModels(R.id.app_dashboard)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null)
            _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (preference.temperatureUnit == "metric")
            binding.celsius.isChecked = true
        else
            binding.fahrenheit.isChecked = true

        binding.units.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.celsius -> {
                    preference.temperatureUnit = "metric"
                    viewModel.setTemperatureUnit("metric")
                }
                R.id.fahrenheit -> {
                    preference.temperatureUnit = "imperial"
                    viewModel.setTemperatureUnit("imperial")
                }
            }
        }


        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

}