package io.alron.fixall.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import io.alron.fixall.BuildConfig
import io.alron.fixall.R

@Composable
fun AuthPolicyAndTermsText() {
    Text(
        buildAnnotatedString {
            withStyle(SpanStyle(fontSize = 14.sp)) {
                append(stringResource(R.string.confirm_you_agree_with))
                append(" ")
            }
            withLink(
                LinkAnnotation.Url(
                    url = "${BuildConfig.BASE_URL}/privacy-policy/",
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp
                        )
                    )
                )
            ) {
                append(stringResource(R.string.policy))
                append(" ")
            }
            withStyle(SpanStyle(fontSize = 14.sp)) {
                append(stringResource(R.string.and))
                append(" ")
            }
            withLink(
                LinkAnnotation.Url(
                    url = "${BuildConfig.BASE_URL}/terms-of-service/",
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp
                        )
                    )
                )
            ) {
                append(stringResource(R.string.terms_of_use))
            }
        }
    )
}