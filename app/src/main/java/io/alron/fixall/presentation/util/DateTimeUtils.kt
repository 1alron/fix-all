package io.alron.fixall.presentation.util

object DateTimeUtils {
    fun formatDate(dateString: String?): String {
        if (dateString.isNullOrBlank()) return ""
        return try {
            val parts = dateString.split("-")
            if (parts.size == 3) {
                "${parts[2]}.${parts[1]}.${parts[0]}"
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
}
