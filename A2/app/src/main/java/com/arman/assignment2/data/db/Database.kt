package com.arman.assignment2.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.arman.assignment2.data.dao.WeatherDataDao
import java.time.LocalDate



@Database(entities = [WeatherData::class], version = 1)
//@TypeConverters(AppDatabase.LocalDateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDataDao(): WeatherDataDao

    companion object {}

//    class LocalDateConverter {
//        @TypeConverter
//        fun fromTimestamp(value: Long?): LocalDate? {
//            return value?.let { LocalDate.ofEpochDay(it) }
//        }
//
//        @TypeConverter
//        fun dateToTimestamp(date: LocalDate?): Long? {
//            return date?.toEpochDay()
//        }
//    }


}