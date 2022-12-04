package com.markusw.app.ui.view.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.markusw.app.ui.theme.MidnightBlue
import com.markusw.app.ui.theme.PokedexAppTheme
import com.markusw.app.ui.view.composables.MainScreen
import com.markusw.app.ui.view.screens.Screens.HomeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokedexAppTheme {
                val navController = rememberNavController()
                val systemUiController = rememberSystemUiController()
                val isDarkThemeEnabled = isSystemInDarkTheme()

                SideEffect {
                    systemUiController.setNavigationBarColor(
                        color = if (isDarkThemeEnabled) MidnightBlue else Color.White
                    )
                    systemUiController.setStatusBarColor(
                        color = if (isDarkThemeEnabled) MidnightBlue else Color.White
                    )
                }

                NavHost(
                    navController = navController,
                    startDestination = HomeScreen.route
                ) {
                    composable(route = HomeScreen.route) {
                        MainScreen(
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
