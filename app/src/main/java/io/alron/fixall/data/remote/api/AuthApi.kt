package io.alron.fixall.data.remote.api

import io.alron.fixall.data.remote.dto.LoginRequestDto
import io.alron.fixall.data.remote.dto.LoginResponseDto
import io.alron.fixall.data.remote.dto.RefreshRequestDto
import io.alron.fixall.data.remote.dto.RefreshResponseDto
import io.alron.fixall.data.remote.dto.RegisterRequestDto
import io.alron.fixall.data.remote.dto.RegisterResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/api/token/")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

    @POST("/api/register/")
    suspend fun register(@Body request: RegisterRequestDto): RegisterResponseDto

    @POST("/api/token/refresh/")
    suspend fun refreshToken(@Body request: RefreshRequestDto): RefreshResponseDto
}