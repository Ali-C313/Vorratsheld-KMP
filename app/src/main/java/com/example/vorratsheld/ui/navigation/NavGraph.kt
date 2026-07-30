package com.example.vorratsheld.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.vorratsheld.ui.screens.auth.LoginScreen
import com.example.vorratsheld.ui.screens.auth.RegisterScreen
import com.example.vorratsheld.ui.screens.overview.OverviewScreen
import com.example.vorratsheld.ui.screens.productdetail.ProductDetailScreen
import com.example.vorratsheld.ui.screens.scan.ScanScreen

@Composable
fun VorratsheldNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }
        composable(Screen.Overview.route) {
            OverviewScreen(navController = navController)
        }
        composable(Screen.Scan.route) {
            ScanScreen(navController = navController)
        }
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("barcode") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            val barcode = backStackEntry.arguments?.getString("barcode") ?: ""
            ProductDetailScreen(
                navController = navController,
                productId = productId,
                barcode = barcode
            )
        }
        composable(Screen.ShoppingList.route) {
            // TODO: ShoppingListScreen einbauen
        }
    }
}