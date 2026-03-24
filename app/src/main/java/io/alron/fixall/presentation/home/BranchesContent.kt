package io.alron.fixall.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.alron.fixall.R
import io.alron.fixall.domain.model.Branch

@Composable
fun BranchesContent(
    branches: List<Branch>
) {
    Text(
        text = stringResource(R.string.our_branches),
        style = MaterialTheme.typography.titleLarge
    )
    Spacer(Modifier.height(8.dp))
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(branches) { _, item ->
            BranchesRowItem(
                item
            )
        }
    }
}