package com.example.mapsapp.Firebase

import android.net.Uri
import android.util.Log
import com.example.mapsapp.Firebase.FirebaseModels.MarkerData
import com.example.mapsapp.Firebase.FirebaseModels.User
import com.example.mapsapp.viewmodel.MyViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class FirebaseRepository {

    private val database = FirebaseFirestore.getInstance()
    fun addUser(user: User) {
        database.collection("users")
            .add(
                hashMapOf(
                    "userName" to user.userName,
                    "age" to user.age,
                    "profilePicture" to user.profilePicture
                )
            )
    }

    fun editUser(editedUser: User) {
        database.collection("users").document(editedUser.userId!!).set(
            hashMapOf(
                "userName" to editedUser.userName,
                "age" to editedUser.age,
                "profilePicture" to editedUser.profilePicture
            )
        )
    }

    fun deleteUser(userId: String) {
        database.collection("users").document(userId).delete()
    }

    fun getUsers(): CollectionReference {
        return database.collection("users")
    }

    fun getUser(userId: String): DocumentReference {
        return database.collection("users").document(userId)
    }






    //Markers

    fun addMarker(marker: MarkerData) {
        database.collection("markers")
            .add(
                hashMapOf(
                    "title" to marker.title,
                    "position" to marker.position,
                    "description" to marker.description,
                    "type" to marker.type,
                    "images" to marker.images,
                    "userId" to marker.userId
                )
            )
    }

    fun getMarkers(): CollectionReference {
        return database.collection("markers")
    }

    fun deleteMarker(markerId: String) {
        database.collection("markers").document(markerId).delete()
    }
}


