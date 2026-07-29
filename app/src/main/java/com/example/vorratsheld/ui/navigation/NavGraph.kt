package com.example.vorratsheld.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vorratsheld.ui.screens.auth.LoginScreen
import com.example.vorratsheld.ui.screens.auth.RegisterScreen

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
            // TODO: OverviewScreen einbauen
        }
        composable(Screen.Scan.route) {
            // TODO: ScanScreen einbauen
        }
        composable(Screen.ProductDetail.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            // TODO: ProductDetailScreen einbauen, productId nutzen
        }
        composable(Screen.ShoppingList.route) {
            // TODO: ShoppingListScreen einbauen
        }
    }
}
