package com.example.ccompepmapper.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mapbase")
data class MapBase(
    @PrimaryKey(autoGenerate = true) val mapBaseId: Int = 0,
    val mapLayerId: Int?,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val zoomLevel: Double,
)
