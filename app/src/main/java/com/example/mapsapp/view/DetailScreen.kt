package com.example.mapsapp.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mapsapp.model.MarkerData
import com.example.mapsapp.viewmodel.MyViewModel
import com.google.android.gms.maps.model.LatLng


@Composable
fun DetailScreen(navigationController: NavController, myViewModel: MyViewModel) {


}


@Composable
fun MyDetails(navigationController: NavController, myViewModel: MyViewModel) {
    val myMarker: MarkerData by myViewModel.marker.observeAsState(MarkerData("ITB",(LatLng(41.4534265, 2.1837151)),"", "", mutableListOf()))
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Card (modifier = Modifier.fillMaxWidth(0.7f).fillMaxHeight(0.4f).border(BorderStroke(2.dp, Color.Transparent))){
            MyMap(myViewModel = myViewModel, navigationController = navigationController )
        }
        Text(text = myMarker.title)
        Text(text = "${myMarker.position}")
        Text(text = myMarker.description)
        Text(text = myMarker.type)
    }
    LazyRow() {

    }
}