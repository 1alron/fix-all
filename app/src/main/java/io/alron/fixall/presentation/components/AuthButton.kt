package io.alron.fixall.presentation.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun AuthButton(
    @StringRes stringRes: Int,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isClickable: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = !isLoading && isClickable,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text(stringResource(stringRes))
        }
    }
}