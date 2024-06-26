package com.example.mapsapp.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mapsapp.MainActivity
import com.example.mapsapp.PermissionDeclinedScreen
import com.example.mapsapp.firebase.firebasemodels.MarkerData
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
import java.util.UUID

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(myViewModel: MyViewModel, navigationController: NavController) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val actualMarker: MarkerData by myViewModel.actualMarker.observeAsState(MarkerData("","","ITB",(LatLng(41.4534265, 2.1837151))," ", "", mutableListOf()))
    val llistaMarkers:MutableList<MarkerData> by myViewModel.markerList.observeAsState(mutableListOf())
    val markPosition: LatLng by myViewModel.markPosition.observeAsState(LatLng(41.4534265, 2.1837151))
    val newMarkerPhotos: MutableList<String> by myViewModel.newMarkerPhotos.observeAsState(mutableListOf())

    val showNewMarkerBottomSheet by myViewModel.showNewMarkerBottomSheet.observeAsState(false)
    val showMarkerOptionsBottomSheet by myViewModel.showMarkerOptionsBottomSheet.observeAsState(false)

    val navBackStackEntry by navigationController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val myTitle: String by myViewModel.markerTitle.observeAsState("")
    val myDescription: String by myViewModel.markerDescription.observeAsState("")
    val placeType: String by myViewModel.placeType.observeAsState("Not specified")

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
            if (mapaInicial || llistaMarkers.size == 0) {
                deviceLatLng = LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
            }
            else {
                if (actualMarker.position != LatLng(41.4534265, 2.1837151)) {
                    deviceLatLng = LatLng(actualMarker.position.latitude, actualMarker.position.longitude)
                }
                else {
                    deviceLatLng = LatLng(markPosition.latitude, markPosition.longitude)
                }

            }
            cameraPositionState.position = CameraPosition.fromLatLngZoom(deviceLatLng, 18f)
            myViewModel.changeMapaInicial(false)
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
                if (currentRoute == Routes.MapScreen.route) {
                    myViewModel.positionChange(it)
                    myViewModel.setNewMarkerBottomSheet(true)
                }
            }
        ) {
            if (showNewMarkerBottomSheet) {
                ModalBottomSheet(onDismissRequest = {
                    myViewModel.clearPhotosFromNewMarker()
                    myViewModel.changeTitle("")
                    myViewModel.changeDescription("")
                    myViewModel.placeTypeChange("Not specified")
                    myViewModel.setNewMarkerBottomSheet(false)},
                    sheetState = sheetState,
                    modifier = Modifier.zIndex(500f),
                    containerColor = Color.Black,
                    contentColor = Color.White) {
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        OutlinedTextField(
                            value = myTitle,
                            onValueChange = { myViewModel.changeTitle(it) },
                            label = {
                                androidx.compose.material.Text(
                                    text = "Title",
                                    color = Color.White,
                                    fontFamily = myViewModel.brownista,
                                    fontSize = 20.sp
                                )
                            },
                            placeholder = {
                                androidx.compose.material.Text(
                                    text = "MyMarker",
                                    color = Color.LightGray,
                                    fontFamily = myViewModel.brownista,
                                    fontSize = 20.sp
                                )
                            },
                            textStyle = TextStyle(fontFamily = myViewModel.brownista, fontSize = 20.sp),
                            leadingIcon = { Icon(imageVector = Icons.Default.Title, contentDescription = null) },
                            colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(
                                unfocusedTextColor = Color.White,
                                focusedTextColor = Color.White,
                                unfocusedBorderColor = Color.White,
                                containerColor = Color.Black,
                                focusedBorderColor = Color.Green,
                                unfocusedLeadingIconColor = Color.White,
                                focusedLeadingIconColor = Color.Green,
                                focusedLabelColor = Color.Green
                            )
                        )
                        Spacer(Modifier.height(15.dp))
                        OutlinedTextField(
                            value = myDescription,
                            onValueChange = { myViewModel.changeDescription(it) },
                            label = {
                                androidx.compose.material.Text(
                                    text = "Description",
                                    color = Color.White,
                                    fontFamily = myViewModel.brownista,
                                    fontSize = 20.sp
                                )
                            },
                            placeholder = {
                                androidx.compose.material.Text(
                                    text = "MyDescription",
                                    color = Color.LightGray,
                                    fontFamily = myViewModel.brownista,
                                    fontSize = 20.sp
                                )
                            },
                            textStyle = TextStyle(fontFamily = myViewModel.brownista, fontSize = 20.sp),
                            leadingIcon = { Icon(imageVector = Icons.Default.Description, contentDescription = null) },
                            colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(
                                unfocusedTextColor = Color.White,
                                focusedTextColor = Color.White,
                                unfocusedBorderColor = Color.White,
                                containerColor = Color.Black,
                                focusedBorderColor = Color.Green,
                                unfocusedLeadingIconColor = Color.White,
                                focusedLeadingIconColor = Color.Green,
                                focusedLabelColor = Color.Green
                            )
                        )
                        creationDropDownMenu(myViewModel = myViewModel)
                        MyCameraFromMap(myViewModel = myViewModel, navigationController = navigationController)
                        if (newMarkerPhotos.size >= 1) {
                            LazyRow (modifier = Modifier.fillMaxHeight(0.3f)){
                                items(newMarkerPhotos.size) { index ->
                                    if (index < newMarkerPhotos.size) {
                                        ImageItem(newMarkerPhotos[index], myViewModel)
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(15.dp))
                        OutlinedButton(modifier = Modifier.background(Color.Transparent), border = BorderStroke(1.dp, Color.White), onClick = {
                            val id= UUID.randomUUID().toString()
                            val newMarker = MarkerData(myViewModel.userId.value!!,id, myTitle, markPosition, myDescription, placeType, mutableListOf())
                            newMarker.images.addAll(newMarkerPhotos)
                            myViewModel.addMarkerToFirebase(newMarker)
                            myViewModel.placeTypeIconChange(placeType)
                            myViewModel.clearPhotosFromNewMarker()
                            myViewModel.setNewMarkerBottomSheet(false)
                            myViewModel.changeTitle("")
                            myViewModel.changeDescription("")
                            myViewModel.placeTypeChange("Not specified")
                        }) {
                            Text(text = "Create marker", color = Color.White, fontFamily = myViewModel.brownista, fontSize = 20.sp)
                        }
                    }

                }
            }
            myViewModel.getMarkers()
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
                        OutlinedButton(modifier = Modifier.background(Color.Transparent), border = BorderStroke(1.dp, Color.White), onClick = {
                            myViewModel.setMarkerOptionsBottomSheet(false)
                            navigationController.navigate(Routes.DetailScreen.route)
                        }) {
                            Text(text = "Marker Details", color = Color.White, fontFamily = myViewModel.brownista, fontSize = 20.sp)
                        }
                        Spacer(Modifier.height(15.dp))
                        OutlinedButton(modifier = Modifier.background(Color.Transparent), border = BorderStroke(1.dp, Color.White), onClick = {
                            navigationController.navigate(Routes.EditMarkerScreen.route)
                        }) {
                            Text(text = "Edit marker", color = Color.White, fontFamily = myViewModel.brownista, fontSize = 20.sp)
                        }
                        Spacer(Modifier.height(15.dp))
                        OutlinedButton(modifier = Modifier.background(Color.Transparent), border = BorderStroke(1.dp, Color.White), onClick = {
                            myViewModel.deleteMarker(actualMarker)
                            myViewModel.setMarkerOptionsBottomSheet(false)
                        }) {
                            Text(text = "Delete marker", color = Color.White, fontFamily = myViewModel.brownista, fontSize = 20.sp)
                        }
                    }

                }
            }
        }
    }
}


@Composable
fun creationDropDownMenu(myViewModel: MyViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val placeType: String by myViewModel.placeType.observeAsState("Not specified")
    val opcions = listOf("Not specified", "Cafe", "Restaurant", "Entertainment", "Shop", "Transport")

    Column (modifier = Modifier.padding(20.dp)) {
        OutlinedTextField(
            value = placeType,
            onValueChange = { myViewModel.placeTypeChange(it) },
            enabled = false,
            readOnly = true,
            textStyle = TextStyle(color = Color.White, fontSize = 25.sp, textAlign = TextAlign.Center, fontFamily = myViewModel.brownista),
            modifier = Modifier
                .clickable { expanded = true }
                .fillMaxWidth(0.6f)
                .border(BorderStroke(1.dp, Color.White))
                .height(60.dp)
                .background(color = Color.Black)
                .align(alignment = Alignment.CenterHorizontally)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            opcions.forEach { type ->
                DropdownMenuItem(modifier = Modifier.background(color = Color.Black) ,text = { Text(text = type, style = TextStyle(color = Color.White, fontFamily = myViewModel.brownista)) }, onClick = {
                    expanded = false
                    myViewModel.placeTypeChange(type)
                })
            }
        }
    }
}


@Composable
fun MyCameraFromMap(navigationController: NavController, myViewModel: MyViewModel) {
    val context = LocalContext.current
    val isCameraPermissionGranted by myViewModel.cameraPermissionGranted.observeAsState(false)
    val shouldShowPermissionRationale by myViewModel.shouldShowPermissionRationale.observeAsState(false)
    val showPermissionDenied by myViewModel.showPermissionDenied.observeAsState(false)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                myViewModel.setCameraPermissionGranted(true)
                navigationController.navigate(Routes.TakePhotoScreen.route)
            } else {
                myViewModel.setShouldShowPermissionRationale(
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity,
                        Manifest.permission.CAMERA
                    )
                )
                if (!shouldShowPermissionRationale) {
                    myViewModel.setShowPermissionDenied(true)
                }
            }
        }
    )

    OutlinedButton(modifier = Modifier.background(Color.Transparent), border = BorderStroke(1.dp, Color.White), onClick = {
        if (!isCameraPermissionGranted) {
            launcher.launch(Manifest.permission.CAMERA)
        } else {
            myViewModel.changeComingFromMap(true)
            navigationController.navigate(Routes.TakePhotoScreen.route)
        }
    }) {
        Text(text = "Take photo", color = Color.White, fontFamily = myViewModel.brownista, fontSize = 20.sp)
    }
    if(showPermissionDenied) {
        PermissionDeclinedScreen()
    }
}

fun createBitmapDescriptor (context: Context, drawableId: Int) : BitmapDescriptor {

    val bitmap = BitmapFactory.decodeResource(context.resources, drawableId)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}


