package com.example.mapsapp.view

import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.placeholder
import com.example.mapsapp.R
import com.example.mapsapp.viewmodel.MyViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, myViewModel: MyViewModel) {
    var userTextfield by remember { mutableStateOf("") }
    var passTextfield by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val purple = Color(0xFF7B0BF3)
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
                OutlinedTextField(
                    value = userTextfield,
                    onValueChange = { userTextfield = it },
                    label = { Text("User") },
                    placeholder = {Text(text ="example@gmail.com", color = Color.LightGray)},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    leadingIcon = {Icon(imageVector = Icons.Default.Email, contentDescription = null)},
                    colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White,
                        focusedBorderColor = purple,
                        focusedLeadingIconColor = purple,
                        focusedLabelColor = purple
                    )
                )
                OutlinedTextField(
                    value = passTextfield,
                    onValueChange = { passTextfield = it },
                    label = { Text("Password") },
                    placeholder = {Text(text ="Min. 6 characters", color = Color.LightGray)},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = {Icon(imageVector = Icons.Default.Lock, contentDescription = null)},
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff
                        val description = if (passwordVisible) "Hide password" else "Show password"
                        IconButton(onClick = {passwordVisible = !passwordVisible}){
                            Icon(imageVector  = image, description) } },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White,
                        focusedBorderColor = purple,
                        focusedLeadingIconColor = purple,
                        focusedLabelColor = purple
                    )
                )
                if (loginFail == true) {
                    Text(text = "El usuari no existeix. Credencials incorrectes.", color = Color.Red, fontSize = 20.sp)
                    userTextfield = ""
                    passTextfield = ""
                }
                if (registerFail == true) {
                    Text(text = "No és possible registrar aquest usuari.", color = Color.Red, fontSize = 20.sp)
                    userTextfield = ""
                    passTextfield = ""
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(text = if (registerMode) "Ja tens compte?" else "No tens compte?", color = Color.White)
                    Text(text = if(!registerMode) " Registra't" else " Accedeix amb el teu compte", modifier = Modifier.clickable { myViewModel.changeMode() }, Color.Blue, textDecoration = TextDecoration.Underline)
                }
                Button(onClick = {
                    if (!registerMode) {
                        if (userTextfield != "" && passTextfield != "") {
                            myViewModel.login(userTextfield, passTextfield)
                            if (goNext == true) {
                                myViewModel.isLogged(true)
                                myViewModel.getUser(userId!!)
                                userTextfield = ""
                                passTextfield = ""
                            }
                        }

                    }
                    else {
                        if (userTextfield != "" && passTextfield != "") {
                            myViewModel.register(userTextfield, passTextfield)
                            if (registerFail == false) {
                                Toast.makeText(context, "Your account has been created", Toast.LENGTH_LONG).show()
                            }
                            userTextfield = ""
                            passTextfield = ""
                        }

                    }

                }) {
                    Text(text = if(!registerMode) "Login" else "Register", color = Color.White, fontSize = 20.sp)
                }

            }
        }
    }
}
