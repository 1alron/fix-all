package io.alron.fixall.auth.data.remote.mappers

import io.alron.fixall.auth.data.remote.dto.LoginResponseDto
import io.alron.fixall.auth.domain.model.AuthTokens

fun LoginResponseDto.toDomain(): AuthTokens =
    AuthTokens(
        access = access,
        refresh = refresh
    )