package com.example.ccompepmapper.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ccompepmapper.data.MapBase
import com.example.ccompepmapper.repository.EPMapperDBRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationMapViewModel @Inject constructor(
    private val dbRepository: EPMapperDBRepository) : ViewModel() {
    fun addMapBase(mapBase: MapBase) {
        viewModelScope.launch(Dispatchers.IO) {
            dbRepository.insertMapBase(mapBase)
        }
    }
}