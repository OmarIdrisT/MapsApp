package com.example.mapsapp.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.mapsapp.firebase.firebasemodels.MarkerData
import com.example.mapsapp.firebase.firebasemodels.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class FirebaseRepository {

    private val database = FirebaseFirestore.getInstance()
    private val _auth = FirebaseAuth.getInstance()
    val auth = _auth
    private val _goToNext = MutableLiveData<Boolean>()
    val goToNext = _goToNext
    private val _userId = MutableLiveData<String>()
    val userId = _userId
    private val _loggedUser = MutableLiveData<String>()
    val loggedUser = _loggedUser
    private val _loginFail = MutableLiveData<Boolean>()
    val loginFail = _loginFail
    private val _registerFail = MutableLiveData<Boolean>()
    val registerFail = _registerFail


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
        query.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val markerRef = database.collection("markers").document(marker.id!!)
                    markerRef.delete()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Error", "Error getting documents: ", exception)
            }
    }


    //Authentication

    fun register (username: String, password: String) {
        _auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    _registerFail.value = false
                }
            }
            .addOnFailureListener {
                _registerFail.value = true
            }
    }

    fun updateRegisterFail() {
        _registerFail.value = false
    }



    fun login(username: String?, password: String?) {
        _auth.signInWithEmailAndPassword(username!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginFail.value = false
                    _userId.value = task.result.user?.uid
                    _loggedUser.value = task.result.user?.email?.split("@")?.get(0)
                    _goToNext.value = true
                    Log.i("hola", "")
                }
            }
            .addOnFailureListener {
                _goToNext.value = false
                _loginFail.value = true
            }
    }

    fun updateLoginFail() {
        _loginFail.value = false
    }
}


