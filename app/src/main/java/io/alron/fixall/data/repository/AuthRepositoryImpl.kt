package io.alron.fixall.data.repository

import com.google.gson.Gson
import io.alron.fixall.data.remote.api.AuthApi
import io.alron.fixall.data.remote.dto.LoginRequestDto
import io.alron.fixall.data.remote.dto.RefreshRequestDto
import io.alron.fixall.data.remote.dto.RegisterRequestDto
import io.alron.fixall.data.remote.dto.RegisterResponseDto
import io.alron.fixall.data.remote.mappers.toDomain
import io.alron.fixall.domain.repository.TokenStorageRepository
import io.alron.fixall.domain.model.AuthTokens
import io.alron.fixall.domain.model.LoginResult
import io.alron.fixall.domain.model.RegisterResult
import io.alron.fixall.domain.repository.AuthRepository
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val tokenStorageRepository: TokenStorageRepository,
    private val gson: Gson
) : AuthRepository {
    override suspend fun login(
        username: String,
        password: String
    ): LoginResult {
        return try {
            val loginResponseDto = api.login(LoginRequestDto(username, password))
            val authTokens = loginResponseDto.toDomain()
            tokenStorageRepository.saveTokens(authTokens)
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

    override suspend fun register(
        username: String,
        firstName: String,
        lastName: String,
        email: String,
        password1: String,
        password2: String
    ): RegisterResult {
        return try {
            val registerResponseDto = api.register(
                RegisterRequestDto(
                    username = username,
                    first_name = firstName,
                    last_name = lastName,
                    email = email,
                    password1 = password1,
                    password2 = password2
                )
            )
            if (registerResponseDto.success) {
                RegisterResult.Success
            } else {
                RegisterResult.Error(fieldErrors = registerResponseDto.errors)
            }
        } catch (e: HttpException) {
            when (e.code()) {
                HttpURLConnection.HTTP_BAD_REQUEST -> {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = try {
                        gson.fromJson(errorBody, RegisterResponseDto::class.java)
                    } catch (_: Exception) {
                        null
                    }
                    RegisterResult.Error(
                        message = e.message(),
                        fieldErrors = errorResponse?.errors
                    )
                }
                HttpURLConnection.HTTP_INTERNAL_ERROR -> RegisterResult.ServerError
                else -> RegisterResult.UnknownError
            }
        } catch (_: IOException) {
            RegisterResult.NetworkError
        }
    }

    override suspend fun refreshToken(refreshToken: String): AuthTokens? {
        return try {
            val response = api.refreshToken(
                RefreshRequestDto(refreshToken)
            )

            val newTokens = AuthTokens(
                access = response.access,
                refresh = refreshToken
            )

            tokenStorageRepository.saveTokens(newTokens)
            newTokens
        } catch (e: HttpException) {
            when (e.code()) {
                HttpURLConnection.HTTP_UNAUTHORIZED -> {
                    tokenStorageRepository.clear()
                    null
                }
                else -> null
            }
        } catch (_: IOException) {
            null
        }
    }
}