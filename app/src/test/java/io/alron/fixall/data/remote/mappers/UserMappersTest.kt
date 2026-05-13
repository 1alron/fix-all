package io.alron.fixall.data.remote.mappers

import io.alron.fixall.data.remote.dto.*
import org.junit.Assert.assertEquals
import org.junit.Test

class UserMappersTest {

    @Test
    fun userDtoToDomain_mapsCorrectly() {
        val dto = UserDto(
            id = 1,
            username = "testuser",
            email = "test@example.com",
            firstName = "John",
            lastName = "Doe",
            fullName = "John Doe",
            isStaff = true,
            profile = UserProfileDto(
                phone = "123456",
                address = "Main St",
                avatarUrl = "url"
            )
        )
        val domain = dto.toDomain()

        assertEquals(dto.id, domain.id)
        assertEquals(dto.username, domain.username)
        assertEquals(dto.email, domain.email)
        assertEquals(dto.isStaff, domain.isAdmin)
        assertEquals(dto.profile.phone, domain.profile.phone)
    }

    @Test
    fun loyaltyInfoDtoToDomain_mapsCorrectly() {
        val dto = LoyaltyInfoDto(
            status = "GOLD",
            statusDisplay = "Gold Status",
            bonusBalance = 100.0,
            totalSpent = 5000.0,
            nextStatus = "PLATINUM",
            nextStatusProgress = 0.5,
            personalDiscount = 5.0,
            statusDiscount = 10.0,
            totalDiscount = 15.0
        )
        val domain = dto.toDomain()

        assertEquals(dto.status, domain.status)
        assertEquals(dto.statusDisplay, domain.statusDisplay)
        assertEquals(dto.bonusBalance, domain.bonusBalance, 0.001)
        assertEquals(dto.totalSpent, domain.totalSpent, 0.001)
        assertEquals(dto.nextStatus, domain.nextStatus)
        assertEquals(dto.nextStatusProgress, domain.nextStatusProgress, 0.001)
        assertEquals(dto.personalDiscount, domain.personalDiscount, 0.001)
        assertEquals(dto.statusDiscount, domain.statusDiscount, 0.001)
        assertEquals(dto.totalDiscount, domain.totalDiscount, 0.001)
    }

    @Test
    fun statItemDtoToDomain_mapsCorrectly() {
        val dto = StatItemDto(name = "Oil Change", count = 5)
        val domain = dto.toDomain()

        assertEquals("Oil Change", domain.name)
        assertEquals(5, domain.count)
    }
}
