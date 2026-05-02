package io.alron.fixall.domain.model

data class AdminClient(
    val id: Int,
    val username: String,
    val fullName: String,
    val email: String,
    val phone: String?,
    val address: String? = null,
    val carsCount: Int,
    val appointmentsCount: Int,
    val activeAppointmentsCount: Int,
    val dateJoined: String,
    val isStaff: Boolean
)

data class AdminClientDetail(
    val id: Int,
    val username: String,
    val fullName: String,
    val email: String,
    val phone: String?,
    val address: String?,
    val dateJoined: String,
    val isStaff: Boolean,
    val carsCount: Int,
    val appointmentsCount: Int,
    val totalPaid: Double,
    val cars: List<AdminClientCar>,
    val recentAppointments: List<AdminClientAppointment>,
    val topServices: List<AdminClientStatItem>,
    val topCenters: List<AdminClientStatItem>,
    val weekdayCounts: List<Int>,
    val hourCounts: List<Int>
)

data class AdminClientCar(
    val id: String,
    val name: String,
    val licensePlate: String,
    val year: Int
)

data class AdminClientAppointment(
    val id: String,
    val scheduledDate: String,
    val scheduledTime: String,
    val serviceName: String,
    val centerAddress: String,
    val status: String,
    val statusDisplay: String
)

data class AdminClientStatItem(
    val name: String,
    val count: Int
)
