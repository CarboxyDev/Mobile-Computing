package com.arman.assignment3.data.db

import kotlinx.coroutines.flow.Flow
import androidx.room.Insert
import androidx.room.Dao;
import androidx.room.Query

@Dao
public interface OrientationDAO {
    @Insert
    suspend fun insertOrientationData(orientation: OrientationEntity)

    @Query("SELECT * FROM OrientationEntity ORDER BY timestamp")
    fun getOrientationData(): Flow<List<OrientationEntity>>

    @Query("DELETE FROM OrientationEntity")
    suspend fun deleteAllOrientationData(): Int

    @Query("SELECT * FROM OrientationEntity WHERE id = :id")
    suspend fun getOrientationDataById(id: Int): OrientationEntity

    @Query("SELECT * FROM OrientationEntity WHERE timestamp = :timestamp")
    suspend fun getOrientationDataByTimestamp(timestamp: Long): OrientationEntity


}
