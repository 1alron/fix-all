package io.alron.fixall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.alron.fixall.auth.presentation.AuthRoute
import io.alron.fixall.auth.presentation.login.LoginScreen
import io.alron.fixall.auth.presentation.registration.RegistrationScreen
import io.alron.fixall.ui.theme.FixAllTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FixAllTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = AuthRoute.Login.route,
                ) {
                    composable(
                        route = AuthRoute.Login.route
                    ) {
                        LoginScreen(
                            onNavigateToRegistration = {
                                navController.popBackStack()
                                navController.navigate(AuthRoute.Registration.route)
                            }
                        )
                    }

                    composable(
                        route = AuthRoute.Registration.route
                    ) {
                        RegistrationScreen(
                            onBackToLogin = {
                                navController.popBackStack()
                                navController.navigate(AuthRoute.Login.route)
                            },
                            onRegisterSuccess = { }
                        )
                    }
                }
            }
        }
    }
}