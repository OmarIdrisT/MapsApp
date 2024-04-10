package com.example.mapsapp.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import com.example.mapsapp.Firebase.FirebaseModels.User
import com.example.mapsapp.Firebase.FirebaseRepository
import com.example.mapsapp.R
import com.example.mapsapp.Firebase.FirebaseModels.MarkerData
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyViewModel {

    //Variable per cada marcador individual
    private var _marker =
        MutableLiveData(MarkerData("","","ITB", (LatLng(41.4534265, 2.1837151)), "", "", mutableListOf()))
    var marker = _marker

    //Variable per controlar la posició
    private var _markPosition = MutableLiveData(LatLng(41.4534265, 2.1837151))
    var markPosition = _markPosition

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


    //Variable per controlar el marcador amb el que s'està treballant
    private var _actualMarker =
        MutableLiveData(MarkerData("", "","ITB", (LatLng(41.4534265, 2.1837151)), "", "", mutableListOf()))
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
    var comingFromMap: Boolean by mutableStateOf(false)
        private set

    //Llista on s'emmagatzemen les fotos fetes abans de crear el marcador.
    private var _newMarkerPhotos = MutableLiveData<MutableList<String>>(mutableListOf())
    var newMarkerPhotos = _newMarkerPhotos

    //Càmera
    private val _cameraPermissionGranted = MutableLiveData(false)
    val cameraPermissionGranted = _cameraPermissionGranted

    private val _shouldShowPermissionRationale = MutableLiveData(false)
    val shouldShowPermissionRationale = _shouldShowPermissionRationale

    private val _showPermissionDenied = MutableLiveData(false)
    val showPermissionDenied = _showPermissionDenied

    //Llista usuaris
    private val _userList = MutableLiveData<MutableList<User>>(mutableListOf())
    val userList = _userList

    //Actual user
    private val _actualUser = MutableLiveData<User>()
    val actualUser = _actualUser

    //Age && userName
    private val _age = MutableLiveData<String>()
    val age = _age
    private val _userName = MutableLiveData<String>()
    val userName = _userName

    //Uri imatges
    val uri = { mutableStateOf(Uri.parse("")) }

    //Funció que modifica la posició en el mapa (necessària per la creació de marcadors)
    fun positionChange(newPosition: LatLng) {
        _markPosition.value = newPosition
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
        Log.i("objeto", newMarker.toString())
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
    fun placeTypeChange(valor: String) {
        placeType.value = valor
    }

    fun placeTypeIconChange(placeType: String): Int {
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
    fun chooseMarker(actualMarker: MarkerData) {
        _actualMarker.value = actualMarker
    }

    //Funció que torna a false mapaInicial un cop hem accedit al mapa per primer cop
    fun changeMapaInicial(value: Boolean) {
        mapaInicial = value
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

    fun setShowPermissionDenied(denied: Boolean) {
        _showPermissionDenied.value = denied
    }

    //Funció que afegeix fotos al marker des de la pantalla de detalls.
    fun addPhotoToMarker(marker: MarkerData, photo: String) {
        val markers = _markerList.value.orEmpty().toMutableList()
        val updatedMarker = marker.copy(images = marker.images.toMutableList().apply { add(photo) })
        chooseMarker(updatedMarker)
        markers.remove(marker)
        markers.add(updatedMarker)
        _markerList.value = markers
    }

    //Funció que afegeix fotos al marker des del mapa.
    fun addPhotosToNewMarker(photo: String) {
        _newMarkerPhotos.value!!.add(photo)
    }

    //Funció que neteja la llista de fotos del marcador nou en cas de sortir del BottomSheet o de crear un marcador
    fun clearPhotosFromNewMarker() {
        _newMarkerPhotos.value!!.clear()
    }

    //Firebase

    val repository = FirebaseRepository()

    //Firebase getUsers()
    fun getUsers() {
        repository.getUsers().addSnapshotListener { value, error ->
            if (error != null) {
                Log.e("Firestore error", error.message.toString())
                return@addSnapshotListener
            }
            val tempList = mutableListOf<User>()
            for (dc: DocumentChange in value?.documentChanges!!) {
                if (dc.type == DocumentChange.Type.ADDED) {
                    val newUser = dc.document.toObject(User::class.java)
                    newUser.userId = dc.document.id
                    tempList.add(newUser)
                }
            }
            _userList.value = tempList
        }
    }

    //Firebase getUser()
    fun getUser(userId: String) {
        repository.getUser(userId).addSnapshotListener { value, error ->
            if (error != null) {
                Log.e("UserRepository", "Listen failted", error)
                return@addSnapshotListener
            }
            if (value != null && value.exists()) {
                val user = value.toObject(User::class.java)
                if (user != null) {
                    user.userId = userId
                }
                _actualUser.value = user
                _userName.value = _actualUser.value!!.userName
                _age.value = _actualUser.value!!.age.toString()
            } else {
                Log.e("UserRepository", "Current data: null")
            }
        }
    }

    //Pujar imatge
    fun uploadImage(imageUri: Uri) {
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storage = FirebaseStorage.getInstance().getReference("images/$fileName")
        storage.putFile(imageUri)
            .addOnSuccessListener {
                Log.i("IMAGE UPLOAD", "Image uploaded successfully")
                storage.downloadUrl.addOnSuccessListener {
                    Log.i("IMAGEN", it.toString())
                    if (!comingFromMap) {
                        addPhotoToMarker(actualMarker.value!!, it.toString())
                    }
                    else {
                        newMarkerPhotos.value!!.add(it.toString())
                    }

                }
            }
            .addOnFailureListener {
                Log.i("IMAGE UPLOAD", "Image upload failed")
            }
    }

    fun addMarkerToFirebase(marker: MarkerData) {
        val repository = FirebaseRepository()
        repository.addMarker(marker)
    }
}
