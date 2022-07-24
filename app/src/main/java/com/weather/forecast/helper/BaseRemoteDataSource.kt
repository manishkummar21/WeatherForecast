package com.weather.forecast.helper

import retrofit2.Response


abstract class BaseRemoteDataSource {


    protected suspend fun <T> getResponse(
        request: suspend () -> Response<T>,
        defaultErrorMessage: String = "Something Went Wrong"
    ): ResultsWrapper<T> {
        lateinit var response: ResultsWrapper<T>
        try {
            val result = request.invoke()
            if (result.isSuccessful) {
                when (result.code()) {
                    204 -> {
                        response = ResultsWrapper.Error("No Content", 204)
                    }
                    else -> {
                        result.body()?.run {
                            response = ResultsWrapper.Success(result.body()!!)
                        }
                    }
                }

            } else {
                response = handleErrors(result, defaultErrorMessage)
            }

        } catch (e: Throwable) {
            response = ResultsWrapper.Error(e.message ?: "Something Went Wrong", -1)
        }
        return response
    }

    private fun <T> handleErrors(
        response: Response<T>,
        defaultErrorMessage: String
    ): ResultsWrapper<T> {
        return ResultsWrapper.Error(defaultErrorMessage)
    }
}