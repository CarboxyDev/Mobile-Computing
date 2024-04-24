package com.arman.assignment3.data

import com.arman.assignment3.data.db.OrientationDAO
import com.arman.assignment3.data.db.OrientationDB
import androidx.room.Room.databaseBuilder
import android.content.Context




class DatabaseRepository(private val context: Context) {
    private val database = databaseBuilder(
        context.applicationContext,
        OrientationDB::class.java,
        "orientation_database"
    ).allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build()

    private val orientationDao = database.orientationDao();

    fun getOrientationDao(): OrientationDAO {
        return this.orientationDao;
    }
}