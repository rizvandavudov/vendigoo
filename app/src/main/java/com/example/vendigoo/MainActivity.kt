package com.example.vendigoo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.vendigoo.data.entities.database.WholesaleDatabase
import com.example.vendigoo.navigation.NavGraph
import com.example.vendigoo.ui.theme.WholesaleTheme
import com.example.vendigoo.viewmodel.WholesaleViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

// MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Proqram başlayanda verilənlər bazasını işə salırıq
        val database = WholesaleDatabase.getDatabase(this)

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

                NavGraph(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}