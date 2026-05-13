package io.alron.fixall.presentation.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class DateTimeUtilsExtendedTest {

    @Test
    fun `millisToApiDate returns correctly formatted string in UTC`() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(2024, Calendar.MAY, 20, 10, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        val result = DateTimeUtils.millisToApiDate(calendar.timeInMillis)
        
        assertEquals("2024-05-20", result)
    }

    @Test
    fun `millisToApiDate handles end of year correctly`() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(2023, Calendar.DECEMBER, 31)
        }
        
        val result = DateTimeUtils.millisToApiDate(calendar.timeInMillis)
        
        assertEquals("2023-12-31", result)
    }
}
