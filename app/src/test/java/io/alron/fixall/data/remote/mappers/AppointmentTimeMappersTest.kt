package io.alron.fixall.data.remote.mappers

import io.alron.fixall.data.remote.dto.AvailableTimeSlotsDto
import io.alron.fixall.data.remote.dto.WorkingHoursRangeDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AppointmentTimeMappersTest {

    @Test
    fun `availableTimeSlotsDto toDomain maps correctly`() {
        val dto = AvailableTimeSlotsDto(
            date = "2024-05-20",
            slots = listOf("09:00", "10:00", "11:00"),
            workingHours = WorkingHoursRangeDto(
                start = "09:00",
                end = "18:00",
                lunchStart = "13:00",
                lunchEnd = "14:00"
            )
        )
        val domain = dto.toDomain()

        assertEquals("2024-05-20", domain.date)
        assertEquals(3, domain.slots.size)
        assertEquals("09:00", domain.workingHours.start)
        assertEquals("13:00", domain.workingHours.lunchStart)
    }

    @Test
    fun `availableTimeSlotsDto with nulls returns default objects`() {
        val dto = AvailableTimeSlotsDto(null, null, null)
        val domain = dto.toDomain()

        assertEquals("", domain.date)
        assertEquals(0, domain.slots.size)
        assertEquals("", domain.workingHours.start)
        assertNull(domain.workingHours.lunchStart)
    }

    @Test
    fun `workingHoursRangeDto toDomain maps correctly`() {
        val dto = WorkingHoursRangeDto("08:00", "20:00", null, null)
        val domain = dto.toDomain()

        assertEquals("08:00", domain.start)
        assertEquals("20:00", domain.end)
        assertNull(domain.lunchStart)
        assertNull(domain.lunchEnd)
    }
}
