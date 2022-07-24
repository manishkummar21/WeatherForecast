package com.weather.forecast.data.location.model

data class LocationDetails(
    var address: String, var locality: String,
    var city: String,
    var pincode: String,
    var state: String,
    var country: String
) {
    constructor() : this("", "", "", "", "", "")
}
