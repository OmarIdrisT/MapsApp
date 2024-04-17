package com.example.mapsapp.firebase.firebasemodels

import com.google.android.gms.maps.model.LatLng

class MarkerData {
    var userId: String = ""
    var markerId: String? = ""
    var title: String = ""
    var position: LatLng = LatLng(0.0,0.0)
    var description: String = ""
    var type: String = ""
    var images: MutableList<String> = mutableListOf()
    constructor()

    constructor(userId: String, markerId: String?, title: String, position: LatLng, description: String, type: String, images: MutableList<String>) {
        this.userId = userId
        this.markerId = markerId
        this.title = title
        this.position = position
        this.description = description
        this.type = type
        this.images = images
    }
}
