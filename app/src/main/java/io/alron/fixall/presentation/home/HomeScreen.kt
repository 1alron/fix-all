package io.alron.fixall.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import io.alron.fixall.R
import io.alron.fixall.presentation.theme.FixAllTheme

@Composable
fun HomeScreen(
    onToolbarIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: HomeViewModel = hiltViewModel()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        HomeToolbar(
            onIconClick = onToolbarIconClick,
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { viewModel.logout() }
        ) {
            Text(stringResource(R.string.logout))
        }
    }
}