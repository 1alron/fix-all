package io.alron.fixall.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.alron.fixall.R
import io.alron.fixall.presentation.theme.FixAllTheme

@Composable
fun HomeToolbar(
    onIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.general),
            style = MaterialTheme.typography.headlineSmall
        )
        IconButton(
            onClick = onIconClick,
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = stringResource(R.string.profile_icon),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(36.dp)
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    FixAllTheme {
        HomeToolbar(onIconClick = { })
    }
}
