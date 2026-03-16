package io.alron.fixall.auth.domain.usecase

import io.alron.fixall.auth.domain.model.LoginResult
import io.alron.fixall.auth.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(username: String, password: String): LoginResult {
        return repository.login(username, password)
    }
}