package io.alron.fixall.domain.model

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val isAdmin: Boolean = false,
    val profile: UserProfile
)

data class UserProfile(
    val phone: String?,
    val address: String?,
    val avatarUrl: String?
)

data class UserStats(
    val totalAppointments: Int,
    val completedAppointments: Int,
    val cancelledAppointments: Int,
    val totalSpent: Double,
    val averageCheck: Double,
    val firstVisit: String?,
    val lastVisit: String?,
    val visitsByMonth: Map<String, Int>,
    val topServices: List<StatItem>,
    val topCenters: List<StatItem>,
    val byWeekday: List<Int>,
    val byHour: List<Int>
)

data class StatItem(
    val name: String,
    val count: Int
)

data class LoyaltyInfo(
    val status: String,
    val statusDisplay: String,
    val bonusBalance: Double,
    val totalSpent: Double,
    val nextStatus: String?,
    val nextStatusProgress: Double,
    val personalDiscount: Double,
    val statusDiscount: Double,
    val totalDiscount: Double
)
