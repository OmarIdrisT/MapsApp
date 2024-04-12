package com.example.mapsapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.ui.theme.MapsAppTheme
import com.example.mapsapp.view.DetailScreen
import com.example.mapsapp.view.LoginScreen
import com.example.mapsapp.view.MapScreen
import com.example.mapsapp.view.MarkerListSCreen
import com.example.mapsapp.view.TakePhotoScreen
import com.example.mapsapp.viewmodel.MyViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapsAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {

                    val navigationController = rememberNavController()
                    val myViewModel = MyViewModel()
                    val localizationPermissionState = rememberPermissionState(permission = android.Manifest.permission.ACCESS_FINE_LOCATION)

                    LaunchedEffect(Unit) {
                        localizationPermissionState.launchPermissionRequest()
                    }

                    if (localizationPermissionState.status.isGranted) {
                        MyDrawer(myViewModel, navigationController)
                    }
                    else {
                        Text("Need permission")
                    }
                }
            }
        }
    }
}

@Composable
fun MyDrawer(myViewModel: MyViewModel, navigationController: NavController) {
    val scope = rememberCoroutineScope()
    val state: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navBackStackEntry by navigationController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    ModalNavigationDrawer(drawerState = state, gesturesEnabled = false, drawerContent = {
        ModalDrawerSheet (drawerContainerColor = Color.Black) {
            Text("Username", modifier = Modifier.padding(16.dp), color = Color.White)
            HorizontalDivider()
            NavigationDrawerItem(
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Black, selectedContainerColor = Color.Cyan),
                label = { Text(text = "MyMap", color = Color.White)},
                selected = false,
                onClick = {
                    scope.launch {
                        state.close()
                    }
                    if (currentRoute != Routes.MapScreen.route) {
                        navigationController.navigate(Routes.MapScreen.route)
                    }
                }
            )
            NavigationDrawerItem(
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Black, selectedContainerColor = Color.Cyan),
                label = { Text(text = "Marker List", color = Color.White)},
                selected = false,
                onClick = {
                    scope.launch {
                        state.close()
                    }
                    if (currentRoute != Routes.MarkerListScreen.route) {
                        navigationController.navigate(Routes.MarkerListScreen.route)
                    }
                }
            )
        }
    }) {
        MyScaffold(myViewModel, state, navigationController)
    }
}


@Composable
fun MyScaffold(myViewModel: MyViewModel, state: DrawerState, navController: NavController) {
    Scaffold(
        containerColor = Color.Black,
        topBar = {MyTopAppBar(myViewModel, state)}
    ) {paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(
                navController = navController as NavHostController,
                startDestination = Routes.MapScreen.route
            ) {
                composable(Routes.LoginScreen.route) { LoginScreen(navController, myViewModel)}
                composable(Routes.MapScreen.route) { MapScreen(myViewModel, navController) }
                composable(Routes.MarkerListScreen.route) { MarkerListSCreen(myViewModel, navController) }
                composable(Routes.TakePhotoScreen.route) {TakePhotoScreen(navController, myViewModel)}
                composable(Routes.DetailScreen.route) { DetailScreen(navController, myViewModel) }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(myViewModel: MyViewModel, state: DrawerState) {
    val scope = rememberCoroutineScope()
    TopAppBar(
        colors = TopAppBarColors(titleContentColor = Color.White, containerColor = Color.Black, navigationIconContentColor = Color.White, actionIconContentColor = Color.White, scrolledContainerColor = Color.Black),
        title = { Text(text = "My SuperApp") },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    state.open()
                }
            }) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu")
            }
        }
    )
}

@Composable
fun PermissionDeclinedScreen() {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Permission Required", fontWeight = FontWeight.Bold)
        Text(text = "This app needs access to the camera to take photos")
        Button(onClick = {
            openAppSettings(context as Activity)
        }) {
            Text(text = "Accept")
        }
    }
}

fun openAppSettings(activity: Activity) {
    val intent = Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", activity.packageName, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    activity.startActivity(intent)
}










