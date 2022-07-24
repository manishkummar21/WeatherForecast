package com.weather.forecast.ui.dashboard.viewmodels

import android.location.Address
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.weather.forecast.PreferenceManager
import com.weather.forecast.data.LocationModel
import com.weather.forecast.data.location.model.LocationDetails
import com.weather.forecast.data.location.repository.LocationRepo
import com.weather.forecast.helper.ResultsWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val locationRepo: LocationRepo
) : ViewModel() {

    fun initializeSearchObservable(textChangeStateFlow: StateFlow<String>): MutableLiveData<ResultsWrapper<MutableList<AutocompletePrediction>>> {
        val response = MutableLiveData<ResultsWrapper<MutableList<AutocompletePrediction>>>()

        viewModelScope.launch {
            textChangeStateFlow.debounce(300)
                .filter { query ->
                    if (query.isEmpty() || query.length < 3) {
                        response.postValue(ResultsWrapper.Success(mutableListOf()))
                        return@filter false
                    } else return@filter true

                }
                .distinctUntilChanged()
                .flatMapLatest {
                    response.postValue(ResultsWrapper.Loading)
                    locationRepo.googleSearchPlaces(
                        it,
                        preferenceManager.lastKnowCurrentLocation ?: LocationModel(0.0, 0.0)
                    )
                        .catch { emitAll(flowOf(mutableListOf())) }
                }
                .flowOn(Dispatchers.IO)
                .collect { result ->
                    response.postValue(ResultsWrapper.Success(result))
                }
        }

        return response
    }

    fun fetchPlaceDetails(placeID: String): MutableLiveData<ResultsWrapper<Place>> {
        val response = MutableLiveData<ResultsWrapper<Place>>()

        viewModelScope.launch {
            locationRepo.fetchPlaceDetails(placeID)
                .flowOn(Dispatchers.IO)
                .catch {
                    response.postValue(ResultsWrapper.Error("Some thing Went Wrong"))
                }.collect {
                    response.postValue(ResultsWrapper.Success(it))
                }
        }

        return response
    }

}