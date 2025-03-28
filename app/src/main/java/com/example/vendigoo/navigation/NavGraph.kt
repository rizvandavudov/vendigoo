package com.example.vendigoo.navigation

import SupportScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.vendigoo.ui.components.AboutScreen

import com.example.vendigoo.ui.screens.FinanceScreen
import com.example.vendigoo.ui.screens.GivenGoodsScreen
import com.example.vendigoo.ui.screens.MainScreen
import com.example.vendigoo.ui.screens.SettingsScreen
import com.example.vendigoo.ui.screens.SuppliersScreen
import com.example.vendigoo.ui.screens.TakenMoneyScreen
import com.example.vendigoo.viewmodel.WholesaleViewModel

// navigation/NavGraph.kt
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    viewModel: WholesaleViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(navController = navController, viewModel = viewModel)
        }

        composable(
            route = "suppliers/{districtId}",
            arguments = listOf(navArgument("districtId") { type = NavType.IntType })
        ) { backStackEntry ->
            SuppliersScreen(
                navController = navController,
                viewModel = viewModel,
                districtId = backStackEntry.arguments?.getInt("districtId") ?: 0
            )
        }

        composable(
            route = "finance/{supplierId}",
            arguments = listOf(navArgument("supplierId") { type = NavType.IntType })
        ) { backStackEntry ->
            FinanceScreen(
                navController = navController,
                viewModel = viewModel,
                supplierId = backStackEntry.arguments?.getInt("supplierId") ?: 0
            )
        }

        composable(
            route = "given_goods/{supplierId}",
            arguments = listOf(navArgument("supplierId") { type = NavType.IntType })
        ) { backStackEntry ->
            GivenGoodsScreen(
                navController = navController,
                viewModel = viewModel,
                supplierId = backStackEntry.arguments?.getInt("supplierId") ?: 0
            )
        }

        composable(
            route = "taken_money/{supplierId}",
            arguments = listOf(navArgument("supplierId") { type = NavType.IntType })
        ) { backStackEntry ->
            TakenMoneyScreen(
                navController = navController,
                viewModel = viewModel,
                supplierId = backStackEntry.arguments?.getInt("supplierId") ?: 0
            )
        }
        composable("support") { SupportScreen(navController) }
        composable("about") { AboutScreen(navController) }

        // Yeni əlavə etdiyimiz
        composable("settings") {
            SettingsScreen(navController = navController, viewModel = viewModel)
        }

    }
}