package com.example.ccompepmapper.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ccompepmapper.data.MapBase
import com.example.ccompepmapper.data.MapLayer
import com.example.ccompepmapper.repository.EPMapperDBRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationEditorViewModel @Inject constructor(
    private val dbRepository: EPMapperDBRepository
) : ViewModel() {
    fun getMapBase(mapBaseId: Int): Flow<MapBase> {
        if (mapBaseId != -1) {
            return dbRepository.getMapBaseById(mapBaseId)
        }
        return emptyFlow()
    }

    fun getMapLayerById(mapBaseId: Int): Flow<MapLayer?> {
        return dbRepository.getAllMapLayerById(mapBaseId)
    }

    fun addNewMapBaseAndMapLayer(mapLayer: MapLayer?, mapBase: MapBase) {
        viewModelScope.launch(Dispatchers.IO) {
            if (mapLayer != null) {
                val response = dbRepository.insertMapLayer(mapLayer)
                dbRepository.insertMapBase(mapBase.copy(mapLayerId = response.toInt()))
            } else {
                dbRepository.insertMapBase(mapBase)
            }
        }
    }
}