package com.example.mapsapp.model

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng

data class MarkerData(val title: String, var position: LatLng, val description: String, val type: String, val images: MutableList<Bitmap> )