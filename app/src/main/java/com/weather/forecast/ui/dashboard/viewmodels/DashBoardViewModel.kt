package com.weather.forecast.ui.dashboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.weather.forecast.data.LocationModel
import com.weather.forecast.data.location.repository.LocationRepo
import com.weather.forecast.data.weather.model.WeatherLocationData
import com.weather.forecast.data.weather.repository.WeatherRepository
import com.weather.forecast.entities.FavoriteCity
import com.weather.forecast.helper.LocationListenerLiveData
import com.weather.forecast.helper.ResultsWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashBoardViewModel @Inject constructor(
    private val locationListenerLiveData: LocationListenerLiveData,
    private val weatherRepository: WeatherRepository, private val locationRepo: LocationRepo
) : ViewModel() {

    val locationData: LocationListenerLiveData
        get() = locationListenerLiveData

    private val _weatherLocationData: MutableLiveData<ResultsWrapper<WeatherLocationData>> =
        MutableLiveData()
    val weatherLocationData: LiveData<ResultsWrapper<WeatherLocationData>>
        get() = _weatherLocationData

    private val _searchLocationSelectedObserver: MutableLiveData<LatLng?> =
        MutableLiveData()
    val searchLocationSelectedObserver: LiveData<LatLng?>
        get() = _searchLocationSelectedObserver

    private val _temperateUnitChange: MutableLiveData<String?> = MutableLiveData()
    val temperateUnitChange: LiveData<String?>
        get() = _temperateUnitChange

    private val _currentLocation: MutableLiveData<LocationModel> = MutableLiveData()
    val currentLocation: LiveData<LocationModel>
        get() = _currentLocation

    val favObserver by lazy {
        weatherRepository.getFavoriteCityID()
    }


    fun fetchWeatherForeCastList(locationModel: LocationModel, temperatureUnit: String) {
        viewModelScope.launch {
            _weatherLocationData.postValue(ResultsWrapper.Loading)
            weatherRepository.getWeatherForeCastList(
                locationModel.latitude,
                locationModel.longitude,
                temperatureUnit
            ).zip(
                locationRepo.getLocationDetails(
                    locationModel.latitude,
                    locationModel.longitude
                )
            ) { weatherResponse, locationResponse ->
                return@zip WeatherLocationData(
                    getSuccessResponse(weatherResponse),
                    getSuccessResponse(locationResponse)
                )
            }
                .flowOn(Dispatchers.IO)
                .catch {
                    _weatherLocationData.postValue(ResultsWrapper.Error("Something Went Wrong"))
                }.collect {
                    _weatherLocationData.postValue(ResultsWrapper.Success(it))
                }
        }
    }


    fun insertOrDeleteFavCity(city: FavoriteCity) {
        viewModelScope.launch {
            weatherRepository.insertOrDeleteFavCity(city)
                .flowOn(Dispatchers.IO)
                .catch {
                    println("Something went wrong")
                }.collect {
                    println("Success updateQuantityByMenuID")
                }
        }
    }

    fun getAllFav(): MutableLiveData<ResultsWrapper<List<FavoriteCity>>> {
        val response = MutableLiveData<ResultsWrapper<List<FavoriteCity>>>()
        viewModelScope.launch {
            response.postValue(ResultsWrapper.Loading)
            weatherRepository.getAllFavoriteCity()
                .flowOn(Dispatchers.IO)
                .catch {
                    response.postValue(ResultsWrapper.Error("Something Went Wrong"))
                }.collect {
                    response.postValue(ResultsWrapper.Success(it))
                }
        }
        return response
    }


    fun fetchCurrentLocation() {
        locationData.startService()
    }

    fun setSelectedLocation(selectedPlace: Place) {
        _searchLocationSelectedObserver.postValue(selectedPlace.latLng)
    }

    fun setTemperatureUnit(unit: String) {
        _temperateUnitChange.postValue(unit)
    }

    fun clear() {
        _searchLocationSelectedObserver.postValue(null)
        _temperateUnitChange.postValue(null)
    }

    private fun <T> getSuccessResponse(
        response: ResultsWrapper<T>,
    ): T? {
        return when (response) {
            is ResultsWrapper.Success -> return response.response
            else -> {
                null
            }
        }
    }
}