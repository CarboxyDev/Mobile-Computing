package com.arman.assignment2.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.arman.assignment2.data.db.WeatherData
import java.time.LocalDate

@Dao
interface WeatherDataDao {
    @Insert
    fun insertWeatherData(weatherData: WeatherData)


    /** This is assuming the location doesn't change */
    @Query("SELECT * FROM WeatherData WHERE date = :date")
    fun getWeatherByDate(date: String): WeatherData?


}