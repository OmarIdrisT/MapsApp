package com.example.mapsapp.Firebase.FirebaseModels

import com.google.android.gms.maps.model.LatLng

data class MarkerData(
    val userId: String,
    var markerId: String?,
    val title: String,
    var position: LatLng,
    val description: String,
    val type: String,
    val images: MutableList<String> )