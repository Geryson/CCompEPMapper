package com.example.ccompepmapper.repository

import com.example.ccompepmapper.data.MapBase
import com.example.ccompepmapper.data.MapBaseDao
import com.example.ccompepmapper.data.MapLayer
import com.example.ccompepmapper.data.MapLayerDao
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class EPMapperDBRepository @Inject constructor(
    private val mapBaseDao: MapBaseDao,
    private val mapLayerDao: MapLayerDao) {

    fun getAllMapBasesStream(): Flow<List<MapBase>> {
        return mapBaseDao.getAllMapBases()
    }

    fun getMapBaseById(mapBaseId: Int): Flow<MapBase> {
        return mapBaseDao.getMapBaseById(mapBaseId)
    }

    suspend fun insertMapBase(mapBase: MapBase) {
        mapBaseDao.insertMapBase(mapBase)
    }

    fun getAllMapLayerById(mapLayerId: Int): Flow<MapLayer?> {
        return mapBaseDao.getMapLayerByMapBaseId(mapLayerId)
    }

    suspend fun insertMapLayer(mapLayer: MapLayer): Long {
        return mapLayerDao.insertMapLayer(mapLayer)
    }

    suspend fun deleteMapBase(mapBase: MapBase) {
        mapBaseDao.deleteMapBase(mapBase)
    }

    suspend fun deleteMapLayer(mapLayerId: Int) {
        mapLayerDao.deleteMapLayerById(mapLayerId)
    }

    suspend fun updateMapLayer(newMapLayer: MapLayer) {
        mapLayerDao.updateMapLayer(newMapLayer)
    }

    suspend fun updateMapBase(mapBase: MapBase) {
        mapBaseDao.updateMapBase(mapBase)
    }

    suspend fun deleteMapLayerById(mapLayerId: Int) {
        mapLayerDao.deleteMapLayerById(mapLayerId)
    }
}