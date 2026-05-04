package io.alron.fixall.presentation.registration

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import io.alron.fixall.R
import io.alron.fixall.presentation.components.AuthButton
import io.alron.fixall.presentation.components.AuthCheckbox
import io.alron.fixall.presentation.components.AuthClickableText
import io.alron.fixall.presentation.components.AuthErrorMessage
import io.alron.fixall.presentation.components.AuthHeader
import io.alron.fixall.presentation.components.AuthPolicyAndTermsText
import io.alron.fixall.presentation.components.AuthTextField
import io.alron.fixall.presentation.components.AuthTopBar

@Composable
fun RegistrationScreen(
    onBackToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegistrationViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsState()

    val handleBack = {
        if (uiState.value.step > 1) viewModel.onPreviousStep()
        else onBackToLogin()
    }

    BackHandler(enabled = true) {
        handleBack()
    }

    Scaffold(
        topBar = {
            AuthTopBar(
                onBack = {
                    handleBack()
                }
            )
        }
    )
    { innerPadding ->
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
                    stringRes = R.string.registration,
                )
                Spacer(Modifier.height(12.dp))

                if (uiState.value.step == 1) {
                    AuthTextField(
                        value = uiState.value.firstName,
                        onValueChange = { viewModel.updateFirstName(it) },
                        labelRes = R.string.first_name,
                        modifier = Modifier.fillMaxWidth(),
                        error = uiState.value.firstNameError,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AuthTextField(
                        value = uiState.value.lastName,
                        onValueChange = { viewModel.updateLastName(it) },
                        labelRes = R.string.last_name,
                        modifier = Modifier.fillMaxWidth(),
                        error = uiState.value.lastNameError,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AuthTextField(
                        value = uiState.value.username,
                        onValueChange = { viewModel.updateUsername(it) },
                        labelRes = R.string.username,
                        modifier = Modifier.fillMaxWidth(),
                        error = uiState.value.usernameError,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AuthTextField(
                        value = uiState.value.email,
                        onValueChange = { viewModel.updateEmail(it) },
                        labelRes = R.string.email,
                        modifier = Modifier.fillMaxWidth(),
                        error = uiState.value.emailError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { viewModel.onNextStep() })
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    AuthButton(
                        stringRes = R.string.continue_button,
                        onClick = { viewModel.onNextStep() },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    AuthTextField(
                        value = uiState.value.password,
                        onValueChange = { viewModel.updatePassword(it) },
                        labelRes = R.string.password,
                        isPassword = true,
                        modifier = Modifier.fillMaxWidth(),
                        error = uiState.value.passwordError,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AuthTextField(
                        value = uiState.value.confirmPassword,
                        onValueChange = { viewModel.updateConfirmPassword(it) },
                        labelRes = R.string.confirm_password,
                        isPassword = true,
                        modifier = Modifier.fillMaxWidth(),
                        error = uiState.value.confirmPasswordError,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            viewModel.register()
                        })
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                        AuthCheckbox(
                            checked = uiState.value.isCheckedAgreement,
                            onCheckedChange = { viewModel.onCheckedChange() },
                        )
                        Spacer(Modifier.width(8.dp))
                        AuthPolicyAndTermsText()
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    AuthButton(
                        isClickable = uiState.value.isCheckedAgreement,
                        stringRes = R.string.register,
                        isLoading = uiState.value.isLoading,
                        onClick = { viewModel.register() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(Modifier.height(16.dp))
                uiState.value.networkError?.let { error ->
                    AuthErrorMessage(error)
                    Spacer(Modifier.height(16.dp))
                }
                Row(
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    Text(
                        text = stringResource(R.string.already_have_account),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    AuthClickableText(
                        textRes = R.string.log_in,
                        onClick = onBackToLogin
                    )
                }
            }
        }
    }
}