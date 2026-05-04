package io.alron.fixall.domain.model

data class Branch(
    val id: String,
    val address: String,
    val phone: String,
    val openingHours: String,
    val photoUrl: String,
    val services: List<Service> = emptyList(),
    val workingHours: List<WorkingHour> = emptyList(),
    val reviewsCount: Int = 0,
    val averageRating: Double = 0.0,
    val latestReviews: List<Review> = emptyList(),
    val isOpenNow: Boolean = false
)

data class WorkingHour(
    val id: Int,
    val dayOfWeek: Int,
    val dayOfWeekDisplay: String,
    val startTime: String,
    val endTime: String,
    val lunchStart: String?,
    val lunchEnd: String?,
    val isWorking: Boolean
)
