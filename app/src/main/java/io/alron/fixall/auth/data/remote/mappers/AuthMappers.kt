package io.alron.fixall.auth.data.remote.mappers

import io.alron.fixall.auth.data.remote.dto.LoginResponseDto
import io.alron.fixall.auth.data.remote.dto.RegisterResponseDto
import io.alron.fixall.auth.domain.model.AuthTokens
import io.alron.fixall.auth.domain.model.RegisterResult

fun LoginResponseDto.toDomain(): AuthTokens =
    AuthTokens(
        access = access,
        refresh = refresh
    )