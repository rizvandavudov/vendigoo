package com.example.vendigoo

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.vendigoo.navigation.NavGraph
import com.example.vendigoo.ui.theme.WholesaleTheme
import com.example.vendigoo.viewmodel.WholesaleViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

// MainActivity.kt
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent {
            WholesaleTheme {
                // Tema və sistem UI parametrləri
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = !isSystemInDarkTheme()

                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = Color.Transparent,
                        darkIcons = useDarkIcons
                    )
                }

                // Naviqasiya və ViewModel
                val navController = rememberNavController()
                val viewModel: WholesaleViewModel = viewModel()

                val sharedPrefs = getSharedPreferences("vendigoo_prefs", Context.MODE_PRIVATE)

                NavGraph(
                    navController = navController,
                    viewModel = viewModel,
                    sharedPreferences = sharedPrefs
                )
            }
        }
    }
}