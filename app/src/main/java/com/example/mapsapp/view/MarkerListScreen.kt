package com.example.mapsapp.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.mapsapp.MyDrawer
import com.example.mapsapp.viewmodel.MyViewModel


@Composable
fun MarkerListScreen(navigationController: NavHostController, myViewModel: MyViewModel) {
    MyDrawer(myViewModel, "Map")
}