package com.example.ccompepmapper.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ccompepmapper.data.MapBase
import com.example.ccompepmapper.data.MapLayer
import com.example.ccompepmapper.repository.EPMapperDBRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationListViewModel @Inject constructor(private val dbRepository: EPMapperDBRepository) :
    ViewModel() {
    fun getMapBases(): Flow<List<MapBase>> {
        return dbRepository.getAllMapBasesStream()
    }

    fun deleteMapBase(mapBase: MapBase) {
        viewModelScope.launch(Dispatchers.IO) {
            if (mapBase.mapLayerId != null) {
                dbRepository.deleteMapLayer(mapBase.mapLayerId)
            }
            dbRepository.deleteMapBase(mapBase)
        }
    }

    fun addParliamentMapBaseAndLayer() {
        viewModelScope.launch(Dispatchers.IO) {
            val newMapLayer = MapLayer(upperLeftLatitude = 47.508836895431735,
                upperLeftLongitude = 19.04364988427083,
                lowerRightLatitude = 47.505599337523755,
                lowerRightLongitude = 19.048442735134817)
            val result = dbRepository.insertMapLayer(newMapLayer)
            val newMapBase = MapBase(mapLayerId = result.toInt(),
                name = "Parliament",
                latitude = 47.50721814143977,
                longitude = 19.046046383609934,
                zoomLevel = 15.952258110046387,
                destinationRadius = 0.18000000715255737
                )
            dbRepository.insertMapBase(newMapBase)
        }
    }
}