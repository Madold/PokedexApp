package com.markusw.app.ui.view.screens

sealed class Screens(val route: String) {
    object HomeScreen: Screens("pokemon_list_screen")
}


