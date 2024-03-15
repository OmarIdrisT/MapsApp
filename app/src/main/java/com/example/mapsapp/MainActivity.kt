package com.example.mapsapp

import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.ui.theme.MapsAppTheme
import com.example.mapsapp.view.CameraScreen
import com.example.mapsapp.view.MapScreen
import com.example.mapsapp.view.MarkerListScreen
import com.example.mapsapp.view.MyMap
import com.example.mapsapp.view.MyRecyclerView
import com.example.mapsapp.view.SplashScreen
import com.example.mapsapp.viewmodel.MyViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
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
                    val cameraPermissionState = rememberPermissionState(permission = android.Manifest.permission.CAMERA)

                    LaunchedEffect(Unit) {
                        localizationPermissionState.launchPermissionRequest()
                    }
                    LaunchedEffect(Unit) {
                        cameraPermissionState.launchPermissionRequest()
                    }

                    if (localizationPermissionState.status.isGranted) {
                        MyDrawer(myViewModel, navigationController)
                    }
                    else {
                        Text("Need permission")
                    }


                    NavHost(
                        navController = navigationController,
                        startDestination = Routes.SplashScreen.route
                    ) {
                        composable(Routes.SplashScreen.route) { SplashScreen(navigationController) }
                        composable(Routes.MapScreen.route) { MapScreen(navigationController, myViewModel) }
                        composable(Routes.MarkerListScreen.route) {MarkerListScreen(myViewModel, navigationController)}
                        composable(Routes.CameraScreen.route) {CameraScreen(navigationController, myViewModel)}
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
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(
        topBar = {MyTopAppBar(myViewModel, state)}
    ) {paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when(currentRoute) {
                Routes.MapScreen.route -> MyMap(myViewModel = myViewModel, navigationController = navController )
                Routes.MarkerListScreen.route -> MyRecyclerView(myViewModel = myViewModel, navController = navController )
            }

        }

    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(myViewModel: MyViewModel, state: DrawerState) {
    val scope = rememberCoroutineScope()
    TopAppBar(
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
fun myDropDownMenu(myViewModel: MyViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val opcions = listOf("Restaurant", "Espai cultural", "Botiga", "Transport")

    Column (modifier = Modifier.padding(20.dp)) {
        OutlinedTextField(
            value = myViewModel.placeType,
            onValueChange = { myViewModel.placeTypeChange(it) },
            enabled = false,
            readOnly = true,
            textStyle = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center),
            modifier = Modifier
                .clickable { expanded = true }
                .fillMaxWidth(0.6f)
                .height(60.dp)
                .background(color = Color.Transparent)
                .align(alignment = Alignment.CenterHorizontally)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            opcions.forEach { dificultad ->
                DropdownMenuItem(modifier = Modifier.background(color = Color.Black) ,text = { Text(text = dificultad, style = TextStyle(color = Color.White)) }, onClick = {
                    expanded = false
                    myViewModel.placeTypeChange(dificultad)
                })
            }
        }
    }
}








