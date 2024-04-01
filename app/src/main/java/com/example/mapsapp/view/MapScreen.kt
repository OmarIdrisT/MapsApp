package com.example.mapsapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mapsapp.MainActivity
import com.example.mapsapp.MyCamera
import com.example.mapsapp.model.MarkerData
import com.example.mapsapp.myDropDownMenu
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.viewmodel.MyViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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
    val showNewMarkerBottomSheet by myViewModel.showNewMarkerBottomSheet.observeAsState(false)
    val llistaMarkers:MutableList<MarkerData> by myViewModel.markerList.observeAsState(mutableListOf(MarkerData("ITB",(LatLng(41.4534265, 2.1837151))," ", "", mutableListOf())))
    var placeType: String by remember { mutableStateOf(myViewModel.placeType) }
    var placeTypeIcon by remember { mutableStateOf(myViewModel.placeTypeIcon) }

    val newMarkerPhotos: MutableList<Bitmap> by myViewModel.newMarkerPhotos.observeAsState(mutableListOf())

    val showMarkerOptionsBottomSheet by myViewModel.showMarkerOptionsBottomSheet.observeAsState(false)

    val navBackStackEntry by navigationController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val myTitle: String by myViewModel.markerTitle.observeAsState("")
    val myDescription: String by myViewModel.markerDescription.observeAsState("")

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
                myViewModel.setNewMarkerBottomSheet(true)

            }
        ) {
            if (showNewMarkerBottomSheet) {
                ModalBottomSheet(onDismissRequest = {
                    myViewModel.clearPhotosFromNewMarker()
                    myViewModel.setNewMarkerBottomSheet(false)},
                    sheetState = sheetState) {
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(text = "Nom del marcador:")
                        TextField(
                            value = myTitle,
                            onValueChange = { myViewModel.changeTitle(it) }
                        )
                        Text(text = "Descripció")
                        TextField(
                            value = myDescription,
                            onValueChange = {myViewModel.changeDescription(it)}
                        )
                        myDropDownMenu(myViewModel = myViewModel)
                        MyCamera(myViewModel = myViewModel, navigationController = navigationController)
                        if (newMarkerPhotos.size >= 1) {
                            LazyRow (modifier = Modifier.fillMaxHeight(0.3f)){
                                items(newMarkerPhotos.size) { index ->
                                    if (index < newMarkerPhotos.size) {
                                        ImageItem(newMarkerPhotos[index], myViewModel)
                                    }
                                }
                            }
                        }

                        Button(onClick = {
                            myViewModel.markerAddition(MarkerData(myTitle, myMarker.position, myDescription, placeType, newMarkerPhotos))
                            myViewModel.setNewMarkerBottomSheet(false)
                            myViewModel.changeTitle("")
                            myViewModel.changeDescription("")
                            myViewModel.placeTypeChange("Sense especificar")
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
                    onInfoWindowClick = {
                        if (currentRoute == Routes.MapScreen.route) {
                            myViewModel.chooseMarker(i)
                            myViewModel.setMarkerOptionsBottomSheet(true)
                        }
                    },
                    snippet = i.description,
                    icon = createBitmapDescriptor(context, placeTypeIcon)

                )
            }

            if (showMarkerOptionsBottomSheet) {
                ModalBottomSheet(onDismissRequest = {myViewModel.setMarkerOptionsBottomSheet(false)}, sheetState = sheetState) {
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Button(onClick = {
                            myViewModel.setMarkerOptionsBottomSheet(false)
                            navigationController.navigate(Routes.DetailScreen.route)
                        }) {
                            Text(text = "Detalls marcador")
                        }
                        Button(onClick = {
                            myViewModel.markerDeletion(myMarker)
                            myViewModel.setMarkerOptionsBottomSheet(false)
                        }) {
                            Text(text = "Eliminar marcador")
                        }
                    }

                }
            }
        }
    }
}


fun createBitmapDescriptor (context: Context, drawableId: Int) : BitmapDescriptor {

    val bitmap = BitmapFactory.decodeResource(context.resources, drawableId)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}


