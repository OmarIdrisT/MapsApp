package com.example.mapsapp.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.mapsapp.model.BottomNavigationScreen

class MyViewModel {
    val bottomNavigationItems = listOf(
        BottomNavigationScreen.Home,
        BottomNavigationScreen.Favorite
    )

    private var _searchText = MutableLiveData<String>()
    val searchText = _searchText

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }
    private val _showSearchBar = MutableLiveData(false)
    var showSearchBar = _showSearchBar

    private val _screenTitle = MutableLiveData<String>()
    val screenTitle = _screenTitle

    fun changeScreenTitle(value: String) {
        _screenTitle.value = value
    }


    fun deploySearchBar(value : Boolean) {
        _showSearchBar.value = value
    }
}