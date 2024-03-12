package com.example.mapsapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import com.example.mapsapp.model.MarkerData
import com.google.android.gms.maps.model.LatLng

class MyViewModel {
    private var _marker = MutableLiveData(MarkerData("ITB",(LatLng(41.4534265, 2.1837151))," "))
    val marker = _marker
    private val _markerList = MutableLiveData<MutableList<MarkerData>>()
    val markerList = _markerList
    var placeType : String by mutableStateOf("Restaurant")
        private set


    fun positionChange(newPosition: LatLng) {
        _marker.value!!.position = newPosition
    }
    fun markerAddition(newMarker: MarkerData) {
        val markers = _markerList.value.orEmpty().toMutableList()
        markers.add(newMarker)
        _markerList.value = markers
    }

    fun markerDeletion(oldMarker: MarkerData) {
        val markers = _markerList.value.orEmpty().toMutableList()
        markers.remove(oldMarker)
        _markerList.value = markers
    }

    fun placeTypeChange (valor : String) {
        placeType = valor
    }

}