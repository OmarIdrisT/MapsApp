package com.example.mapsapp.navigation

sealed class Routes(val route: String) {
    object LoginScreen:Routes("LoginScreen")
    object MapScreen:Routes("MapScreen")
    object MarkerListScreen:Routes("MarkerListScreen")
    object DetailScreen:Routes("DetailScreen")
    object TakePhotoScreen:Routes("TakePhotoScreen")
}