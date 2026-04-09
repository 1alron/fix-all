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
import androidx.compose.material3.Scaffold
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

    Scaffold(
        topBar = {
            MainToolbar(
                title = stringResource(R.string.general),
                onNavigationIconClick = onBurgerIconClick,
                onActionIconClick = onAccountIconClick
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.refresh() },
        ) {
            when {
                state.branches.isNotEmpty() -> {
                    HomeScreenContent(
                        scrollState = scrollState,
                        onLogout = { viewModel.logout() },
                        branches = state.branches
                    )
                }

                state.errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
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
}


@Composable
fun HomeScreenContent(
    onLogout: () -> Unit,
    scrollState: ScrollState,
    branches: List<Branch>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        ServiceInstructionContent(items = StepProvider.provideSteps())
        Spacer(Modifier.height(24.dp))
        BranchesContent(
            branches = branches
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onLogout,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(R.string.logout))
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun ServiceInstructionContent(
    items: List<Step>,
) {
    Text(
        text = stringResource(R.string.how_service_works),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    )
    Spacer(Modifier.height(12.dp))
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(items) { _, item ->
            StepRowItem(item)
        }
    }
}
