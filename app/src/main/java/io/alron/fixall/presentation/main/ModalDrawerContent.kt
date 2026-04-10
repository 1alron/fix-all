package io.alron.fixall.presentation.main

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.alron.fixall.R
import io.alron.fixall.presentation.MainRoute
import io.alron.fixall.presentation.ModalDrawerRoute

@Composable
fun ModalDrawerContent(
    currentRoute: String?,
    onHomeClick: () -> Unit,
    onCarsClick: () -> Unit,
    onServiceCentersClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = stringResource(R.string.logo_icon_content_description),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        HorizontalDivider()
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text(stringResource(R.string.general)) },
            selected = currentRoute == MainRoute.Home.name,
            onClick = onHomeClick,
            modifier = Modifier.padding(4.dp)
        )
        HorizontalDivider()
        NavigationDrawerItem(
            icon = { Icon(painterResource(R.drawable.ic_car), contentDescription = null) },
            label = { Text(stringResource(R.string.my_cars)) },
            selected = currentRoute == ModalDrawerRoute.Cars.name,
            onClick = onCarsClick,
            modifier = Modifier.padding(4.dp)
        )
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Place, contentDescription = null) },
            label = { Text(stringResource(R.string.our_branches)) },
            selected = currentRoute == ModalDrawerRoute.ServiceCenters.name,
            onClick = onServiceCentersClick,
            modifier = Modifier.padding(4.dp)
        )
    }
}