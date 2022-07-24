package com.weather.forecast.helper

sealed class ResultsWrapper<out T> {
    object Loading : ResultsWrapper<Nothing>()
    data class Success<T>(val response: T) : ResultsWrapper<T>()
    data class Error(val error: String, val code: Int = 500) : ResultsWrapper<Nothing>()
}
