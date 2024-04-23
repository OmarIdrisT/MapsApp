package com.example.mapsapp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mapsapp.viewmodel.MyViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMarkerScreen(navigationController: NavController, myViewModel: MyViewModel) {
    val actualMarker by myViewModel.actualMarker.observeAsState()
    var expanded by remember { mutableStateOf(false) }
    val opcions = listOf("Not especified", "Cafe", "Restaurant", "Entertainment", "Shop", "Transport")
    val cameraPositionState = rememberCameraPositionState {
        if (actualMarker != null) {
            position = CameraPosition.fromLatLngZoom(actualMarker!!.position, 15f)
        }
    }
    if (actualMarker != null) {
        var newTitle by remember {mutableStateOf(actualMarker!!.title)}
        var newDescription by remember { mutableStateOf(actualMarker!!.description) }
        var newType by remember { mutableStateOf(actualMarker!!.type) }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxSize()
        ) {
            OutlinedTextField(
                value = newTitle,
                onValueChange = { newTitle = it },
                label = { Text("User") },
                placeholder = { Text(text = "example@gmail.com", color = Color.LightGray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedTextColor = Color.Red,
                    unfocusedBorderColor = Color.Red,
                    containerColor = Color.White,
                    focusedBorderColor = Color.Green,
                    unfocusedLeadingIconColor = Color.Red,
                    focusedLeadingIconColor = Color.Green.copy(alpha = 0.4f),
                    focusedLabelColor = Color.Green
                )
            )
            OutlinedTextField(
                value = newDescription,
                onValueChange = { newDescription = it },
                label = { Text("New description") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = Color.Red,
                    containerColor = Color.White,
                    focusedBorderColor = Color.Green,
                    focusedLeadingIconColor = Color.Red,
                    focusedLabelColor = Color.Red
                )
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                opcions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option) },
                        onClick = {
                            expanded = false
                            newType = option
                        },
                        modifier = Modifier.width(200.dp)
                    )
                }
            }
            Button(onClick = {
                myViewModel.editMarker(newTitle, newDescription, newType)
                navigationController.navigateUp()
            }) {

            }
        }
    }
}

