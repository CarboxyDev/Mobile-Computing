package com.arman.assignment3.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OrientationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roll: Float,
    val pitch: Float,
    val yaw: Float,
    val timestamp: Long = System.currentTimeMillis(),
)

fun resampleOrientationData(data: List<OrientationEntity>, interval: Long): List<OrientationEntity> {
    return data
        .groupBy { it.timestamp / interval }
        .map { entry ->
            val averageRoll = entry.value.map { it.roll }.average().toFloat()
            val averagePitch = entry.value.map { it.pitch }.average().toFloat()
            val averageYaw = entry.value.map { it.yaw }.average().toFloat()
            OrientationEntity(entry.key * interval, averageRoll, averagePitch, averageYaw)
        }
}