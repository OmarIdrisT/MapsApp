package com.example.mapsapp.view

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mapsapp.R
import com.example.mapsapp.firebase.firebasemodels.MarkerData
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.viewmodel.MyViewModel


@Composable
fun MarkerListSCreen(myViewModel: MyViewModel, navController: NavController) {
    val llistaMarkersFiltrada:MutableList<MarkerData> by myViewModel.filteredMarkerList.observeAsState(mutableListOf())
    Image(
        painter = painterResource(id = R.drawable.fondo),
        contentDescription = "",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds
    )
    if (llistaMarkersFiltrada.isNotEmpty()) {
        LazyColumn() {
            items(llistaMarkersFiltrada.size) {
                CardItem(marker = llistaMarkersFiltrada[it], navController, myViewModel)
            }
        }
    }
    else {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "There are no markers yet", color = Color.White, fontFamily = myViewModel.brownista, fontSize = 30.sp, textAlign = TextAlign.Center)
        }
    }

}
@OptIn(ExperimentalGlideComposeApi::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun CardItem(marker: MarkerData, navController: NavController, myViewModel: MyViewModel) {
    Box(modifier = Modifier
        .padding(8.dp)
        .fillMaxSize(),
        contentAlignment = Alignment.Center){
        Card (
            border = BorderStroke(2.dp, Color.LightGray),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(8.dp),
            colors = CardDefaults.cardColors(Color.Black.copy(alpha = 0.3f)),
            onClick = {
                myViewModel.chooseMarker(marker)
                navController.navigate(Routes.DetailScreen.route)
            }
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlideImage(
                        model = myViewModel.placeTypeIconChange(marker.type),
                        contentDescription = "Marker Icon",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(65.dp)
                    )
                    Text(
                        text = "${marker.title}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 30.sp,
                        fontFamily = myViewModel.brownista,
                        color = Color.White,
                        textAlign = TextAlign.Center, modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    )
                    IconButton(
                        modifier = Modifier.background(Color.Transparent),
                        onClick =  {
                        myViewModel.deleteMarker(marker)}) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete", tint = Color.White)
                    }
                }

            }
        }
    }
}

