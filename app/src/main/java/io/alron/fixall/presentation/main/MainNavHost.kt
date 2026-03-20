package io.alron.fixall.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.alron.fixall.presentation.MainRoute
import io.alron.fixall.presentation.home.HomeScreen

@Composable
fun MainNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val startDestination = MainRoute.Home

    Scaffold(
        bottomBar = {
            NavigationBar(
                windowInsets = NavigationBarDefaults.windowInsets,
            ) {
                MainRoute.entries.forEachIndexed { _, destination ->
                    NavigationBarItem(
                        selected = currentRoute == destination.name,
                        onClick = {
                            navController.navigate(destination.name) {
                                popUpTo(startDestination.name) {
                                    saveState = true
                                }
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = null
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(MainRoute.Home.name) {
                HomeScreen(
                    onToolbarIconClick = {
                        navController.navigate(MainRoute.Profile.name)
                    },
                )
            }

            composable(MainRoute.Branches.name) {
                Text("Branches")
            }

            composable(MainRoute.Appointments.name) {
                Text("Appointments")
            }

            composable(MainRoute.Profile.name) {
                Text("Profile")
            }
        }
    }
}