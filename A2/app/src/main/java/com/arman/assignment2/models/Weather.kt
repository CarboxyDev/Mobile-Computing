package com.arman.assignment2.models

import com.google.gson.annotations.SerializedName

data class WeatherApiResponse(
    val latitude: Float,
    val longitude: Float,
    @SerializedName("generationtime_ms")
    val generationtimeInMs: Double,
    @SerializedName("utc_offset_seconds")
    val utcOffsetInSeconds: Int,
    val timezone: String,
    @SerializedName("timezone_abbreviation")
    val timezoneAbbr: String,
    val elevation: Float,
    @SerializedName("hourly_units")
    val hourlyUnits: HourlyUnits,
    val hourly: Hourly
)

data class HourlyUnits(
    val time: String,
    @SerializedName("temperature_2m")
    val temps: String
)

data class Hourly(
    val time: List<String>,
    @SerializedName("temperature_2m")
    val temps: List<Float>
)

data class WeatherApiError(
    val reason: String,
    val error: Boolean
)