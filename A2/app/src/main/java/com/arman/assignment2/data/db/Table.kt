package com.arman.assignment2.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date
import java.time.LocalDate

@Entity
data class WeatherData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val maxTemp: Float,
    val minTemp: Float,
    val longitude: Float,
    val latitude: Float
)