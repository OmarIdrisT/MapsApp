package com.example.mapsapp.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.mapsapp.MyDrawer
import com.example.mapsapp.MyScaffold
import com.example.mapsapp.viewmodel.MyViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(navigationController: NavController, myViewModel: MyViewModel) {
    MyDrawer(myViewModel, "Map")

}



@Composable
fun MyMap(navigationController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val itb = LatLng(41.4534265, 2.1837151)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(itb, 10f)
        }
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(compassEnabled = true, indoorLevelPickerEnabled = true, rotationGesturesEnabled = true, zoomControlsEnabled = true),
            //properties = MapProperties(mapType = MapType.HYBRID),
            onMapClick = {},
            onMapLongClick = {
            }
        ) {
            Marker(
                state = MarkerState(position = itb),
                title = "ITB",
                snippet = "Marker at ITB"
            )
        }

    }
}


