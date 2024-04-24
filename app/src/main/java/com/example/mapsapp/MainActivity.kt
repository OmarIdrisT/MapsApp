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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mapsapp.firebase.firebasemodels.MarkerData
import com.example.mapsapp.firebase.firebasemodels.UserPrefs
import com.example.mapsapp.models.FilterOption
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.ui.theme.MapsAppTheme
import com.example.mapsapp.view.DetailScreen
import com.example.mapsapp.view.EditMarkerScreen
import com.example.mapsapp.view.LoginScreen
import com.example.mapsapp.view.MapScreen
import com.example.mapsapp.view.MarkerListSCreen
import com.example.mapsapp.view.TakePhotoScreen
import com.example.mapsapp.viewmodel.MyViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.LatLng
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
    val loggedUser by myViewModel.loggedUser.observeAsState("")
    val context = LocalContext.current
    val userPrefs = UserPrefs(context)

    ModalNavigationDrawer(drawerState = state, gesturesEnabled = false, drawerContent = {
        ModalDrawerSheet (drawerContainerColor = Color.Black) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Image(painter = painterResource(id = R.drawable.profileicon), contentDescription = null, modifier = Modifier.padding(16.dp))
                Text(text = loggedUser , modifier = Modifier.padding(16.dp), color = Color.White, fontFamily = myViewModel.brownista, fontSize = 30.sp)
            }

            HorizontalDivider(modifier = Modifier.height(16.dp), color = Color.White, thickness = 2.dp)
            NavigationDrawerItem(
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Black, selectedContainerColor = Color.Cyan),
                icon = { Image(painter = painterResource(id = R.drawable.mapicon), contentDescription = null) },
                label = { Text(text = "Map", color = Color.White, fontFamily = myViewModel.brownista, fontSize = 30.sp)},
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
            Spacer(modifier = Modifier.height(16.dp))
            NavigationDrawerItem(
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Black, selectedContainerColor = Color.Cyan),
                icon = { Image(painter = painterResource(id = R.drawable.listicon), contentDescription = null) },
                label = { Text(text = "Marker List", color = Color.White, fontFamily = myViewModel.brownista, fontSize = 30.sp)},
                selected = false,
                onClick = {
                    scope.launch {
                        state.close()
                    }
                    if (currentRoute != Routes.MarkerListScreen.route) {
                        myViewModel.filterList(filterOption = FilterOption.ALL)
                        navigationController.navigate(Routes.MarkerListScreen.route)
                    }
                }
            )
            Spacer(modifier = Modifier.weight(1f))
            NavigationDrawerItem(
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Black, selectedContainerColor = Color.Cyan),
                icon = { Image(painter = painterResource(id = R.drawable.logouticon), contentDescription = null) },
                label = { Text(text = "LogOut", color = Color.White, fontFamily = myViewModel.brownista, fontSize = 30.sp)},
                selected = false,
                onClick = {
                    scope.launch {
                        state.close()
                        userPrefs.clearUserData()
                        myViewModel.logOut()
                        myViewModel.changeMapaInicial(true)
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
    val isLoggedIn by myViewModel.isLoggedIn.observeAsState(false)

    if (!isLoggedIn) {
        LoginScreen(navController, myViewModel)
    }
    else {
        Scaffold(
            containerColor = Color.Black,
            topBar = {MyTopAppBar(myViewModel, state, navController)}
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
                    composable(Routes.EditMarkerScreen.route) {EditMarkerScreen(navController, myViewModel)}
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(myViewModel: MyViewModel, state: DrawerState, navController: NavController) {
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val deployFilter by myViewModel.deployFilter.observeAsState(false)
    var selectedFilter by remember { mutableStateOf(myViewModel.selectedFilter) }
    myViewModel.updateFilter(FilterOption.ALL)
    TopAppBar(
        colors = TopAppBarColors(titleContentColor = Color.White, containerColor = Color.Black, navigationIconContentColor = Color.White, actionIconContentColor = Color.White, scrolledContainerColor = Color.Black),
        title = { Text(text = "My SuperApp", color = Color.White, fontFamily = myViewModel.brownista, fontSize = 30.sp) },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    state.open()
                }
            }) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            if (currentRoute == Routes.MarkerListScreen.route) {
                IconButton(onClick = {
                    if (deployFilter) myViewModel.changeDeployFilter(false)
                    else myViewModel.changeDeployFilter(true)
                }) {
                    Icon(imageVector = Icons.Filled.FilterAlt, contentDescription = "Filter")
                }
                if (deployFilter) {
                    FilterDropDownMenu(
                        myViewModel = myViewModel,
                        onOptionSelected = { option ->
                            myViewModel.updateFilter(option)
                        },
                        currentOption = selectedFilter.value!!)
                }
            }
            if(currentRoute == Routes.DetailScreen.route) {
                IconButton(onClick = {
                    navController.navigate(Routes.EditMarkerScreen.route)
                }) {
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit")
                }
            }
        }
    )
}

@Composable
fun FilterDropDownMenu(myViewModel: MyViewModel, onOptionSelected: (FilterOption) -> Unit, currentOption: FilterOption) {
    var selectedOption by remember { mutableStateOf(currentOption) }

    DropdownMenu(
        expanded = true,
        onDismissRequest = {myViewModel.changeDeployFilter(false) },
        modifier = Modifier.background(Color.Black).padding(horizontal = 4.dp),
    ) {
        Column {
            FilterOption.values().forEach { option ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedOption == option,
                        onClick = {
                            selectedOption = option
                            onOptionSelected(selectedOption)
                        },
                        colors = RadioButtonDefaults.colors(selectedColor = Color.White),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(
                        text = option.title,
                        color = Color.White,
                        fontFamily = myViewModel.brownista,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
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










