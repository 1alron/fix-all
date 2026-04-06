package io.alron.fixall.presentation.home

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.alron.fixall.R
import io.alron.fixall.domain.model.Branch
import io.alron.fixall.presentation.components.MainToolbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    scrollState: ScrollState,
    onBurgerIconClick: () -> Unit,
    onAccountIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    PullToRefreshBox(
        modifier = modifier.fillMaxSize(),
        isRefreshing = state.isRefreshing,
        onRefresh = { viewModel.refresh() },
    ) {
        when {
            state.branches.isNotEmpty() -> {
                HomeScreenContent(
                    scrollState = scrollState,
                    onLogout = { viewModel.logout() },
                    onAccountIconClick = onAccountIconClick,
                    onBurgerIconClick = onBurgerIconClick,
                    branches = state.branches,
                    modifier = modifier
                )
            }

            state.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(state.errorMessage!!)
                }
            }

            state.isLoading -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    // Временно
    onLogout: () -> Unit,

    scrollState: ScrollState,
    onBurgerIconClick: () -> Unit,
    onAccountIconClick: () -> Unit,
    branches: List<Branch>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(12.dp)
            .verticalScroll(scrollState)
    ) {
        MainToolbar(
            text = stringResource(R.string.general),
            onBurgerIconClick = onBurgerIconClick,
            onAccountIconClick = onAccountIconClick,
        )
        Spacer(Modifier.height(20.dp))
        ServiceInstructionContent(items = StepProvider.provideSteps())
        Spacer(Modifier.height(16.dp))
        BranchesContent(
            branches = branches
        )
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onLogout
        ) {
            Text(stringResource(R.string.logout))
        }
        Spacer(Modifier.height(1000.dp))
    }
}

@Composable
fun ServiceInstructionContent(
    items: List<Step>,
) {
    Text(
        text = stringResource(R.string.how_service_works),
        style = MaterialTheme.typography.titleLarge
    )
    Spacer(Modifier.height(8.dp))
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(items) { _, item ->
            StepRowItem(item)
        }
    }
}