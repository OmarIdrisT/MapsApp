package com.example.mapsapp.view

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.google.android.gms.maps.model.LatLng


@Composable
fun MarkerListSCreen(myViewModel: MyViewModel, navController: NavController) {
    myViewModel.updateFilteredList()
    val llistaMarkersFiltrada:MutableList<MarkerData> by myViewModel.filteredMarkerList.observeAsState(mutableListOf(
        MarkerData("", "","ITB",(LatLng(41.4534265, 2.1837151))," ", "", mutableListOf())
    ))
    if (llistaMarkersFiltrada.isNotEmpty()) {
        LazyColumn() {
            items(llistaMarkersFiltrada.size) {
                CardItem(marker = llistaMarkersFiltrada[it], navController, myViewModel)
            }
        }
    }
    else {
        Column(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "There are no markers yet", color = Color.White)
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
            colors = CardDefaults.cardColors(Color.DarkGray.copy(alpha = 0.6f)),
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
                        contentDescription = "Character Image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(65.dp)
                    )
                    Text(
                        text = "${marker.title} \n${marker.type}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 20.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center, modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    )
                    IconButton(
                        modifier = Modifier.background(Color.Blue),
                        onClick =  {
                        myViewModel.deleteMarker(marker) }) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete", tint = Color.White)
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun DismissBackground(dismissState: DismissState) {
    val color = when (dismissState.dismissDirection) {
        DismissDirection.StartToEnd -> Color(0xFFFF1744)
        DismissDirection.EndToStart -> Color(0xFF1DE9B6)
        null -> Color.Transparent
    }
    val direction = dismissState.dismissDirection

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(12.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (direction == DismissDirection.StartToEnd) Icon(
            Icons.Default.Delete,
            contentDescription = "Delete"
        )
        Spacer(modifier = Modifier)
        if (direction == DismissDirection.EndToStart) Icon(
            painter = painterResource(R.drawable.empty_image),
            contentDescription = "Archive"
        )
    }
}