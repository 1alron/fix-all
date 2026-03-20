package io.alron.fixall.data.remote.mappers

import io.alron.fixall.data.remote.dto.LoginResponseDto
import io.alron.fixall.domain.model.AuthTokens

fun LoginResponseDto.toDomain(): AuthTokens =
    AuthTokens(
        access = access,
        refresh = refresh
    )