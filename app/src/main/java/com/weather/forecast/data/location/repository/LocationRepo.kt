package com.weather.forecast.data.location.repository

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.weather.forecast.data.LocationModel
import com.weather.forecast.data.location.model.LocationDetails
import com.weather.forecast.helper.ResultsWrapper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

open class LocationRepo @Inject constructor(
    private val context: Context,
    private val placesClient: PlacesClient,
) {

    private val sessionToken = AutocompleteSessionToken.newInstance()

    private val placeFields = mutableListOf(Place.Field.ID, Place.Field.LAT_LNG)

    private val gcd = Geocoder(context)

    private fun buildPlaceRequests(
        query: String,
        currentLocation: LocationModel
    ): FindAutocompletePredictionsRequest {
        return FindAutocompletePredictionsRequest
            .builder()
            .setSessionToken(sessionToken)
            .setOrigin(LatLng(25.2048, 55.2708))
            .setQuery(query)
            .setCountries(listOf("IN","AE"))
            .build()
    }


    fun googleSearchPlaces(
        query: String,
        currentLocation: LocationModel
    ): Flow<MutableList<AutocompletePrediction>> =
        callbackFlow {
            placesClient.findAutocompletePredictions(buildPlaceRequests(query, currentLocation))
                .addOnCompleteListener {
                    val searchResult = mutableListOf<AutocompletePrediction>()
                    if (it.isSuccessful) {
                        print("success in googleSearchPlaces ${it.result.autocompletePredictions}")
                        searchResult.addAll(it.result.autocompletePredictions)
                    }
                    trySend(searchResult)
                }
            //Finally if collect is not in use or collecting any data we cancel this channel to prevent any leak and remove the subscription listener to the database
            awaitClose {}
        }

    fun fetchPlaceDetails(placeID: String): Flow<Place> = callbackFlow {

        placesClient.fetchPlace(FetchPlaceRequest.newInstance(placeID, placeFields))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    trySend(it.result.place)
                } else {
                    cancel(CancellationException("API Error", it.exception))
                }
            }

        awaitClose {}
    }

    fun getLocationDetails(lat: Double, lng: Double) = flow {
        val addresses = gcd.getFromLocation(lat, lng, 3)
        if (addresses.size > 0)
            emit(ResultsWrapper.Success(getAddress(addresses[0])))
        else
            emit(ResultsWrapper.Error("Some thing Went Wrong"))
    }


    private fun getAddress(address: Address): LocationDetails {
        val geoLocation = LocationDetails()

        //address
        address.getAddressLine(if (address.maxAddressLineIndex != -1) address.maxAddressLineIndex else 0)
            ?.let {
                geoLocation.address = it
            }

        address.subLocality?.let {
            geoLocation.locality = it
        }

        address.locality?.let {
            geoLocation.city = it
        }

        address.postalCode?.let {
            geoLocation.pincode = it
        }

        address.adminArea?.let {
            geoLocation.state = it
        }

        address.countryName?.let {
            geoLocation.country = it
        }
        return geoLocation
    }
}