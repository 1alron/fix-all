package io.alron.fixall.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class ModalDrawerRoute(
    val icon: ImageVector
) {
    Cars(Icons.Default.Settings),
    ServiceCenters(Icons.Default.Place),
    Appointments(Icons.Default.DateRange)
}