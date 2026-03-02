package io.alron.fixall.auth.domain.usecase

import io.alron.fixall.auth.domain.model.LoginResult
import io.alron.fixall.auth.domain.repository.LoginRepository

class LoginUseCase(private val repository: LoginRepository) {
    suspend operator fun invoke(username: String, password: String): LoginResult {
        return repository.login(username, password)
    }
}