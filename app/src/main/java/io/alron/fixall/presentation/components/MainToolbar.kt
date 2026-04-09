package io.alron.fixall.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainToolbar(
    title: String,
    onNavigationIconClick: () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: ImageVector = Icons.Default.Menu,
    onActionIconClick: (() -> Unit)? = null,
    actionIcon: ImageVector = Icons.Default.AccountCircle
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(
                    imageVector = navigationIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        actions = {
            if (onActionIconClick != null) {
                IconButton(onClick = onActionIconClick) {
                    Icon(
                        imageVector = actionIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        windowInsets = TopAppBarDefaults.windowInsets
    )
}
