package io.alron.fixall.presentation.main

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import io.alron.fixall.domain.model.Car
import io.alron.fixall.presentation.MainRoute
import io.alron.fixall.presentation.ModalDrawerRoute
import io.alron.fixall.presentation.cars.AddCarScreen
import io.alron.fixall.presentation.cars.CarsFloatingActionButton
import io.alron.fixall.presentation.cars.CarsScreen
import io.alron.fixall.presentation.home.HomeFloatingActionButton
import io.alron.fixall.presentation.home.HomeScreen
import io.alron.fixall.presentation.service_centers.ServiceCentersScreen
import io.alron.fixall.presentation.service_centers.details.ServiceCenterDetailsScreen
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

    val fabWidth by animateDpAsState(
        targetValue = if (isScrollingDown) 56.dp else 180.dp,
        animationSpec = tween(durationMillis = 300),
        label = ""
    )
    val fabTextAlpha by animateFloatAsState(
        targetValue = if (isScrollingDown) 0f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = ""
    )

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    fun navigateWithModalDrawer(
        route: String
    ) {
        scope.launch {
            drawerState.close()
            navController.navigate(route) {
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(scrollState.value) {
        isScrollingDown = scrollState.value > lastScrollValue
        lastScrollValue = scrollState.value
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerContent(
                currentRoute = currentRoute,
                onHomeClick = {
                    navigateWithModalDrawer(MainRoute.Home.name)
                },
                onCarsClick = {
                    navigateWithModalDrawer(ModalDrawerRoute.Cars.name)
                },
                onServiceCentersClick = {
                    navigateWithModalDrawer(ModalDrawerRoute.ServiceCenters.name)
                }
            )
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

                if (currentRoute == ModalDrawerRoute.Cars.name) {
                    CarsFloatingActionButton(
                        onClick = {
                            navController.navigate(MainRoute.AddCar.name)
                        }
                    )
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

                composable(ModalDrawerRoute.Cars.name) {
                    CarsScreen(
                        onAccountIconClick = {
                            navController.navigate(MainRoute.Profile.name)
                        },
                        onBurgerIconClick = {
                            scope.launch { drawerState.open() }
                        },
                        onAddCarClick = {
                            navController.navigate(MainRoute.AddCar.name)
                        },
                        onEditCarClick = { car ->
                            val carJson = Gson().toJson(car)
                            navController.navigate(MainRoute.AddCar.name + "?car=${carJson}")
                        }
                    )
                }

                composable(ModalDrawerRoute.ServiceCenters.name) {
                    ServiceCentersScreen(
                        onBurgerIconClick = {
                            scope.launch { drawerState.open() }
                        },
                        onAccountIconClick = {
                            navController.navigate(MainRoute.Profile.name)
                        },
                        onServiceCenterClick = { id ->
                            navController.navigate("service_center_details/$id")
                        }
                    )
                }

                composable(
                    route = "service_center_details/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) {
                    ServiceCenterDetailsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = MainRoute.AddCar.name + "?car={car}",
                    arguments = listOf(
                        navArgument("car") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val carJson = backStackEntry.arguments?.getString("car")
                    val car = carJson?.let { Gson().fromJson(it, Car::class.java) }
                    
                    AddCarScreen(
                        onBack = { navController.popBackStack() },
                        onSuccess = {
                            navController.popBackStack()
                        },
                        initialCar = car
                    )
                }
            }
        }
    }
}
