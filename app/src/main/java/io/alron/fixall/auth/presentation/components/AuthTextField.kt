package io.alron.fixall.auth.presentation.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.alron.fixall.R

@Composable
fun AuthTextField(
    @StringRes labelRes: Int,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    @StringRes errorDescriptionRes: Int? = null,
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = stringResource(labelRes)) },
        visualTransformation =
            if (isPassword && !passwordVisible)
                PasswordVisualTransformation()
            else
                VisualTransformation.None,
        isError = errorDescriptionRes != null,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword)
                KeyboardType.Password
            else
                KeyboardType.Text
        ),
        supportingText = {
            if (errorDescriptionRes != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = stringResource(errorDescriptionRes),
                        modifier = Modifier.size(16.dp).padding(end = 2.dp)
                    )
                    Text(
                        text = stringResource(errorDescriptionRes),
                        color = MaterialTheme.colorScheme.error
                    )
                }

            }
        },
        trailingIcon = if (isPassword) {
            {
                val painter = if (passwordVisible)
                    painterResource(R.drawable.ic_visibility_off)
                else
                    painterResource(R.drawable.ic_visibility)

                val description = if (passwordVisible)
                    stringResource(R.string.hide_password)
                else
                    stringResource(R.string.show_password)

                IconButton(onClick = {
                    passwordVisible = !passwordVisible
                }) {
                    Icon(painter = painter, contentDescription = description)
                }
            }
        } else null,
        modifier = modifier
    )
}