package com.example.mapsapp.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import com.example.mapsapp.R
import com.example.mapsapp.model.MarkerData
import com.google.android.gms.maps.model.LatLng

class MyViewModel {

    //Variable per cada marcador individual
    private var _marker = MutableLiveData(MarkerData("ITB",(LatLng(41.4534265, 2.1837151)),"", "", mutableListOf()))
    var marker = _marker

    //Llista de marcadors
    private val _markerList = MutableLiveData<MutableList<MarkerData>>()
    val markerList = _markerList

    //Títol dels marcadors
    private var _markerTitle = MutableLiveData<String>()
    var markerTitle = _markerTitle

    //Descripció dels marcadors
    private var _markerDescription = MutableLiveData<String>()
    var markerDescription = _markerDescription

    //Tipus de localització dels marcadors
    var placeType = MutableLiveData("Sense especificar")
        private set



    private var _actualMarker = MutableLiveData(MarkerData("ITB",(LatLng(41.4534265, 2.1837151)),"", "", mutableListOf()))
    var actualMarker = _actualMarker

    //Variable que controla el estat del ModalBottomSheet per crear nous marcadors.
    private var _showNewMarkerBottomSheet = MutableLiveData<Boolean>(false)
    val showNewMarkerBottomSheet = _showNewMarkerBottomSheet

    //Variable que controla el estat del ModalBottomSheet per eliminar marcadors.
    private var _showMarkerOptionsBottomSheet = MutableLiveData<Boolean>(false)
    val showMarkerOptionsBottomSheet = _showMarkerOptionsBottomSheet

    /*Variable que indica si és la primera vegada que accedim al mapa, per així fer que a detalls ens
    recondueixi a la localització del marcador.*/
    var mapaInicial: Boolean by mutableStateOf(true)
        private set

    //Variabla que controla si s'accedeix a la càmera desde el mapa o desde detalls.
    var comingFromMap : Boolean by mutableStateOf(false)
        private set

    //Llista on s'emmagatzemen les fotos fetes abans de crear el marcador.
    private var _newMarkerPhotos = MutableLiveData<MutableList<Bitmap>>(mutableListOf())
    var newMarkerPhotos = _newMarkerPhotos

    //Càmera
    private val _cameraPermissionGranted = MutableLiveData(false)
    val cameraPermissionGranted = _cameraPermissionGranted

    private val _shouldShowPermissionRationale = MutableLiveData(false)
    val shouldShowPermissionRationale = _shouldShowPermissionRationale

    private val _showPermissionDenied = MutableLiveData(false)
    val showPermissionDenied = _showPermissionDenied


    //Funció que modifica la posició en el mapa (necessària per la creació de marcadors)
    fun positionChange(newPosition: LatLng) {
        _marker.value!!.position = newPosition
    }


    //Funció que modifica el títol del marcador

    fun changeTitle(text: String) {
        _markerTitle.value = text
    }

    //Funció que modifica la descripció del marcador
    fun changeDescription(text: String) {
        _markerDescription.value = text
    }

    //Funció que permet controlar l'estat del "NewMarkerBottomSheet"
    fun setNewMarkerBottomSheet(state: Boolean) {
        _showNewMarkerBottomSheet.value = state
    }

    //Funció que permet controlar l'estat del "MarkerOptionsBottomSheet"
    fun setMarkerOptionsBottomSheet(state: Boolean) {
        _showMarkerOptionsBottomSheet.value = state
    }

    //Funció que afegeix un nou marcador a la llista de marcadors.
    fun markerAddition(newMarker: MarkerData) {
        val markers = _markerList.value.orEmpty().toMutableList()
        markers.add(newMarker)
        _markerList.value = markers
    }

    //Funció que elimina un marcador de la llista.
    fun markerDeletion(oldMarker: MarkerData) {
        val markers = _markerList.value.orEmpty().toMutableList()
        markers.remove(oldMarker)
        _markerList.value = markers
    }

    //Funció que permet escollir el tipus de localització del marcador.
    fun placeTypeChange (valor : String) {
        placeType.value = valor
    }

    fun placeTypeIconChange (placeType: String) : Int {
        return when (placeType) {
            "Cafeteria" -> R.drawable.cafeteria
            "Restaurant" -> R.drawable.restaurants
            "Entreteniment" -> R.drawable.entertainment
            "Botiga" -> R.drawable.shopping
            "Transport" -> R.drawable.transport
            else -> R.drawable.defaultplace
        }
    }

    //Funció que permet assignar a la variable marker el valor del marcador que volem mostrar a detalls.
    fun chooseMarker (actualMarker: MarkerData) {
        _actualMarker.value = actualMarker
    }

    //Funció que torna a false mapaInicial un cop hem accedit al mapa per primer cop
    fun changeMapaInicial() {
        mapaInicial = false
    }

    //Funció que modifica el valor de "comingFromMap" per indicar si la càmera s'ha obert o no des del mapa.
    fun changeComingFromMap(fromMap: Boolean) {
        comingFromMap = fromMap
    }

    //Càmera
    fun setCameraPermissionGranted(granted: Boolean) {
        _cameraPermissionGranted.value = granted
    }

    fun setShouldShowPermissionRationale(should: Boolean) {
        _shouldShowPermissionRationale.value = should
    }

    fun setShowPermissionDenied(denied : Boolean) {
        _showPermissionDenied.value = denied
    }

    //Funció que afegeix fotos al marker des de la pantalla de detalls.
    fun addPhotoToMarker(photo: Bitmap) {
        _marker.value!!.images.add(photo)
    }

    //Funció que afegeix fotos al marker des del mapa.
    fun addPhotosToNewMarker(photo: Bitmap) {
        _newMarkerPhotos.value!!.add(photo)
    }

    //Funció que neteja la llista de fotos del marcador nou en cas de sortir del BottomSheet o de crear un marcador
    fun clearPhotosFromNewMarker() {
        _newMarkerPhotos.value!!.clear()
    }

}