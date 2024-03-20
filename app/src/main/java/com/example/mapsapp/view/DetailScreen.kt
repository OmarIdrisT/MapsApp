package com.example.mapsapp.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mapsapp.model.MarkerData
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.viewmodel.MyViewModel
import com.google.android.gms.maps.model.LatLng


@Composable
fun DetailScreen(navigationController: NavController, myViewModel: MyViewModel) {
    val myMarker: MarkerData by myViewModel.marker.observeAsState(MarkerData("ITB",(LatLng(41.4534265, 2.1837151)),"", "", mutableListOf()))

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
            Text(text = myMarker.title, color = Color.White)
        }
        item {
            Text(text = "${myMarker.position}", color = Color.White)
        }
        item {
            Text(text = myMarker.description, color = Color.White)
        }
        item {
            Text(text = myMarker.type, color = Color.White)
        }
        item {
            LazyRow {
                items(myMarker.images.size + 1) { index ->
                    if (index < myMarker.images.size) {
                        ImageItem(myMarker.images[index], myViewModel)
                    } else {
                        Button(
                            onClick = {
                                navigationController.navigate(Routes.TakePhotoScreen.route)
                            },
                            modifier = Modifier.size(100.dp)
                        ) {
                            Text("+", color = Color.White, fontSize = 40.sp)
                        }
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