package io.alron.fixall.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

enum class MainRoute(val icon: ImageVector? = null) {
    Home(Icons.Default.Home),
    Profile(Icons.Default.AccountBox),

    AddCar
}