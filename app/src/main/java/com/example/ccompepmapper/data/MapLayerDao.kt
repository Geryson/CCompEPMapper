package com.example.ccompepmapper.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MapLayerDao {
    @Query("SELECT * FROM maplayer WHERE mapLayerId = :id")
    fun getMapLayerById(id: Int): Flow<MapLayer>

    @Query("DELETE FROM maplayer WHERE mapLayerId = :id")
    suspend fun deleteMapLayerById(id: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMapLayer(mapLayer: MapLayer) : Long

    @Update
    suspend fun updateMapLayer(mapLayer: MapLayer)

}