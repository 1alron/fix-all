package io.alron.fixall.presentation.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import io.alron.fixall.R
import io.alron.fixall.domain.model.Car
import io.alron.fixall.presentation.MainRoute
import io.alron.fixall.presentation.ModalDrawerRoute
import io.alron.fixall.presentation.admin.AdminDashboardScreen
import io.alron.fixall.presentation.admin.appointments.AdminAppointmentsScreen
import io.alron.fixall.presentation.admin.appointments.details.AdminAppointmentDetailScreen
import io.alron.fixall.presentation.admin.branches.AdminBranchesScreen
import io.alron.fixall.presentation.admin.branches.add_edit.AdminAddEditBranchScreen
import io.alron.fixall.presentation.admin.branches.detail.AdminBranchDetailScreen
import io.alron.fixall.presentation.admin.reviews.AdminReviewsScreen
import io.alron.fixall.presentation.admin.services.AdminServicesScreen
import io.alron.fixall.presentation.appointments.AppointmentsListScreen
import io.alron.fixall.presentation.appointments.create.CreateAppointmentScreen
import io.alron.fixall.presentation.appointments.details.AppointmentDetailsScreen
import io.alron.fixall.presentation.cars.AddCarScreen
import io.alron.fixall.presentation.cars.CarsScreen
import io.alron.fixall.presentation.home.HomeScreen
import io.alron.fixall.presentation.profile.ProfileScreen
import io.alron.fixall.presentation.profile.stats.StatsScreen
import io.alron.fixall.presentation.service_centers.ServiceCentersScreen
import io.alron.fixall.presentation.service_centers.details.ServiceCenterDetailsScreen
import io.alron.fixall.presentation.service_centers.reviews.ServiceCenterReviewsScreen

@Composable
fun MainNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route
    val startDestination = MainRoute.Home

    val scrollState = rememberScrollState()

    val bottomNavItems = listOf(
        BottomNavItem(MainRoute.Home.name, Icons.Default.Home, R.string.general),
        BottomNavItem(ModalDrawerRoute.Cars.name, Icons.Default.Settings, R.string.my_cars),
        BottomNavItem(ModalDrawerRoute.ServiceCenters.name, Icons.Default.Place, R.string.branches),
        BottomNavItem(
            ModalDrawerRoute.Appointments.name,
            Icons.Default.DateRange,
            R.string.my_appointments
        ),
        BottomNavItem(MainRoute.Profile.name, Icons.Default.Person, R.string.go_to_profile)
    )

    val navigateToTab: (String) -> Unit = { route ->
        if (currentRoute != route) {
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    val bottomScreenWithBarSpacer = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding() + 48.dp

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            containerColor = MaterialTheme.colorScheme.background
        ) { _ ->
            NavHost(
                navController = navController,
                startDestination = startDestination.name,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(MainRoute.Home.name) {
                    HomeScreen(
                        onAccountIconClick = { navigateToTab(MainRoute.Profile.name) },
                        onAddAppointmentClick = { navController.navigate("create_appointment") },
                        onFindBranchClick = { navigateToTab(ModalDrawerRoute.ServiceCenters.name) },
                        onAppointmentClick = { id -> navController.navigate("appointment_details/$id") },
                        scrollState = scrollState,
                        bottomSpacer = bottomScreenWithBarSpacer
                    )
                }

                composable(MainRoute.Profile.name) {
                    ProfileScreen(
                        onStatsClick = { navController.navigate(MainRoute.Stats.name) },
                        onAdminClick = { navController.navigate(MainRoute.AdminDashboard.name) },
                        bottomSpacer = bottomScreenWithBarSpacer
                    )
                }

                composable(MainRoute.Stats.name) {
                    StatsScreen(onBack = { navController.popBackStack() })
                }

                composable(MainRoute.AdminDashboard.name) {
                    AdminDashboardScreen(
                        onBack = { navController.popBackStack() },
                        onNewAppointmentClick = { navController.navigate("create_appointment") },
                        onAllAppointmentsClick = { navController.navigate(MainRoute.AdminAppointments.name) },
                        onAppointmentClick = { id ->
                            navController.navigate("${MainRoute.AdminAppointmentDetail.name}/$id")
                        },
                        onReviewsClick = { navController.navigate(MainRoute.AdminReviews.name) },
                        onServicesClick = { navController.navigate(MainRoute.AdminServices.name) },
                        onBranchesClick = { navController.navigate(MainRoute.AdminBranches.name) }
                    )
                }

                composable(MainRoute.AdminAppointments.name) {
                    AdminAppointmentsScreen(
                        onBack = { navController.popBackStack() },
                        onAppointmentClick = { id -> 
                            navController.navigate("${MainRoute.AdminAppointmentDetail.name}/$id")
                        }
                    )
                }

                composable(
                    route = "${MainRoute.AdminAppointmentDetail.name}/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) {
                    AdminAppointmentDetailScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(MainRoute.AdminReviews.name) {
                    AdminReviewsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(MainRoute.AdminServices.name) {
                    AdminServicesScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(MainRoute.AdminBranches.name) {
                    AdminBranchesScreen(
                        onBack = { navController.popBackStack() },
                        onAddBranch = { navController.navigate(MainRoute.AdminAddEditBranch.name) },
                        onBranchClick = { id ->
                            navController.navigate("${MainRoute.AdminBranchDetail.name}/$id")
                        }
                    )
                }

                composable(
                    route = "${MainRoute.AdminBranchDetail.name}/{branchId}",
                    arguments = listOf(navArgument("branchId") { type = NavType.StringType })
                ) {
                    AdminBranchDetailScreen(
                        onBack = { navController.popBackStack() },
                        onEditBranch = { id ->
                            navController.navigate("${MainRoute.AdminAddEditBranch.name}?branchId=$id")
                        },
                        onAppointmentClick = { id ->
                            navController.navigate("${MainRoute.AdminAppointmentDetail.name}/$id")
                        }
                    )
                }

                composable(
                    route = MainRoute.AdminAddEditBranch.name + "?branchId={branchId}",
                    arguments = listOf(
                        navArgument("branchId") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) {
                    AdminAddEditBranchScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(ModalDrawerRoute.Cars.name) {
                    CarsScreen(
                        onAccountIconClick = { navigateToTab(MainRoute.Profile.name) },
                        onAddCarClick = { navController.navigate(MainRoute.AddCar.name) },
                        onEditCarClick = { car ->
                            val carJson = Gson().toJson(car)
                            navController.navigate(MainRoute.AddCar.name + "?car=${carJson}")
                        },
                        bottomSpacer = bottomScreenWithBarSpacer
                    )
                }

                composable(ModalDrawerRoute.ServiceCenters.name) {
                    ServiceCentersScreen(
                        onAccountIconClick = { navigateToTab(MainRoute.Profile.name) },
                        onServiceCenterClick = { id -> navController.navigate("service_center_details/$id") },
                        bottomSpacer = bottomScreenWithBarSpacer
                    )
                }

                composable(
                    route = "service_center_details/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) {
                    ServiceCenterDetailsScreen(
                        onBack = { navController.popBackStack() },
                        onShowReviewsClick = { id -> navController.navigate("service_center_reviews/$id") }
                    )
                }

                composable(
                    route = "service_center_reviews/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) {
                    ServiceCenterReviewsScreen(onBack = { navController.popBackStack() })
                }

                composable(ModalDrawerRoute.Appointments.name) {
                    AppointmentsListScreen(
                        onAccountIconClick = { navigateToTab(MainRoute.Profile.name) },
                        onAddAppointmentClick = { navController.navigate("create_appointment") },
                        onAppointmentClick = { id -> navController.navigate("appointment_details/$id") },
                        bottomSpacer = bottomScreenWithBarSpacer
                    )
                }

                composable(
                    route = "appointment_details/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) {
                    AppointmentDetailsScreen(onBack = { navController.popBackStack() })
                }

                composable("create_appointment") {
                    CreateAppointmentScreen(
                        onBack = { navController.popBackStack() },
                        onNavigateToAddCar = { navController.navigate(MainRoute.AddCar.name) },
                        onSuccess = { navController.popBackStack() }
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
                        onSuccess = { navController.popBackStack() },
                        initialCar = car
                    )
                }
            }
        }

        val showBottomBar = bottomNavItems.any { it.route == currentRoute }

        if (showBottomBar) {
            LiquidBottomBar(
                items = bottomNavItems,
                currentDestination = currentDestination,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        bottom = WindowInsets.navigationBars
                            .asPaddingValues()
                            .calculateBottomPadding() + 16.dp
                    ),
                onNavigate = { route -> navigateToTab(route) }
            )
        }
    }
}
