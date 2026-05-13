package io.alron.fixall.data.remote.mappers

import io.alron.fixall.data.remote.dto.BranchDto
import io.alron.fixall.data.remote.dto.ReviewDto
import io.alron.fixall.data.remote.dto.ServiceDto
import io.alron.fixall.data.remote.dto.WorkingHourDto
import org.junit.Assert.assertEquals
import org.junit.Test

class BranchesMappersTest {

    @Test
    fun serviceDtoToDomain_mapsCorrectly() {
        val dto = ServiceDto(
            id = "s1",
            name = "Oil Change",
            description = "Basic oil change",
            duration = 30,
            price = "50.0",
            isActive = true
        )
        val domain = dto.toDomain()

        assertEquals(dto.id, domain.id)
        assertEquals(dto.name, domain.name)
        assertEquals(dto.description, domain.description)
        assertEquals(dto.duration, domain.duration)
        assertEquals(dto.price, domain.price)
        assertEquals(dto.isActive, domain.isActive)
    }

    @Test
    fun workingHourDtoToDomain_mapsCorrectly() {
        val dto = WorkingHourDto(
            id = 1,
            dayOfWeek = 1,
            dayOfWeekDisplay = "Mon",
            startTime = "09:00",
            endTime = "18:00",
            lunchStart = "13:00",
            lunchEnd = "14:00",
            isWorking = true
        )
        val domain = dto.toDomain()

        assertEquals(dto.id, domain.id)
        assertEquals(dto.dayOfWeek, domain.dayOfWeek)
        assertEquals(dto.dayOfWeekDisplay, domain.dayOfWeekDisplay)
        assertEquals(dto.startTime, domain.startTime)
        assertEquals(dto.endTime, domain.endTime)
        assertEquals(dto.lunchStart, domain.lunchStart)
        assertEquals(dto.lunchEnd, domain.lunchEnd)
        assertEquals(dto.isWorking, domain.isWorking)
    }

    @Test
    fun reviewDtoToDomain_mapsCorrectly() {
        val dto = ReviewDto(
            id = "r1",
            userName = "John Doe",
            userAvatar = "url",
            rating = 5,
            comment = "Great service",
            adminReply = "Thanks",
            adminReplyAt = "2024-01-01",
            createdAt = "2023-12-31"
        )
        val domain = dto.toDomain()

        assertEquals(dto.id, domain.id)
        assertEquals(dto.userName, domain.userName)
        assertEquals(dto.userAvatar, domain.userAvatar)
        assertEquals(dto.rating, domain.rating)
        assertEquals(dto.comment, domain.comment)
        assertEquals(dto.adminReply, domain.adminReply)
        assertEquals(dto.adminReplyAt, domain.adminReplyAt)
        assertEquals(dto.createdAt, domain.createdAt)
    }

    @Test
    fun branchDtoToDomain_mapsCorrectlyWithNulls() {
        val dto = BranchDto(
            id = "b1",
            address = "Street 1",
            phone = "123",
            openingHours = "9-18",
            photoUrl = "photo"
        )
        val domain = dto.toDomain()

        assertEquals(dto.id, domain.id)
        assertEquals(0, domain.services.size)
        assertEquals(0, domain.workingHours.size)
        assertEquals(0, domain.reviewsCount)
        assertEquals(0.0, domain.averageRating, 0.001)
        assertEquals(0, domain.latestReviews.size)
        assertEquals(false, domain.isOpenNow)
    }
}
