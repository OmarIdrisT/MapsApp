package com.example.mapsapp.view

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
import androidx.navigation.NavController
import com.example.mapsapp.model.MarkerData
import com.example.mapsapp.myDropDownMenu
import com.example.mapsapp.viewmodel.MyViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(navigationController: NavController, myViewModel: MyViewModel) {


}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyMap(myViewModel: MyViewModel, navigationController: NavController) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val myMarker: MarkerData by myViewModel.marker.observeAsState(MarkerData("ITB",(LatLng(41.4534265, 2.1837151))," "))
    val llistaMarkers:MutableList<MarkerData> by myViewModel.markerList.observeAsState(mutableListOf(MarkerData("ITB",(LatLng(41.4534265, 2.1837151))," ")))
    var posicioNewMarker by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    var showDeletionBottomSheet by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var myText by remember{ mutableStateOf("") }
    var mySnippet by remember { mutableStateOf("Marker at $myText") }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val itb = LatLng(41.4534265, 2.1837151)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(myMarker.position, 10f)
        }
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
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
                        myDropDownMenu(myViewModel = myViewModel)
                        Button(onClick = {
                            myViewModel.markerAddition(MarkerData(myText, myMarker.position, mySnippet))
                            showBottomSheet = false
                            myText = ""
                        }) {
                            Text(text = "Crear marcador")
                        }
                    }

                }
            }
            for (i in llistaMarkers){
                println(i)
                Marker(
                    state = MarkerState(position = i.position),
                    title = i.title,
                    snippet = i.snippet
                )
            }

            Marker(
                onClick = {showDeletionBottomSheet},
                state = MarkerState(position = itb),
                title = "ITB",
                snippet = "Marker at ITB"
            )
            if (showDeletionBottomSheet) {
                ModalBottomSheet(onDismissRequest = {!showBottomSheet}, sheetState = sheetState) {
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(text = "Vols eliminar el marcador?")
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Button(onClick = {
                                myViewModel.markerDeletion(MarkerData(myText, myMarker.position, mySnippet))
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


