package io.alron.fixall.auth.presentation

sealed class AuthRoute(val route: String) {
    data object Login : AuthRoute("login")
    data object Registration : AuthRoute("destination")
}