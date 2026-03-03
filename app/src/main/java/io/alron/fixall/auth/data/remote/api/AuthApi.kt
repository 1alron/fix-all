package io.alron.fixall.auth.data.remote.api

import io.alron.fixall.auth.data.remote.dto.LoginRequestDto
import io.alron.fixall.auth.data.remote.dto.LoginResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/api/token/")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto
}