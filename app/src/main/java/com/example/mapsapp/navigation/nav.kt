package com.example.mapsapp.navigation

sealed class Routes(val route: String) {
    object SplashScreen: Routes("SplashScreen")
    object LoginScreen:Routes("LoginScreen")
    object MapScreen:Routes("MapScreen")
    object MarkerListScreen:Routes("MarkerListScreen")
    object DetailScreen:Routes("DetailScreen")
    object CameraScreen:Routes("CameraScreen")
}