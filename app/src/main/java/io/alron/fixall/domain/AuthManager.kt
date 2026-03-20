package io.alron.fixall.domain

import io.alron.fixall.domain.model.LoginResult
import io.alron.fixall.domain.model.RegisterResult
import io.alron.fixall.domain.repository.AuthRepository
import io.alron.fixall.domain.repository.TokenStorageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthManager(
    private val authRepository: AuthRepository,
    private val tokenStorageRepository: TokenStorageRepository
) {
    private val _isAuthorized = MutableStateFlow(false)
    val isAuthorized: StateFlow<Boolean> = _isAuthorized

    init {
        val token = tokenStorageRepository.getAccessToken()
        _isAuthorized.value = !token.isNullOrEmpty()
    }

    suspend fun login(username: String, password: String): LoginResult {
        val result = authRepository.login(username, password)
        if (result is LoginResult.Success) {
            _isAuthorized.value = true
        }
        return result
    }

    suspend fun register(
        username: String,
        firstName: String,
        lastName: String,
        email: String,
        password1: String,
        password2: String
    ): RegisterResult {
        val result = authRepository.register(
            username, firstName, lastName, email, password1, password2
        )
        if (result is RegisterResult.Success) {
            //todo: сделать обработку ошибок
            login(username, password1)
        }
        return result
    }

    fun logout() {
        tokenStorageRepository.clear()
        _isAuthorized.value = false
    }

    fun getAccessToken(): String? = tokenStorageRepository.getAccessToken()
}