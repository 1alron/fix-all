package io.alron.fixall.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

@Composable
fun StepRowItem(
    item: Step,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column {
            Icon(
                imageVector = item.vect,
                contentDescription = null
            )
            Text(text = item.title)
            Text(text = item.description)
        }
    }
}