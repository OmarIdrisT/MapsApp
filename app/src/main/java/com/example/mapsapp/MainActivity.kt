package com.example.mapsapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mapsapp.model.BottomNavigationScreen
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.ui.theme.MapsAppTheme
import com.example.mapsapp.view.MapScreen
import com.example.mapsapp.view.MarkerListScreen
import com.example.mapsapp.view.MyMap
import com.example.mapsapp.view.SplashScreen
import com.example.mapsapp.viewmodel.MyViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
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
                    MyDrawer(myViewModel)
                    NavHost(
                        navController = navigationController,
                        startDestination = Routes.SplashScreen.route
                    ) {
                        composable(Routes.SplashScreen.route) { SplashScreen(navigationController) }
                        composable(Routes.MapScreen.route) { MapScreen(navigationController, myViewModel) }
                        composable(Routes.MarkerListScreen.route) {MarkerListScreen(navigationController, myViewModel)}
                    }

                }
            }
        }
    }
}


@Composable
fun MyScaffold(
    navigationController: NavController,
    myViewModel: MyViewModel,
    state: DrawerState
) {
    val bottomNavigationItems = myViewModel.bottomNavigationItems
    val screenTitle = myViewModel.screenTitle
    Scaffold(
        topBar = { TopAppBar(myViewModel, navigationController, state) },
        bottomBar = { MyBottomBar(navigationController, bottomNavigationItems)},
        content = { paddingValues ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)) {
                if (screenTitle == "Marker List") {
                }
                else {
                    MyMap(navigationController)
                }
            }
        }

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(myViewModel: MyViewModel, navigationController: NavController, state: DrawerState) {
    val showSearchBar: Boolean by myViewModel.showSearchBar.observeAsState(false)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navigationController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    TopAppBar(
        title = { Text(text = myViewModel.screenTitle) },
        navigationIcon = {
            if (screenTitle == "Map") {
                IconButton(onClick = {
                    scope.launch {
                        state.open()
                    }
                }) {
                    Icon(imageVector = Icons.Filled.Menu, contentDescription = "menu")
                }
        } },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Black,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        actions = {
            if (screenTitle == "Marker List") {
                if (showSearchBar) {
                    MySearchBar(myViewModel)
                }
                IconButton(onClick = {myViewModel.deploySearchBar(true)}){
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
                }
            }
            else {

            }

        }
    )
}
@Composable
fun MyBottomBar(
    navigationController: NavController,
    bottomNavigationItems: List<BottomNavigationScreen>
) {
    BottomNavigation(backgroundColor = Color.Black, contentColor = Color.White) {
        val navBackStackEntry by navigationController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        bottomNavigationItems.forEach { item ->
            val selectedColor = if(currentRoute == item.route) Color.Green else Color.White
            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.label, tint = selectedColor) },
                label = { Text(text = item.label, color = selectedColor) },
                selected = currentRoute == item.route,
                alwaysShowLabel = false,
                onClick = {
                    if (currentRoute != item.route) {
                        navigationController.navigate(item.route)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySearchBar (myViewModel: MyViewModel) {
    val searchText: String by myViewModel.searchText.observeAsState("")
    val showSearchBar: Boolean by myViewModel.showSearchBar.observeAsState(true)
    SearchBar(
        colors = SearchBarDefaults.colors(Color.Black, inputFieldColors = TextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)),
        query = searchText,
        onQueryChange = { myViewModel.onSearchTextChange(it) },
        onSearch = { myViewModel.onSearchTextChange(it) },
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "CloseSearch",
                tint = Color.White,
                modifier = Modifier.clickable {
                    if (showSearchBar) {
                        myViewModel.deploySearchBar(false)
                    }
                })
        },
        active = true,
        placeholder = { Text(text = "Search...", color = Color.White) },
        onActiveChange = {},
        modifier = Modifier
            .fillMaxHeight(0.1f)
            .clip(CircleShape)) {
    }
}

@Composable
fun MyDrawer(myViewModel:MyViewModel) {
    val navigationController = rememberNavController()
    val scope = rememberCoroutineScope()
    val state: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    ModalNavigationDrawer(drawerState = state, gesturesEnabled = true, drawerContent = {
        Text("Menú", modifier = Modifier.padding(16.dp))
        Divider()
        NavigationDrawerItem(label = { Text(text = "Item 1") }, selected = false, onClick = {scope.launch { state.close() }})
    }) {
        MyScaffold(navigationController = navigationController, myViewModel = myViewModel, state)
    }
}
