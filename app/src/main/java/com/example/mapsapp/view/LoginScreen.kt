package com.example.mapsapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavController
import com.example.mapsapp.viewmodel.MyViewModel


@Composable
fun LoginScreen(navController: NavController, myViewModel: MyViewModel) {
    val user by myViewModel.userTextfield.observeAsState()
    val pass by myViewModel.passTextField.observeAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight(0.4f)
                .fillMaxWidth(0.8f)
                .background(Color.Black)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
               Text(text = "User")
                TextField(
                    modifier = Modifier.background(Color.White),
                    value = user!!,
                    onValueChange = { myViewModel.changeUserTextfield(it) }
                )
                Text(text = "Password")
                TextField(
                    modifier = Modifier.background(Color.White),
                    value = pass!!,
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = { myViewModel.changePassTextfield(it) }
                )

            }
        }
    }
}

