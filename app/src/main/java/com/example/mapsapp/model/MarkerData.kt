package com.example.mapsapp.model

import com.google.android.gms.maps.model.LatLng

data class MarkerData (val title: String, var position: LatLng, val snippet: String)