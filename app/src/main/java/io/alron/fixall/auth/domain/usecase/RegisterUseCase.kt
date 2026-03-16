package io.alron.fixall.auth.domain.usecase

import io.alron.fixall.auth.domain.model.RegisterResult
import io.alron.fixall.auth.domain.repository.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        username: String,
        firstName: String,
        lastName: String,
        email: String,
        password1: String,
        password2: String
    ): RegisterResult {
        return repository.register(
            username,
            firstName,
            lastName,
            email,
            password1,
            password2
        )
    }
}