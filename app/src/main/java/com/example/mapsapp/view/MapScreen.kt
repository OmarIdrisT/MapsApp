package com.example.mapsapp.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.example.mapsapp.MainActivity
import com.example.mapsapp.MyCamera
import com.example.mapsapp.model.MarkerData
import com.example.mapsapp.myDropDownMenu
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.viewmodel.MyViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(myViewModel: MyViewModel, navigationController: NavController) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val myMarker: MarkerData by myViewModel.marker.observeAsState(MarkerData("ITB",(LatLng(41.4534265, 2.1837151))," ", "", mutableListOf()))
    val llistaMarkers:MutableList<MarkerData> by myViewModel.markerList.observeAsState(mutableListOf(MarkerData("ITB",(LatLng(41.4534265, 2.1837151))," ", "", mutableListOf())))
    var placeType: String by remember { mutableStateOf(myViewModel.placeType) }
    val newMarkerPhotos: MutableList<Bitmap> by myViewModel.newMarkerPhotos.observeAsState(mutableListOf())

    var showDeletionBottomSheet by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }


    var myText by remember{ mutableStateOf("") }
    var myDescription by remember { mutableStateOf("") }

    val context = LocalContext.current
    val fusedLocationProviderClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var lastKnownLocation by remember { mutableStateOf<Location?>(null) }
    var deviceLatLng by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    val cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(deviceLatLng, 18f) }
    val locationResult = fusedLocationProviderClient.getCurrentLocation(100, null)

    val mapaInicial by remember { mutableStateOf(myViewModel.mapaInicial) }

    locationResult.addOnCompleteListener(context as MainActivity) { task ->
        if (task.isSuccessful) {
            lastKnownLocation = task.result
            if (mapaInicial) {
                deviceLatLng = LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
            }
            else {
                deviceLatLng = LatLng(myMarker.position.latitude, myMarker.position.longitude)
            }
            cameraPositionState.position = CameraPosition.fromLatLngZoom(deviceLatLng, 18f)
            myViewModel.changeMapaInicial()
        } else {

            Log.e("Error", "Exception: %s", task.exception)

        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true, isBuildingEnabled = true),
            onMapClick = {},
            onMapLongClick = {
                myViewModel.positionChange(it)
                showBottomSheet = true

            }
        ) {
            if (showBottomSheet) {
                ModalBottomSheet(onDismissRequest = {!showBottomSheet}, sheetState = sheetState) {
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(text = "Nom del marcador:")
                        TextField(
                            value = myText,
                            onValueChange = { myText = it }
                        )
                        Text(text = "Descripció")
                        TextField(
                            value = myDescription,
                            onValueChange = {myDescription = it}
                        )
                        myDropDownMenu(myViewModel = myViewModel)
                        Button(onClick = {
                            myViewModel.changeComingFromMap(true)
                            navigationController.navigate(Routes.TakePhotoScreen.route) }) {
                        }
                        Button(onClick = {
                            myViewModel.markerAddition(MarkerData(myText, myMarker.position, myDescription, placeType, newMarkerPhotos))
                            showBottomSheet = false
                            myText = ""
                        }) {
                            Text(text = "Crear marcador")
                        }
                    }

                }
            }
            for (i in llistaMarkers){
                Marker(
                    state = MarkerState(position = i.position),
                    title = i.title,
                    snippet = i.description
                )
            }

            if (showDeletionBottomSheet) {
                ModalBottomSheet(onDismissRequest = {!showBottomSheet}, sheetState = sheetState) {
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(text = "Vols eliminar el marcador?")
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Button(onClick = {
                                myViewModel.markerDeletion(MarkerData(myText, myMarker.position, myDescription, placeType, mutableListOf()))
                                showDeletionBottomSheet = false
                            }) {
                                Text(text = "Si")
                            }
                            Button(onClick = {
                                showDeletionBottomSheet = false
                            }) {
                                Text(text = "No")
                            }
                        }

                    }

                }
            }
        }
    }
}


