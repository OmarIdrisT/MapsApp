package com.example.mapsapp.viewmodel

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.MutableLiveData
import com.example.mapsapp.R
import com.example.mapsapp.firebase.FirebaseRepository
import com.example.mapsapp.firebase.firebasemodels.MarkerData
import com.example.mapsapp.firebase.firebasemodels.User
import com.example.mapsapp.models.FilterOption
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.storage.FirebaseStorage
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyViewModel {
    //Variable per controlar la font de text de la App
    val brownista = FontFamily(Font(R.font.brownista))

    //Variable per controlar la posició
    private var _markPosition = MutableLiveData(LatLng(41.4534265, 2.1837151))
    var markPosition = _markPosition

    //Llista de marcadors
    private val _markerList = MutableLiveData<MutableList<MarkerData>>()
    val markerList = _markerList

    //Llista filtrada de marcadors
    private val _filteredMarkerList = MutableLiveData<MutableList<MarkerData>>()
    val filteredMarkerList = _filteredMarkerList

    //Títol dels marcadors
    private var _markerTitle = MutableLiveData<String>()
    var markerTitle = _markerTitle

    //Descripció dels marcadors
    private var _markerDescription = MutableLiveData<String>()
    var markerDescription = _markerDescription


    //Tipus de localització dels marcadors
    var placeType = MutableLiveData("Not specified")
        private set


    //Variable per controlar el marcador amb el que s'està treballant
    private var _actualMarker =
        MutableLiveData(MarkerData("", "","ITB", (LatLng(41.4534265, 2.1837151)), "", "", mutableListOf()))
    var actualMarker = _actualMarker

    //Variable que controla l' estat del ModalBottomSheet per crear nous marcadors.
    private var _showNewMarkerBottomSheet = MutableLiveData<Boolean>(false)
    val showNewMarkerBottomSheet = _showNewMarkerBottomSheet

    //Variable que controla l' estat del ModalBottomSheet per eliminar marcadors.
    private var _showMarkerOptionsBottomSheet = MutableLiveData<Boolean>(false)
    val showMarkerOptionsBottomSheet = _showMarkerOptionsBottomSheet

    /*Variable que indica si és la primera vegada que accedim al mapa, per així fer que a detalls ens
    recondueixi a la localització del marcador.*/
    var mapaInicial: Boolean by mutableStateOf(true)
        private set

    //Variable que controla si s'accedeix a la càmera desde el mapa o desde detalls.
    var comingFromMap: Boolean by mutableStateOf(false)
        private set

    //Variable que controla si s'ha de desplegar el filtre o no.
    var deployFilter = MutableLiveData(false)

    //Variable que controla si s'ha de mostrar el toast de registre.
    var showRegisterToast = MutableLiveData(false)

    //Llista on s'emmagatzemen les fotos fetes abans de crear el marcador.
    private var _newMarkerPhotos = MutableLiveData<MutableList<String>>(mutableListOf())
    var newMarkerPhotos = _newMarkerPhotos

    //Variables pels permissos de la càmera
    private val _cameraPermissionGranted = MutableLiveData(false)
    val cameraPermissionGranted = _cameraPermissionGranted

    private val _shouldShowPermissionRationale = MutableLiveData(false)
    val shouldShowPermissionRationale = _shouldShowPermissionRationale

    private val _showPermissionDenied = MutableLiveData(false)
    val showPermissionDenied = _showPermissionDenied

    //Actual user
    private val _actualUser = MutableLiveData<User>()

    //Authentication

    //Variable que controla si l'usuari està loggejat o no.
    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn = _isLoggedIn

    //Variable que controla si s'ha de mostrar la UI de login o la de registre
    private val _registerMode = MutableLiveData(false)
    val registerMode = _registerMode

    //Variable que controla si s'accedeix per primera vegada a la pantalla de login.
    var firstAccess by mutableStateOf(true)


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

    //Funció que elimina un marcador de la llista.
    fun markerDeletionFromList(oldMarker: MarkerData) {
        val markers = _markerList.value.orEmpty().toMutableList()
        markers.remove(oldMarker)
        _markerList.value = markers
    }

    //Funció que permet escollir el tipus de localització del marcador.
    fun placeTypeChange(valor: String) {
        placeType.value = valor
    }

    //Funció que assigna l'icona corresponent al tipus de localització.
    fun placeTypeIconChange(placeType: String): Int {
        return when (placeType) {
            "Cafe" -> R.drawable.cafeteria
            "Restaurant" -> R.drawable.restaurants
            "Entertainment" -> R.drawable.entertainment
            "Shop" -> R.drawable.shopping
            "Transport" -> R.drawable.transport
            else -> R.drawable.defaultplace
        }
    }

    //Funció per filtrar
    fun filterList(filterOption: FilterOption) {
        filteredMarkerList.value = when (filterOption) {
            FilterOption.ALL -> _markerList.value
            else -> _markerList.value!!.filter { marker ->
                marker.type == filterOption.title
            }.toMutableList()
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

    //Funció que modifica el valor de "deployFilter" per indicar si s'ha de desplegar el filtre o no.
    fun changeDeployFilter(value: Boolean) {
        deployFilter.value = value
    }

    //Funcions relacionades amb els permissos de la càmera
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
        val updatedMarker = MarkerData(marker.userId, marker.id, marker.title, marker.position, marker.description, marker.description, marker.images.toMutableList().apply { add(photo) })
        chooseMarker(updatedMarker)
        markers.remove(marker)
        markers.add(updatedMarker)
        _markerList.value = markers
    }

    //Funció que afegeix fotos a una llista temporal.
    fun addPhotosToNewMarker(photo: String) {
        _newMarkerPhotos.value!!.add(photo)
    }

    //Funció que neteja la llista de fotos del marcador nou en cas de sortir del BottomSheet o de crear un marcador
    fun clearPhotosFromNewMarker() {
        _newMarkerPhotos.value!!.clear()
    }

    //Conversor Bitmap a Uri
    fun bitmapToUri(context: Context, bitmap: Bitmap): Uri? {
        val filename = "${System.currentTimeMillis()}.jpg"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, filename)
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        }

        val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        uri?.let {
            val outstream: OutputStream? = context.contentResolver.openOutputStream(it)
            outstream?.let { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
            outstream?.close()
        }

        return uri
    }

    //Firebase

    //Instancia del repositori de Firebase
    val repository = FirebaseRepository()

    //Variables per controlar la navegació
    private val _goToNext = MutableLiveData<Boolean>()
    val goToNext = _goToNext

    //Variable per controlar el userId
    private val _userId = MutableLiveData<String>()
    val userId = _userId
    //Variable per controlar l'usuari loggejat
    private val _loggedUser = MutableLiveData<String>()
    val loggedUser = _loggedUser
    //Variable per controlar si el login ha fallat
    private val _loginFail = MutableLiveData<Boolean>()
    val loginFail = _loginFail
    //Variable per controlar si el registre ha fallat
    private val _registerFail = MutableLiveData<Boolean>()
    val registerFail = _registerFail

    //Funció que permet obtenir l'usuari actual
    fun getUser(userId: String) {
        repository.getUserFromDatabase(userId).addSnapshotListener { value, error ->
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
            } else {
                Log.e("UserRepository", "Current data: null")
            }
        }
    }

    //Funció que permet pujar una imatge a Firebase Storage
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
                        repository.editMarker(actualMarker.value!!)
                    }
                    else {
                        addPhotosToNewMarker(it.toString())
                    }

                }
            }
            .addOnFailureListener {
                Log.i("IMAGE UPLOAD", "Image upload failed")
            }
    }

    //Funció que permet afegir un marcador a Firebase
    fun addMarkerToFirebase(marker: MarkerData) {
        repository.addMarker(marker)
    }

    //Funció que permet obtenir els marcadors de Firebase per un usuari concret
    fun getMarkers() {
        repository.getMarkers().addSnapshotListener { value, error ->
            if (error != null) {
                Log.e("Firestore error", error.message.toString())
                return@addSnapshotListener
            }
            val tempList = mutableListOf<MarkerData>()
            for (dc: DocumentChange in value?.documentChanges!!) {
                if (dc.type == DocumentChange.Type.ADDED) {
                    val document = dc.document
                    val title = document.getString("title") ?: ""
                    val description = document.getString("description") ?: ""
                    val ubicacionMap = document.get("position") as? Map<String, Double>
                    val position = LatLng(
                        ubicacionMap?.get("latitude") ?: 0.0,
                        ubicacionMap?.get("longitude") ?: 0.0
                    )
                    val type = document.getString("type") ?: ""
                    val images =
                        document.get("images") as? MutableList<String> ?: mutableListOf<String>()
                    val markerId = document.getString("id") ?: ""
                    val userId = document.getString("userId") ?: ""
                    val newMark =
                        MarkerData(userId, markerId, title, position, description, type, images)
                    tempList.add(newMark)
                }
            }
            val filtredList = mutableListOf<MarkerData>()
            for (marca in tempList) {
                if (marca.userId == userId.value) {
                    filtredList.add(marca)
                }
            }
            _markerList.value = filtredList
        }
    }

    //Variable per controlar el filtre seleccionat
    var selectedFilter = MutableLiveData(FilterOption.ALL)


    //Funció que permet actualitzar el filtre seleccionat
    fun updateFilter(filter: FilterOption) {
        selectedFilter.value = filter
        filterList(selectedFilter.value!!)
    }

    //Funció que permet esborrar un marcador de la App
    fun deleteMarker(marker: MarkerData) {
        markerDeletionFromList(marker)
        repository.deleteMarkerFromDatabase(marker)
        filterList(selectedFilter.value!!)
    }

    //Funció que permet esborrar una imatge de la App
    fun editMarker(newTitle: String, newDescription: String, newType: String) {
        _actualMarker.value?.title = newTitle
        _actualMarker.value?.description = newDescription
        _actualMarker.value?.type = newType
        repository.editMarker(_actualMarker.value!!)
    }


    //Authentication

    //Funció que permet registrar un usuari
    fun register (username: String, password: String) {
        repository.auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    _registerFail.value = false
                    showRegisterToast.value = true
                }
                else {
                    _registerFail.value = true
                }
            }
            .addOnFailureListener {
                _registerFail.value = true
            }
    }

    //Funció que permet tancar el toast de registre
    fun restoreRegisterToast() {
        showRegisterToast.value = false
    }

    //Funció que permet loggejar un usuari
    fun login(username: String?, password: String?) {
        repository.auth.signInWithEmailAndPassword(username!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginFail.value = false
                    _userId.value = task.result.user?.uid
                    _loggedUser.value = task.result.user?.email?.split("@")?.get(0)
                    _goToNext.value = true
                    _isLoggedIn.value = true
                }
                else {
                    _goToNext.value = false
                    _loginFail.value = true
                }
            }
            .addOnFailureListener {
                _goToNext.value = false
                _loginFail.value = true
            }
    }

    //Funció que permet actualitzar el missatge de registre fallit
    fun updateRegisterFail() {
        _registerFail.value = false
    }

    //Funció que permet actualitzar el missatge de login fallit
    fun updateLoginFail() {
        _loginFail.value = false
    }

    //Funció que actualitza el valor de _goToNext per fer el logout
    fun updateGoToNext() {
        _goToNext.value = false
    }


    //Funció que permet tancar la sessió
    fun logOut() {
        updateGoToNext()
        isLogged(false)
        repository.auth.signOut()

    }


    //Funció que permet actualitzar el valor de _isLoggedIn comprovant si l'usuari està loggejat o no
    fun isLogged(value: Boolean) {
        _isLoggedIn.value = value
    }

    //Funció que permet canviar el mode de registre a login i viceversa
    fun changeMode() {
        if (_registerMode.value == false) {
            _registerMode.value = true
        }
        else {
            _registerMode.value = false
        }
    }

    //Funció que permet actualitzar el valor de firstAccess
    fun updateAccess() {
        firstAccess = false
    }






}
