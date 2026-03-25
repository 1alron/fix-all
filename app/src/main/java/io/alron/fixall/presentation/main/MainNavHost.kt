package io.alron.fixall.presentation.main

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.alron.fixall.R
import io.alron.fixall.presentation.MainRoute
import io.alron.fixall.presentation.home.HomeFloatingActionButton
import io.alron.fixall.presentation.home.HomeScreen
import kotlinx.coroutines.launch

@Composable
fun MainNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val startDestination = MainRoute.Home

    val scrollState = rememberScrollState()

    var lastScrollValue by remember { mutableIntStateOf(0) }
    var isScrollingDown by remember { mutableStateOf(false) }

    val bottomBarOffset by animateDpAsState(
        targetValue = if (isScrollingDown) 72.dp else 0.dp,
        animationSpec = tween(durationMillis = 300)
    )
    val fabWidth by animateDpAsState(
        targetValue = if (isScrollingDown) 56.dp else 180.dp,
        animationSpec = tween(durationMillis = 300)
    )
    val fabTextAlpha by animateFloatAsState(
        targetValue = if (isScrollingDown) 0f else 1f,
        animationSpec = tween(durationMillis = 300)
    )

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(scrollState.value) {
        isScrollingDown = scrollState.value > lastScrollValue
        lastScrollValue = scrollState.value
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Drawer title", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = "Drawer Item") },
                    selected = false,
                    onClick = { /*TODO*/ },
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    ) {
        Scaffold(
            floatingActionButton = {
                if (currentRoute == MainRoute.Home.name) {
                    HomeFloatingActionButton(
                        fabWidth = fabWidth,
                        fabTextAlpha = fabTextAlpha
                    )
                }
            },
            bottomBar = {
                if (!isScrollingDown) {
                    NavigationBar(
                        modifier = Modifier
                            .offset(y = bottomBarOffset)
                            .fillMaxWidth(),
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
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(MainRoute.Home.name) {
                    HomeScreen(
                        onAccountIconClick = {
                            navController.navigate(MainRoute.Profile.name)
                        },
                        onBurgerIconClick = {
                            scope.launch { drawerState.open() }
                        },
                        scrollState = scrollState
                    )
                }

                composable(MainRoute.Profile.name) {
                    Text("Profile")
                }
            }
        }
    }
}