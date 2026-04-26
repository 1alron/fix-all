package io.alron.fixall.presentation.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateTimeUtils {
    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    
    private val fullDateTimeFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    fun formatDate(dateString: String?): String {
        if (dateString.isNullOrBlank()) return ""
        return try {
            if (dateString.contains("-")) {
                val parts = dateString.split("-")
                if (parts.size == 3) {
                    "${parts[2]}.${parts[1]}.${parts[0]}"
                } else {
                    dateString
                }
            } else {
                dateString
            }
        } catch (e: Exception) {
            dateString
        }
    }

    fun formatTime(timeString: String?): String {
        if (timeString.isNullOrBlank()) return ""
        return try {
            val parts = timeString.split(":")
            if (parts.size >= 2) {
                "${parts[0]}:${parts[1]}"
            } else {
                timeString
            }
        } catch (e: Exception) {
            timeString
        }
    }

    fun formatFullDateTime(dateTimeString: String?): String {
        if (dateTimeString.isNullOrBlank()) return ""
        return try {
            val date = isoFormat.parse(dateTimeString.substring(0, 19))
            date?.let { fullDateTimeFormat.format(it) } ?: dateTimeString
        } catch (e: Exception) {
            dateTimeString
        }
    }

    fun millisToApiDate(millis: Long): String {
        return apiDateFormat.format(Date(millis))
    }
}
