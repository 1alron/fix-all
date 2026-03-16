package io.alron.fixall.auth.presentation.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import io.alron.fixall.R
import io.alron.fixall.auth.presentation.components.AuthButton
import io.alron.fixall.auth.presentation.components.AuthClickableText
import io.alron.fixall.auth.presentation.components.AuthErrorMessage
import io.alron.fixall.auth.presentation.components.AuthHeader
import io.alron.fixall.auth.presentation.components.AuthTextField
import io.alron.fixall.auth.presentation.components.AuthTopBar

@Composable
fun LoginScreen(
    onNavigateToRegistration: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AuthTopBar()
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 580.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
            ) {
                Spacer(Modifier.height(68.dp))
                AuthHeader(
                    stringRes = R.string.logging_in,
                )
                Spacer(Modifier.height(12.dp))
                AuthTextField(
                    value = uiState.value.username,
                    onValueChange = { viewModel.updateUsername(it) },
                    labelRes = R.string.username,
                    modifier = Modifier.fillMaxWidth(),
                    error = uiState.value.usernameError,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                AuthTextField(
                    value = uiState.value.password,
                    onValueChange = { viewModel.updatePassword(it) },
                    labelRes = R.string.password,
                    isPassword = true,
                    modifier = Modifier.fillMaxWidth(),
                    error = uiState.value.passwordError,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { viewModel.login() }
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                AuthButton(
                    stringRes = R.string.log_in,
                    isLoading = uiState.value.isLoading,
                    onClick = { viewModel.login() },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                uiState.value.networkError?.let { error ->
                    AuthErrorMessage(error)
                    Spacer(Modifier.height(16.dp))
                }
                Row(
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    Text(
                        text = stringResource(R.string.no_account),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    AuthClickableText(
                        textRes = R.string.create_it_free,
                        onClick = onNavigateToRegistration
                    )
                }
            }
        }
    }
}