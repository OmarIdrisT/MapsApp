package com.example.mapsapp.Firebase

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.mapsapp.Firebase.FirebaseModels.MarkerData
import com.example.mapsapp.Firebase.FirebaseModels.User
import com.example.mapsapp.viewmodel.MyViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class FirebaseRepository {

    private val database = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _goToNext = MutableLiveData<Boolean>()
    private val _userId = MutableLiveData<String>()
    private val _loggedUser = MutableLiveData<String>()


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


    //Authentication

    fun register (username: String, password: String) {
        auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    _goToNext.value = true
                } else {
                    _goToNext.value = false
                    Log.d("Error", "Error creating user: ${task.result}")
                }
            }
    }

    fun login(username: String?, password: String?) {
        auth.signInWithEmailAndPassword(username!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _userId.value = task.result.user?.uid
                    _loggedUser.value = task.result.user?.email?.split("@")?.get(0)
                    _goToNext.value = true
                } else {
                    _goToNext.value = false
                    Log.d("Error", "Error signing in: ${task.result}")
                }
            }
    }
}


