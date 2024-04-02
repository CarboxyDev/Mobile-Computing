package com.arman.assignment2.api

import com.arman.assignment2.models.WeatherApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query



interface WeatherApi {
    @GET("v1/archive")
    fun getHistoricalWeatherData(
        @Query("latitude") latitude: Float,
        @Query("longitude") longitude: Float,
        @Query("daily") hourlyParameters: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Call<WeatherApiResponse>
}