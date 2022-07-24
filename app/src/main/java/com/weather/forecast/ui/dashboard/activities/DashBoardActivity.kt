package com.weather.forecast.ui.dashboard.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.weather.forecast.databinding.ActivityMainBinding
import com.weather.forecast.ui.dashboard.viewmodels.DashBoardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<DashBoardViewModel>()

    private val requestLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            if (it.containsValue(false)) {
                finish()
                Toast.makeText(
                    this,
                    "Please Enable the Location Permission to use the Application",
                    Toast.LENGTH_LONG
                ).show()
            } else
                getLocation()

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkLocationPermission()
    }


    private fun checkLocationPermission() {
        if (!isPermissionGranted())
            requestPermission()
    }

    private fun getLocation() {
        viewModel.fetchCurrentLocation()
    }

    private fun isPermissionGranted(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        requestLocationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }
}