package com.example.ccompepmapper.ui.screen

import androidx.lifecycle.ViewModel
import com.example.ccompepmapper.data.MapBase
import com.example.ccompepmapper.repository.EPMapperDBRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class LocationListViewModel @Inject constructor(private val dbRepository: EPMapperDBRepository) : ViewModel() {
    fun getMapBases(): Flow<List<MapBase>> {
        return dbRepository.getAllMapBasesStream()
    }
}