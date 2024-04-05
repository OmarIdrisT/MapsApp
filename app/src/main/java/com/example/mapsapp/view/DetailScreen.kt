package com.example.mapsapp.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mapsapp.PermissionDeclinedScreen
import com.example.mapsapp.model.MarkerData
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.viewmodel.MyViewModel
import com.google.android.gms.maps.model.LatLng


@Composable
fun DetailScreen(navigationController: NavController, myViewModel: MyViewModel) {
    val myMarker: MarkerData by myViewModel.actualMarker.observeAsState(MarkerData("ITB",(LatLng(41.4534265, 2.1837151)),"", "", mutableListOf()))
    Log.i("objeto detall", myMarker.images.toString())
    LazyColumn (
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        item {
            Card (modifier = Modifier
                .size(200.dp)
                .border(BorderStroke(2.dp, Color.Transparent))){
                MapScreen(myViewModel = myViewModel, navigationController = navigationController )
            }
        }
        item {
            Text(
                text = myMarker.title,
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            Text(text = myMarker.type,
                color = Color.White,
                fontSize = 15.sp,
            )
        }
        item {
            Text(
                text = myMarker.description,
                color = Color.White
            )
        }

        item {
            LazyRow {
                items(myMarker.images.size + 1) { index ->
                    if (index < myMarker.images.size) {
                        ImageItem(myMarker.images[index], myViewModel)
                    } else {
                        MyCameraFromDetails(navigationController, myViewModel)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ImageItem(markerPhoto: Bitmap, myViewModel: MyViewModel) {
    Box(modifier = Modifier
        .padding(8.dp)
        .fillMaxSize(0.9f),
        contentAlignment = Alignment.Center){
        Card (
            border = BorderStroke(2.dp, Color.LightGray),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(8.dp),
            colors = CardDefaults.cardColors(Color.DarkGray.copy(alpha = 0.6f)),
            onClick = {}
        ) {
            GlideImage(
                model = markerPhoto,
                contentDescription = "Character Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(100.dp)
            )
        }
    }
}


@Composable
fun MyCameraFromDetails(navigationController: NavController, myViewModel: MyViewModel) {
    val context = LocalContext.current
    val isCameraPermissionGranted by myViewModel.cameraPermissionGranted.observeAsState(false)
    val shouldShowPermissionRationale by myViewModel.shouldShowPermissionRationale.observeAsState(false)
    val showPermissionDenied by myViewModel.showPermissionDenied.observeAsState(false)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                myViewModel.setCameraPermissionGranted(true)
            } else {
                myViewModel.setShouldShowPermissionRationale(
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity,
                        Manifest.permission.CAMERA
                    )
                )
                if (!shouldShowPermissionRationale) {
                    Log.i("CameraScreen", "No podemos volver a pedir permisos")
                    myViewModel.setShowPermissionDenied(true)
                }
            }
        }
    )
    Box(
        modifier = Modifier
            .size(100.dp)
            .clickable {
                if (!isCameraPermissionGranted) {
                    launcher.launch(Manifest.permission.CAMERA)
                } else {
                    myViewModel.changeComingFromMap(false)
                    navigationController.navigate(Routes.TakePhotoScreen.route)
                }
            },
        contentAlignment = Alignment.Center
        ) {
         Text(text = "+", color = Color.White)
        }
    if(showPermissionDenied) {
        PermissionDeclinedScreen()
    }
}