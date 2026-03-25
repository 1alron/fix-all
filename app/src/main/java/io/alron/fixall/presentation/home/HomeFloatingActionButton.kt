package io.alron.fixall.presentation.home

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.alron.fixall.R

@Composable
fun HomeFloatingActionButton(
    fabWidth: Dp,
    fabTextAlpha: Float,
    modifier: Modifier = Modifier
) {
    ExtendedFloatingActionButton(
        onClick = { },
        icon = { Icon(Icons.Default.Add,
            contentDescription = stringResource(R.string.create_appointment)) },
        text = {
            Text(
                stringResource(R.string.create_appointment),
                maxLines = 1,
                modifier = Modifier.alpha(fabTextAlpha)
            )
        },
        modifier = modifier
            .width(fabWidth)
            .height(56.dp)
    )
}