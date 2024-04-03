package com.example.mapsapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mapsapp.MainActivity
import com.example.mapsapp.MyCamera
import com.example.mapsapp.R
import com.example.mapsapp.model.MarkerData
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
    val actualMarker: MarkerData by myViewModel.actualMarker.observeAsState(MarkerData("ITB",(LatLng(41.4534265, 2.1837151))," ", "", mutableListOf()))
    val llistaMarkers:MutableList<MarkerData> by myViewModel.markerList.observeAsState(mutableListOf(MarkerData("ITB",(LatLng(41.4534265, 2.1837151))," ", "", mutableListOf())))

    val newMarkerPhotos: MutableList<Bitmap> by myViewModel.newMarkerPhotos.observeAsState(mutableListOf())

    val showNewMarkerBottomSheet by myViewModel.showNewMarkerBottomSheet.observeAsState(false)
    val showMarkerOptionsBottomSheet by myViewModel.showMarkerOptionsBottomSheet.observeAsState(false)

    val navBackStackEntry by navigationController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val myTitle: String by myViewModel.markerTitle.observeAsState("")
    val myDescription: String by myViewModel.markerDescription.observeAsState("")
    val placeType: String by myViewModel.placeType.observeAsState("Sense especificar")

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
                    sheetState = sheetState,
                    modifier = Modifier.zIndex(500f),
                    containerColor = Color.Black,
                    contentColor = Color.White) {
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(text = "Nom del marcador:")
                        TextField(
                            modifier = Modifier.background(Color.White),
                            value = myTitle,
                            onValueChange = { myViewModel.changeTitle(it) }
                        )
                        Spacer(Modifier.height(15.dp))
                        Text(text = "Descripció")
                        TextField(
                            modifier = Modifier.background(Color.White),
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
                        Log.i("estado", sheetState.currentValue.name)

                        Button(onClick = {
                            myViewModel.markerAddition(MarkerData(myTitle, myMarker.position, myDescription, placeType, newMarkerPhotos))
                            myViewModel.placeTypeIconChange(placeType)
                            myViewModel.setNewMarkerBottomSheet(false)
                            myViewModel.changeTitle("")
                            myViewModel.changeDescription("")
                            myViewModel.placeTypeChange("Sense especificar")
                            myViewModel.clearPhotosFromNewMarker()
                        }) {
                            Text(text = "Crear marcador")
                        }
                    }

                }
            }
            for (i in llistaMarkers){
                val icon = createBitmapDescriptor(context, myViewModel.placeTypeIconChange(i.type))
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
                    icon = icon

                )
            }

            if (showMarkerOptionsBottomSheet) {
                ModalBottomSheet(onDismissRequest = {myViewModel.setMarkerOptionsBottomSheet(false)}, sheetState = sheetState, containerColor = Color.Black, contentColor = Color.White) {
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun myDropDownMenu(myViewModel: MyViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val placeType: String by myViewModel.placeType.observeAsState("Sense especificar")
    val opcions = listOf("Sense especificar", "Cafeteria", "Restaurant", "Entreteniment", "Botiga", "Transport")

    Column (modifier = Modifier.padding(20.dp)) {
        OutlinedTextField(
            value = placeType,
            onValueChange = { myViewModel.placeTypeChange(it) },
            enabled = false,
            readOnly = true,
            textStyle = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center),
            modifier = Modifier
                .clickable { expanded = true }
                .fillMaxWidth(0.6f)
                .height(60.dp)
                .background(color = Color.White)
                .align(alignment = Alignment.CenterHorizontally)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            opcions.forEach { type ->
                DropdownMenuItem(modifier = Modifier.background(color = Color.Black) ,text = { Text(text = type, style = TextStyle(color = Color.White)) }, onClick = {
                    expanded = false
                    myViewModel.placeTypeChange(type)
                })
            }
        }
    }
}


fun createBitmapDescriptor (context: Context, drawableId: Int) : BitmapDescriptor {

    val bitmap = BitmapFactory.decodeResource(context.resources, drawableId)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}


