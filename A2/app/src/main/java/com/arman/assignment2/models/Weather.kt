package com.arman.assignment2.models

import com.arman.assignment2.data.db.WeatherData
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
    @SerializedName("daily_units")
    val dailyUnits: DailyUnits,
    val daily: Daily,
)

data class DailyUnits(
    val time: String,
    @SerializedName("temperature_2m_max")
    val maxTemps: String,
    @SerializedName("temperature_2m_min")
    val minTemps: String
)

data class Daily(
    val time: List<String>,
    @SerializedName("temperature_2m_max")
    val maxTemps: List<Float>,
    @SerializedName("temperature_2m_min")
    val minTemps: List<Float>
)


data class WeatherApiError(
    val reason: String,
    val error: Boolean
)
