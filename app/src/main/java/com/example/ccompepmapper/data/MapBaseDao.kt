package com.example.ccompepmapper.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MapBaseDao {
    @Query("SELECT * FROM mapbase")
    fun getAllMapBases(): Flow<List<MapBase>>

    @Query("SELECT * FROM mapbase WHERE mapBaseId = :id")
    fun getMapBaseById(id: Int): Flow<MapBase>

    @Query("SELECT * FROM maplayer WHERE mapLayerId IN (SELECT mapLayerId FROM mapbase WHERE mapBaseId = :mapBaseId)")
    fun getMapLayerByMapBaseId(mapBaseId: Int): Flow<MapLayer?>

    @Delete
    suspend fun deleteMapBase(mapBase: MapBase)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMapBase(mapBase: MapBase)

    @Update
    suspend fun updateMapBase(mapBase: MapBase)

}