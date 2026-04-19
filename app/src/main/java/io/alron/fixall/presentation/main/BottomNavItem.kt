package io.alron.fixall.presentation.main

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val labelRes: Int
)