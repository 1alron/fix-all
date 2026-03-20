package io.alron.fixall.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import io.alron.fixall.domain.AuthManager
import io.alron.fixall.presentation.main.MainNavHost
import io.alron.fixall.presentation.login.LoginScreen
import io.alron.fixall.presentation.registration.RegistrationScreen

private enum class GraphRoute {
    Auth,
    Main
}

private const val mainNavHostRoute = "NavHostRoute"

@Composable
fun RootNavHost(
     authManager: AuthManager
) {
    val navController = rememberNavController()
    val isAuthorized by authManager.isAuthorized.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (isAuthorized) GraphRoute.Main.name else GraphRoute.Auth.name,
    ) {
        navigation(
            startDestination = AuthRoute.Login.name,
            route = GraphRoute.Auth.name
        ) {
            composable(
                route = AuthRoute.Login.name
            ) {
                LoginScreen(
                    onNavigateToRegistration = {
                        navController.popBackStack()
                        navController.navigate(AuthRoute.Registration.name)
                    }
                )
            }

            composable(
                route = AuthRoute.Registration.name
            ) {
                RegistrationScreen(
                    onBackToLogin = {
                        navController.popBackStack()
                        navController.navigate(AuthRoute.Login.name)
                    }
                )
            }
        }

        navigation(
            startDestination = mainNavHostRoute,
            route = GraphRoute.Main.name
        ) {
            composable(
                route = mainNavHostRoute
            ) {
                MainNavHost()
            }
        }
    }
}