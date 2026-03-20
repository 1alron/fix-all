package io.alron.fixall.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.graphics.vector.ImageVector

enum class MainRoute(val icon: ImageVector) {
    Home(Icons.Default.Home),
    Branches(Icons.Default.LocationOn),
    Appointments(Icons.Default.Build),
    Profile(Icons.Default.AccountBox)
}