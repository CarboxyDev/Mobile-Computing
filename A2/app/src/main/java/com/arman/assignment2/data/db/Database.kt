package com.arman.assignment2.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.arman.assignment2.data.dao.WeatherDataDao

@Database(entities = [WeatherData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDataDao(): WeatherDataDao

    companion object {}
}