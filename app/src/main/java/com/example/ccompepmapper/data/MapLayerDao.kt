package com.example.ccompepmapper.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MapLayerDao {
    @Query("SELECT * FROM maplayer WHERE mapLayerId = :id")
    fun getMapLayerById(id: Int): Flow<MapLayer>

    @Delete
    suspend fun deleteMapLayer(mapLayer: MapLayer)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMapLayer(mapLayer: MapLayer) : Long

    @Update
    suspend fun updateMapLayer(mapLayer: MapLayer)

}