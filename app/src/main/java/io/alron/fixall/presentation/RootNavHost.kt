package io.alron.fixall.presentation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import io.alron.fixall.BuildConfig
import io.alron.fixall.domain.AuthManager
import io.alron.fixall.domain.repository.ProfileRepository
import io.alron.fixall.presentation.main.MainNavHost
import io.alron.fixall.presentation.login.LoginScreen
import io.alron.fixall.presentation.registration.RegistrationScreen
import io.alron.fixall.presentation.util.LocalUserAvatar

private enum class GraphRoute {
    Auth,
    Main
}

private const val mainNavHostRoute = "NavHostRoute"

@Composable
fun RootNavHost(
     authManager: AuthManager,
     profileRepository: ProfileRepository
) {
    val navController = rememberNavController()
    val isAuthorized by authManager.isAuthorized.collectAsState()
    
    val currentUser by profileRepository.currentUser.collectAsState()

    LaunchedEffect(isAuthorized) {
        if (isAuthorized) {
            profileRepository.getMe()
        } else {
            profileRepository.clearCache()
        }
    }

    val userAvatarUrl = remember(currentUser) {
        currentUser?.profile?.avatarUrl?.let { 
            if (it.startsWith("http")) it else "${BuildConfig.BASE_URL}$it"
        }
    }

    CompositionLocalProvider(LocalUserAvatar provides userAvatarUrl) {
        NavHost(
            navController = navController,
            startDestination = if (isAuthorized) GraphRoute.Main.name else GraphRoute.Auth.name,
        ) {
            navigation(
                startDestination = AuthRoute.Login.name,
                route = GraphRoute.Auth.name
            ) {
                composable(route = AuthRoute.Login.name) {
                    LoginScreen(
                        onNavigateToRegistration = {
                            navController.popBackStack()
                            navController.navigate(AuthRoute.Registration.name)
                        }
                    )
                }

                composable(route = AuthRoute.Registration.name) {
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
                composable(route = mainNavHostRoute) {
                    MainNavHost()
                }
            }
        }
    }
}
