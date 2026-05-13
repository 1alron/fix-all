package io.alron.fixall.data.remote.mappers

import io.alron.fixall.data.remote.dto.LoginResponseDto
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthMappersTest {

    @Test
    fun `loginResponseDto toDomain maps tokens correctly`() {
        val dto = LoginResponseDto(access = "access_token", refresh = "refresh_token")
        val domain = dto.toDomain()

        assertEquals("access_token", domain.access)
        assertEquals("refresh_token", domain.refresh)
    }
}
