package io.alron.fixall.auth.data.repository

import io.alron.fixall.auth.data.remote.api.AuthApi
import io.alron.fixall.auth.data.remote.dto.LoginRequestDto
import io.alron.fixall.auth.data.remote.mappers.toDomain
import io.alron.fixall.auth.data.storage.TokenStorage
import io.alron.fixall.auth.domain.model.AuthTokens
import io.alron.fixall.auth.domain.model.LoginResult
import io.alron.fixall.auth.domain.repository.LoginRepository
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection

class LoginRepositoryImpl(
    private val api: AuthApi,
    private val tokenStorage: TokenStorage
) : LoginRepository {
    override suspend fun login(
        username: String,
        password: String
    ): LoginResult {
        return try {
            val loginResponseDto = api.login(LoginRequestDto(username, password))
            val authTokens = loginResponseDto.toDomain()
            tokenStorage.saveTokens(authTokens)
            return LoginResult.Success(authTokens)
        } catch (e: HttpException) {
            when (e.code()) {
                HttpURLConnection.HTTP_UNAUTHORIZED -> LoginResult.InvalidCredentials
                HttpURLConnection.HTTP_INTERNAL_ERROR -> LoginResult.ServerError
                else -> LoginResult.UnknownError
            }
        } catch (_: IOException) {
            LoginResult.NetworkError
        }
    }

    override suspend fun refreshToken(refreshToken: String): AuthTokens {
        TODO("Implement refresh token")
    }
}