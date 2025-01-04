package com.example.ccompepmapper.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MapLayer(
    @PrimaryKey(autoGenerate = true) val mapLayerId: Int = 0,
    val upperLeftLatitude: Double,
    val upperLeftLongitude: Double,
    val lowerRightLatitude: Double,
    val lowerRightLongitude: Double,
)
