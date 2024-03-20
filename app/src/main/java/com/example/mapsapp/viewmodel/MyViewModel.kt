package com.example.mapsapp.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import com.example.mapsapp.model.MarkerData
import com.google.android.gms.maps.model.LatLng

class MyViewModel {
    private var _marker = MutableLiveData(MarkerData("ITB",(LatLng(41.4534265, 2.1837151)),"", "", mutableListOf()))
    var marker = _marker
    private val _markerList = MutableLiveData<MutableList<MarkerData>>()
    val markerList = _markerList

    var placeType : String by mutableStateOf("Sense especificar")
        private set

    var mapaInicial: Boolean by mutableStateOf(true)
        private set

    private val _cameraPermissionGranted = MutableLiveData(false)
    val cameraPermissionGranted = _cameraPermissionGranted

    private val _shouldShowPermissionRationale = MutableLiveData(false)
    val shouldShowPermissionRationale = _shouldShowPermissionRationale

    private val _showPermissionDenied = MutableLiveData(false)
    val showPermissionDenied = _showPermissionDenied
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

    fun chooseMarker (actualMarker: MarkerData) {
        _marker.value = actualMarker
    }

    fun changeMapaInicial() {
        mapaInicial = false
    }

    fun setCameraPermissionGranted(granted: Boolean) {
        _cameraPermissionGranted.value = granted
    }

    fun setShouldShowPermissionRationale(should: Boolean) {
        _shouldShowPermissionRationale.value = should
    }

    fun setShowPermissionDenied(denied : Boolean) {
        _showPermissionDenied.value = denied
    }

    fun addPhotoToMarker(photo: Bitmap) {
        _marker.value!!.images.add(photo)
    }

}