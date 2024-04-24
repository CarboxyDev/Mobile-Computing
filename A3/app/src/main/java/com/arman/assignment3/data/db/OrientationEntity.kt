package com.arman.assignment3.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OrientationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val roll: Float,
    val pitch: Float,
    val yaw: Float,
    val timestamp: Long = System.currentTimeMillis(),
)
