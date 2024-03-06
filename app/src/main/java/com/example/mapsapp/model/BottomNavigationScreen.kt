package com.example.mapsapp.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mapsapp.navigation.Routes

sealed class BottomNavigationScreen (val route: String, val icon: ImageVector, val label: String) {
    object Home:BottomNavigationScreen(Routes.MapScreen.route, Icons.Filled.Info, "Map")
    object Favorite:BottomNavigationScreen(Routes.MarkerListScreen.route, Icons.Filled.Place, "My Markers")
}