package com.example.vorratsheld.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.vorratsheld.data.enums.LegalType
import com.example.vorratsheld.data.settings.AppSettings
import com.example.vorratsheld.ui.applock.AppLockSettingsScreen
import com.example.vorratsheld.ui.auth.AuthViewModel
import com.example.vorratsheld.ui.auth.LoginScreen
import com.example.vorratsheld.ui.auth.RegisterScreen
import com.example.vorratsheld.ui.editprofile.EditProfileScreen
import com.example.vorratsheld.ui.help.HelpScreen
import com.example.vorratsheld.ui.language.LanguageScreen
import com.example.vorratsheld.ui.legal.LegalScreen
import com.example.vorratsheld.ui.lock.LockScreen
import com.example.vorratsheld.ui.onboarding.OnboardingScreen
import com.example.vorratsheld.ui.overview.OverviewScreen
import com.example.vorratsheld.ui.productdetail.ProductDetailScreen
import com.example.vorratsheld.ui.productinfo.ProductInfoScreen
import com.example.vorratsheld.ui.scan.ScanScreen
import com.example.vorratsheld.ui.settings.SettingsScreen
import com.example.vorratsheld.ui.shoppinglist.ShoppingListScreen
import com.example.vorratsheld.ui.splash.SplashScreen
import com.example.vorratsheld.util.AppLock
import org.koin.compose.viewmodel.koinViewModel

// Splash nur einmal pro App-Start zeigen (nicht nochmal nach einem Sprachwechsel)
private var introShownThisSession = false

// Navigation der App plus Sperrbildschirm darüber
@Composable
fun VorratsheldNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val authViewModel: AuthViewModel = koinViewModel()

    // Nach Splash und Onboarding: eingeloggt zur Übersicht, sonst zum Login
    val afterIntro: () -> Screen = {
        if (authViewModel.isLoggedIn()) Screen.Overview else Screen.Login
    }
    val startDestination = if (introShownThisSession) afterIntro() else Screen.Splash

    val locked by AppLock.locked.collectAsState()
    var introDone by remember { mutableStateOf(introShownThisSession) }

    // App geht in den Hintergrund oder kommt zurück, daraus entscheidet AppLock über die Sperre
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) { AppLock.onBackground() }
    LifecycleEventEffect(Lifecycle.Event.ON_START) { AppLock.onForeground() }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable<Screen.Splash> {
                SplashScreen(
                    onFinished = {
                        introShownThisSession = true
                        introDone = true
                        val target = if (AppSettings.onboardingDone) afterIntro() else Screen.Onboarding
                        navController.navigate(target) {
                            popUpTo(Screen.Splash) { inclusive = true }
                        }
                    }
                )
            }
            composable<Screen.Onboarding> {
                OnboardingScreen(
                    onFinished = {
                        AppSettings.onboardingDone = true
                        navController.navigate(afterIntro()) {
                            popUpTo(Screen.Onboarding) { inclusive = true }
                        }
                    }
                )
            }
            composable<Screen.Login> {
                LoginScreen(navController = navController)
            }
            composable<Screen.Register> {
                RegisterScreen(navController = navController)
            }
            composable<Screen.Overview> {
                OverviewScreen(navController = navController)
            }
            composable<Screen.Scan> {
                ScanScreen(navController = navController)
            }
            composable<Screen.Settings> {
                SettingsScreen(navController = navController)
            }
            composable<Screen.EditProfile> {
                EditProfileScreen(navController = navController)
            }
            composable<Screen.Language> {
                LanguageScreen(navController = navController)
            }
            composable<Screen.Help> {
                HelpScreen(navController = navController)
            }
            composable<Screen.SecuritySettings> {
                AppLockSettingsScreen(navController = navController)
            }
            composable<Screen.Legal> { backStackEntry ->
                val route = backStackEntry.toRoute<Screen.Legal>()
                LegalScreen(
                    navController = navController,
                    type = LegalType.valueOf(route.type)
                )
            }
            composable<Screen.ProductDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<Screen.ProductDetail>()
                ProductDetailScreen(
                    navController = navController,
                    productId = route.productId,
                    barcode = route.barcode
                )
            }
            composable<Screen.ProductInfo> { backStackEntry ->
                val route = backStackEntry.toRoute<Screen.ProductInfo>()
                ProductInfoScreen(
                    navController = navController,
                    productId = route.productId
                )
            }
            composable<Screen.ShoppingList> {
                ShoppingListScreen(navController = navController)
            }
        }

        // Sperre erst nach dem Splash und nur, wenn jemand eingeloggt ist
        if (locked && introDone && authViewModel.isLoggedIn()) {
            LockScreen(
                onUnlocked = { AppLock.unlock() },
                onForgotPin = {
                    authViewModel.logout {
                        AppLock.unlock()
                        navController.navigate(Screen.Login) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                }
            )
        }
    }
}
