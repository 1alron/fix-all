package io.alron.fixall.presentation.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.TimeZone
import java.util.Locale

class DateTimeUtilsTest {

    @Before
    fun setup() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        Locale.setDefault(Locale.US)
    }

    @Test
    fun formatDate_withValidIsoDate_returnsDotsFormat() {
        val input = "2024-05-20"
        val expected = "20.05.2024"
        assertEquals(expected, DateTimeUtils.formatDate(input))
    }

    @Test
    fun formatDate_withNull_returnsEmptyString() {
        assertEquals("", DateTimeUtils.formatDate(null))
    }

    @Test
    fun formatDate_withBlank_returnsEmptyString() {
        assertEquals("", DateTimeUtils.formatDate("   "))
    }

    @Test
    fun formatDate_withThreePartsNonIso_reversesParts() {
        val input = "20-05-2024"
        val expected = "2024.05.20"
        assertEquals(expected, DateTimeUtils.formatDate(input))
    }

    @Test
    fun formatDate_withInvalidPartsCount_returnsOriginalString() {
        val input = "2024-05"
        assertEquals(input, DateTimeUtils.formatDate(input))
    }

    @Test
    fun formatTime_withFullTime_returnsHoursAndMinutes() {
        val input = "14:30:15"
        val expected = "14:30"
        assertEquals(expected, DateTimeUtils.formatTime(input))
    }

    @Test
    fun formatTime_withShortTime_returnsOriginalString() {
        val input = "14:30"
        assertEquals("14:30", DateTimeUtils.formatTime(input))
    }

    @Test
    fun formatTime_withNull_returnsEmptyString() {
        assertEquals("", DateTimeUtils.formatTime(null))
    }

    @Test
    fun formatFullDateTime_withIsoDateTime_returnsReadableFormat() {
        val input = "2024-05-20T14:30:00"
        val result = DateTimeUtils.formatFullDateTime(input)
        
        assertTrue("Result '$result' should contain date part", result.contains("20.05.2024"))
    }
}
