package com.example.mapsapp.view

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mapsapp.R
import com.example.mapsapp.firebase.firebasemodels.UserPrefs
import com.example.mapsapp.viewmodel.MyViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, myViewModel: MyViewModel) {
    var userTextfield by remember { mutableStateOf("") }
    var passTextfield by remember { mutableStateOf("") }
    var verifypassTextField by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var verifypasswordVisible by remember {mutableStateOf(false)}
    val context = LocalContext.current
    val showRegisterToast by myViewModel.showRegisterToast.observeAsState(false)
    val userId by myViewModel.userId.observeAsState()
    val goNext by myViewModel.goToNext.observeAsState(false)
    val loginFail by myViewModel.loginFail.observeAsState()
    val registerFail by myViewModel.registerFail.observeAsState()
    val registerMode by myViewModel.registerMode.observeAsState(false)
    val userPrefs = UserPrefs(context)
    val storedUserData=userPrefs.getUserData.collectAsState(initial = emptyList())
    var rememberUser by remember { mutableStateOf(false)}
    if (storedUserData.value.isNotEmpty() && storedUserData.value[0]!="" && storedUserData.value[1]!=""){
        storedUserData.value.let {
            if(rememberUser) {
                myViewModel.login(it[0],it[1])
            }

        }
    }

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
                    label = { Text(text = "User", color = Color.White, fontFamily = myViewModel.brownista, fontSize = 20.sp) },
                    placeholder = {Text(text ="example@gmail.com", color = Color.LightGray, fontFamily = myViewModel.brownista, fontSize = 20.sp)},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    textStyle = TextStyle(fontFamily = myViewModel.brownista, fontSize = 20.sp),
                    leadingIcon = {Icon(imageVector = Icons.Default.Email, contentDescription = null)},
                    colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        containerColor = Color.Black.copy(alpha = 0.05f),
                        focusedBorderColor = Color.Green,
                        unfocusedLeadingIconColor = Color.White,
                        focusedLeadingIconColor = Color.Green,
                        focusedLabelColor = Color.Green
                    )
                )
                OutlinedTextField(
                    value = passTextfield,
                    onValueChange = { passTextfield = it },
                    label = { Text(text = "Password", color = Color.White, fontFamily = myViewModel.brownista, fontSize = 20.sp) },
                    placeholder = {Text(text ="Min. 6 characters", color = Color.LightGray, fontFamily = myViewModel.brownista, fontSize = 20.sp)},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    textStyle = TextStyle(fontFamily = myViewModel.brownista, fontSize = 20.sp),
                    leadingIcon = {Icon(imageVector = Icons.Default.Lock, contentDescription = null)},
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff
                        val description = if (passwordVisible) "Hide password" else "Show password"
                        IconButton(onClick = {passwordVisible = !passwordVisible}){
                            Icon(imageVector  = image, description, tint = Color.White) } },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        containerColor = Color.Black.copy(alpha = 0.05f),
                        focusedBorderColor = Color.Green,
                        unfocusedLeadingIconColor = Color.White,
                        focusedLeadingIconColor = Color.Green,
                        focusedLabelColor = Color.Green
                    )
                )
                if (registerMode) {
                    OutlinedTextField(
                        value = verifypassTextField,
                        onValueChange = { verifypassTextField = it },
                        label = { Text(text = "VerifyPassword", color = Color.White, fontFamily = myViewModel.brownista, fontSize = 20.sp) },
                        placeholder = {Text(text ="Min. 6 characters", color = Color.LightGray, fontFamily = myViewModel.brownista, fontSize = 20.sp)},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        textStyle = TextStyle(fontFamily = myViewModel.brownista, fontSize = 20.sp),
                        leadingIcon = {Icon(imageVector = Icons.Default.Lock, contentDescription = null)},
                        trailingIcon = {
                            val image = if (verifypasswordVisible)
                                Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff
                            val description = if (verifypasswordVisible) "Hide password" else "Show password"
                            IconButton(onClick = {verifypasswordVisible = !verifypasswordVisible}){
                                Icon(imageVector  = image, description, tint = Color.White) } },
                        visualTransformation = if (verifypasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedBorderColor = Color.White,
                            containerColor = Color.Black.copy(alpha = 0.05f),
                            focusedBorderColor = Color.Green,
                            unfocusedLeadingIconColor = Color.White,
                            focusedLeadingIconColor = Color.Green,
                            focusedLabelColor = Color.Green
                        )
                    )
                }
                else {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),horizontalArrangement = Arrangement.Center,verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked =rememberUser ,
                            onCheckedChange ={rememberUser=it},
                            colors = CheckboxDefaults.colors(
                                checkmarkColor = Color.Green,
                                uncheckedColor = Color.White,
                            ) )
                        Text(text = "Remember me", color = Color.White, fontFamily = myViewModel.brownista, fontSize = 20.sp)
                    }
                }
                if (loginFail == true) {
                    Text(text = "This user does not exist.", color = Color.Red.copy(alpha = 0.8f), fontFamily = myViewModel.brownista, fontSize = 20.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(text = if (registerMode) "Do you have an account?" else "Don't have an account?", color = Color.White, fontFamily = myViewModel.brownista, fontSize = 20.sp)
                    Text(
                        text = if(!registerMode) " Sign up." else " Log in",
                        modifier = Modifier.clickable {
                            myViewModel.changeMode()
                            myViewModel.updateLoginFail()
                            myViewModel.updateRegisterFail()},
                        color = Color.Blue,
                        fontFamily = myViewModel.brownista,
                        textDecoration = TextDecoration.Underline,
                        fontSize = 20.sp)
                }
                if (goNext == true) {
                    myViewModel.getUser(userId!!)
                    myViewModel.isLogged(true)
                    userTextfield = ""
                    passTextfield = ""
                    verifypassTextField = ""
                }
                if (!registerMode) {
                    OutlinedButton(modifier = Modifier.background(Color.Transparent), border = BorderStroke(1.dp, Color.White), onClick = {
                        myViewModel.updateLoginFail()
                        if (userTextfield != "" && passTextfield != "") {
                            myViewModel.login(userTextfield, passTextfield)
                            CoroutineScope(Dispatchers.IO).launch {
                                if (rememberUser) {
                                    userPrefs.saveUserData(userTextfield,passTextfield)
                                }
                                else {
                                    userPrefs.clearUserData()
                                    userTextfield = ""
                                    passTextfield = ""

                                }
                            }
                            verifypassTextField = ""
                        }}) {
                        Text(text = "Log in", color = Color.White, fontFamily = myViewModel.brownista, fontSize = 20.sp)
                    }
                } else {
                    OutlinedButton(modifier = Modifier.background(Color.Transparent), border = BorderStroke(1.dp, Color.White), onClick = {
                        myViewModel.updateRegisterFail()
                        if (userTextfield != "" && passTextfield != "" && verifypassTextField == passTextfield) {
                            myViewModel.register(userTextfield, passTextfield)
                        }
                        userTextfield = ""
                        passTextfield = ""
                        verifypassTextField = ""}) {
                        Text(text = "Sign up", color = Color.White, fontFamily = myViewModel.brownista, fontSize = 20.sp)
                    }
                    if (registerFail == true) {
                        Text(text = "This user cannot be created.", color = Color.Red.copy(alpha = 0.8f), fontFamily = myViewModel.brownista, fontSize = 20.sp)
                    }
                    if (showRegisterToast) {
                        Toast.makeText(context, "Your account has been created successfully", Toast.LENGTH_LONG).show()
                        myViewModel.restoreRegisterToast()
                    }
                }
            }
        }
    }
}
