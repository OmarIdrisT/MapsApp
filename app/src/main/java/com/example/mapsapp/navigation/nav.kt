package com.example.mapsapp.navigation

sealed class Routes(val route: String) {
    object SplashScreen: Routes("SplashScreen")
    object LoginScreen:Routes("LoginScreen")
    object MapScreen:Routes("MapScreen")
    object PinsScreen:Routes("PinsScreen")
}