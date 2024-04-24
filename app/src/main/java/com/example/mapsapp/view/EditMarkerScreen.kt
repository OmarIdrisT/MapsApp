package com.example.mapsapp.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mapsapp.viewmodel.MyViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMarkerScreen(navigationController: NavController, myViewModel: MyViewModel) {
    val actualMarker by myViewModel.actualMarker.observeAsState()
    var expanded by remember { mutableStateOf(false) }
    val opcions = listOf("Not specified", "Cafe", "Restaurant", "Entertainment", "Shop", "Transport")
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
                label = {
                    Text(
                        text = "New title",
                        color = Color.White,
                        fontFamily = myViewModel.brownista,
                        fontSize = 20.sp
                    )
                },
                placeholder = {
                    Text(
                        text = "MyMarkerNewTitlw",
                        color = Color.LightGray,
                        fontFamily = myViewModel.brownista,
                        fontSize = 20.sp
                    )
                },
                textStyle = TextStyle(fontFamily = myViewModel.brownista, fontSize = 20.sp),
                leadingIcon = { Icon(imageVector = Icons.Default.Title, contentDescription = null) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    containerColor = Color.Black,
                    focusedBorderColor = Color.Green,
                    unfocusedLeadingIconColor = Color.White,
                    focusedLeadingIconColor = Color.Green,
                    focusedLabelColor = Color.Green
                )
            )
            OutlinedTextField(
                value = newDescription,
                onValueChange = { newDescription = it },
                label = {
                    Text(
                        text = "New description",
                        color = Color.White,
                        fontFamily = myViewModel.brownista,
                        fontSize = 20.sp
                    )
                },
                placeholder = {
                    Text(
                        text = "MyNewDescription",
                        color = Color.LightGray,
                        fontFamily = myViewModel.brownista,
                        fontSize = 20.sp
                    )
                },
                textStyle = TextStyle(fontFamily = myViewModel.brownista, fontSize = 20.sp),
                leadingIcon = { Icon(imageVector = Icons.Default.Description, contentDescription = null) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    containerColor = Color.Black,
                    focusedBorderColor = Color.Green,
                    unfocusedLeadingIconColor = Color.White,
                    focusedLeadingIconColor = Color.Green,
                    focusedLabelColor = Color.Green
                )
            )

            OutlinedTextField(
                value = newType,
                onValueChange = { newType = it },
                enabled = false,
                readOnly = true,
                textStyle = TextStyle(color = Color.White, fontSize = 25.sp, textAlign = TextAlign.Center, fontFamily = myViewModel.brownista),
                modifier = Modifier
                    .clickable { expanded = true }
                    .fillMaxWidth(0.6f)
                    .border(BorderStroke(1.dp, Color.White))
                    .height(60.dp)
                    .background(color = Color.Black)
                    .align(alignment = Alignment.CenterHorizontally),
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }) {
                opcions.forEach { type ->
                    DropdownMenuItem(modifier = Modifier.background(color = Color.Black) ,text = {
                        androidx.compose.material3.Text(
                            text = type,
                            style = TextStyle(color = Color.White, fontFamily = myViewModel.brownista)
                        )
                    }, onClick = {
                        expanded = false
                        newType = type
                    })
                }
            }

            OutlinedButton(modifier = Modifier.background(Color.Transparent), border = BorderStroke(1.dp, Color.White), onClick = {
                myViewModel.editMarker(newTitle, newDescription, newType)
                navigationController.navigateUp()
            }) {
                Text(text = "Save changes", color = Color.White, fontFamily = myViewModel.brownista, fontSize = 20.sp)
            }
        }
    }
}


