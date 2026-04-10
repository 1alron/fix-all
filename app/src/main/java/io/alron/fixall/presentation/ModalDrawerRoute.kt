package io.alron.fixall.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class ModalDrawerRoute(
    val icon: ImageVector
) {
    Cars(Icons.Default.Settings), // Using Settings as a placeholder if DirectionsCar is missing
    ServiceCenters(Icons.Default.Place)
}