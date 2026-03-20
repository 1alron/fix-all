package io.alron.fixall.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.alron.fixall.R
import io.alron.fixall.domain.model.Branch
import io.alron.fixall.presentation.theme.FixAllTheme

@Composable
fun HomeScreen(
    onToolbarIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        HomeToolbar(
            onIconClick = onToolbarIconClick,
        )
        Spacer(Modifier.height(20.dp))

        when (val currentState = state) {
            is HomeState.Content -> {
                BranchesContent(
                    branches = currentState.branches
                )
            }

            HomeState.Error -> {
                Box(Modifier
                    .fillMaxWidth()
                    .height(200.dp), contentAlignment = Alignment.Center) {
                    Text("Ошибка!")
                }
            }

            HomeState.Loading -> {
                Box(Modifier
                    .fillMaxWidth()
                    .height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

        Button(
            onClick = { viewModel.logout() }
        ) {
            Text(stringResource(R.string.logout))
        }
    }
}

@Composable
fun BranchesContent(
    branches: List<Branch>
) {
    Text(
        text = "Наши филиалы",
        style = MaterialTheme.typography.titleLarge
    )
    Spacer(Modifier.height(8.dp))
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(branches) { index, item ->
            Card(
                modifier = Modifier.size(200.dp)
            ) {
                Text("Филиал $index - $item")
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun Preview() {
    FixAllTheme {
        HomeScreen(
            onToolbarIconClick = {},
            modifier = Modifier.safeDrawingPadding()
        )
    }
}