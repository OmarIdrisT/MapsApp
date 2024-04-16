package com.example.mapsapp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.placeholder
import com.example.mapsapp.R
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.viewmodel.MyViewModel


@Composable
fun LoginScreen(navController: NavController, myViewModel: MyViewModel) {
    val user by myViewModel.userTextfield.observeAsState("")
    val pass by myViewModel.passTextField.observeAsState("")
    val goNext by myViewModel.repository.goToNext.observeAsState(false)
    val loginFail by myViewModel.repository.loginFail.observeAsState()

    Image(
        painter = painterResource(id = R.drawable.fondo),
        contentDescription = "",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .fillMaxWidth(0.8f)
                .background(Color.Black.copy(alpha = 0.3f))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxSize()
            ) {
               Text(text = "User", color = Color.White, fontSize = 30.sp)
                TextField(
                    modifier = Modifier.background(Color.White),
                    placeholder = { "example@gmail.com" },
                    value = user!!,
                    onValueChange = { myViewModel.changeUserTextfield(it) }
                )
                Text(text = "Password", color = Color.White, fontSize = 30.sp)
                TextField(
                    modifier = Modifier.background(Color.White),
                    value = pass!!,
                    placeholder = { "Password (min 6 chars)" },
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = { myViewModel.changePassTextfield(it) }
                )
                if (loginFail == true) {
                    Text(text = "El usuari no existeix. Credencials incorrectes.", color = Color.Red, fontSize = 20.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = {
                        myViewModel.login(user, pass)
                        if (goNext == true) {
                            myViewModel.isLogged(true)
                            myViewModel.changeUserTextfield("")
                            myViewModel.changePassTextfield("")
                        }
                    }) {
                        Text(text = "Login", color = Color.White, fontSize = 20.sp)
                    }
                    Button(onClick = {
                        myViewModel.register(user, pass)
                        myViewModel.changeUserTextfield("")
                        myViewModel.changePassTextfield("")
                    }) {
                        Text(text = "Registrar-se",color = Color.White, fontSize = 20.sp)
                    }
                }

            }
        }
    }
}

