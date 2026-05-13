package io.alron.fixall.presentation.util

import android.util.Patterns
import java.util.regex.Pattern

object EmailValidator {
    private val EMAIL_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
        "\\@" +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
        "(" +
        "\\." +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
        ")+"
    )

    fun isValid(email: CharSequence?): Boolean {
        if (email.isNullOrBlank()) return false
        return try {
            val patternsEmail = Patterns.EMAIL_ADDRESS
            if (patternsEmail != null) {
                patternsEmail.matcher(email).matches()
            } else {
                EMAIL_PATTERN.matcher(email).matches()
            }
        } catch (e: Throwable) {
            EMAIL_PATTERN.matcher(email).matches()
        }
    }
}
