package com.arman.assignment3.data.db

import androidx.room.RoomDatabase
import androidx.room.Database


@Database(entities = [OrientationEntity::class], version = 1)
abstract class OrientationDB : RoomDatabase() {
    abstract fun orientationDao(): OrientationDAO;
}
