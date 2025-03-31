package com.example.vendigoo.navigation

import SupportScreen
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.vendigoo.ui.components.AboutScreen
import com.example.vendigoo.ui.components.LockScreen
import com.example.vendigoo.ui.screens.FinanceScreen
import com.example.vendigoo.ui.screens.GivenGoodsScreen

import com.example.vendigoo.ui.screens.MainScreen
import com.example.vendigoo.ui.screens.SettingsScreen
import com.example.vendigoo.ui.screens.SuppliersScreen
import com.example.vendigoo.ui.screens.TakenMoneyScreen
import com.example.vendigoo.viewmodel.WholesaleViewModel

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    viewModel: WholesaleViewModel,
    sharedPreferences: SharedPreferences
) {
    NavHost(
        navController = navController,
        startDestination = "lock" // İLK olaraq LockScreen açılacaq
    ) {
        composable("lock") {
            LockScreen(
                navController = navController,
                sharedPreferences = sharedPreferences
            )
        }

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
        composable("settings") {
            SettingsScreen(navController = navController, viewModel = viewModel)
        }
    }
}
