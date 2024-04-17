package com.example.mapsapp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mapsapp.R
import com.example.mapsapp.viewmodel.MyViewModel


@Composable
fun LoginScreen(navController: NavController, myViewModel: MyViewModel) {
    val userTextfield by myViewModel.userTextfield.observeAsState("")
    val passTextfield by myViewModel.passTextField.observeAsState("")
    val userId by myViewModel.repository.userId.observeAsState()
    val goNext by myViewModel.repository.goToNext.observeAsState(false)
    val loginFail by myViewModel.repository.loginFail.observeAsState()
    val registerFail by myViewModel.repository.registerFail.observeAsState()
    val registerMode by myViewModel.registerMode.observeAsState(false)

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
                    leadingIcon = {Icon(imageVector = Icons.Default.Email, contentDescription = null)},
                    placeholder = { Text("example@gmail.com") },
                    value = userTextfield!!,
                    onValueChange = { myViewModel.changeUserTextfield(it) }
                )
                Text(text = "Password", color = Color.White, fontSize = 30.sp)
                TextField(
                    modifier = Modifier.background(Color.White),
                    value = passTextfield!!,
                    leadingIcon = {Icon(imageVector = Icons.Default.Password, contentDescription = null)},
                    label = { Text("Password (min 6 chars)") },
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = { myViewModel.changePassTextfield(it) }
                )
                if (loginFail == true) {
                    Text(text = "El usuari no existeix. Credencials incorrectes.", color = Color.Red, fontSize = 20.sp)
                    myViewModel.changeUserTextfield("")
                    myViewModel.changePassTextfield("")
                }
                if (registerFail == true) {
                    Text(text = "No és possible registrar aquest usuari.", color = Color.Red, fontSize = 20.sp)
                    myViewModel.changeUserTextfield("")
                    myViewModel.changePassTextfield("")
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(text = if (registerMode) "Ja tens compte?" else "No tens compte?", color = Color.White)
                    Text(text = if(!registerMode) " Registra't" else " Accedeix amb el teu compte", modifier = Modifier.clickable { myViewModel.changeMode() }, Color.Blue, textDecoration = TextDecoration.Underline)
                }
                Button(onClick = {
                    if (!registerMode) {
                        myViewModel.login(userTextfield, passTextfield)
                        if (goNext == true) {
                            myViewModel.isLogged(true)
                            myViewModel.getUser(userId!!)
                            myViewModel.getMarkers()
                            myViewModel.changeUserTextfield("")
                            myViewModel.changePassTextfield("")
                        }
                    }
                    else {
                        myViewModel.register(userTextfield, passTextfield)
                        myViewModel.changeUserTextfield("")
                        myViewModel.changePassTextfield("")
                    }

                }) {
                    Text(text = if(!registerMode) "Login" else "Register", color = Color.White, fontSize = 20.sp)
                }

            }
        }
    }
}
