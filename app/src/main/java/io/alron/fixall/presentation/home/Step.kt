package io.alron.fixall.presentation.home

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector

data class Step(
    val vect: ImageVector,
    val title: String,
    val description: String
)