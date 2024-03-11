package com.example.mapsapp.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.mapsapp.model.MarkerData
import com.google.android.gms.maps.model.LatLng

class MyViewModel {
    private var _position = LatLng(41.4534265, 2.1837151)
    val position = _position
    private val _markerList = MutableLiveData<MutableList<MarkerData>>()
    val markerList = _markerList


    fun positionChange(newPosition: LatLng) {
        _position = newPosition
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
}