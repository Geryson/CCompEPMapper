package com.example.ccompepmapper.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ccompepmapper.data.MapBase
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
}