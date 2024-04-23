package com.example.mapsapp.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.mapsapp.firebase.firebasemodels.MarkerData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class FirebaseRepository {

    private val database = FirebaseFirestore.getInstance()
    private val _auth = FirebaseAuth.getInstance()
    val auth = _auth



    fun getUserFromDatabase(userId: String): DocumentReference {
        return database.collection("users").document(userId)
    }


    //Markers

    fun addMarker(marker: MarkerData) {
        database.collection("markers")
            .add(
                hashMapOf(
                    "title" to marker.title,
                    "position" to hashMapOf(
                        "latitude" to marker.position.latitude,
                        "longitude" to marker.position.longitude
                    ),
                    "description" to marker.description,
                    "type" to marker.type,
                    "images" to marker.images,
                    "userId" to marker.userId,
                    "id" to marker.id
                )
            )
    }

    fun getMarkers(): CollectionReference {
        return database.collection("markers")
    }

    fun deleteMarker(marker: MarkerData){
        val query = database.collection("markers").whereEqualTo("id", marker.id)
        Log.i("marker", marker.id.toString())
        query.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val markerRef = database.collection("markers").document(document.id)
                    markerRef.delete()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Error", "Error getting documents: ", exception)
            }
    }

    fun editMarker(marker: MarkerData){
        database.collection("markers")
            .whereEqualTo("id",marker.id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    database.collection("markers").document(document.id)
                        .update(hashMapOf(
                            "id" to marker.id,
                            "title" to marker.title,
                            "position" to hashMapOf(
                                "latitude" to marker.position.latitude,
                                "longitude" to marker.position.longitude
                            ),
                            "descripcion" to marker.description,
                            "tipo" to marker.type,
                            "imagenes" to marker.images
                        ))
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Error", "Error getting documents: ", exception)
            }
    }


    //Authentication








}


