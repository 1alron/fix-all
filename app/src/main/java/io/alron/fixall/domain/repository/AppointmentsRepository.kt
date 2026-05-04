package io.alron.fixall.domain.repository

import io.alron.fixall.data.remote.dto.PaymentStatusDto
import io.alron.fixall.data.remote.dto.SyncPaymentStatusResponseDto
import io.alron.fixall.domain.model.Appointment
import io.alron.fixall.domain.model.Service
import io.alron.fixall.domain.model.TimeSlots
import kotlinx.coroutines.flow.Flow

interface AppointmentsRepository {
    val appointments: Flow<List<Appointment>>
    val upcomingAppointment: Flow<Appointment?>

    suspend fun getAppointments(): Result<List<Appointment>>
    suspend fun createAppointment(
        carId: String,
        serviceTypeId: String,
        serviceCenterId: String,
        scheduledDate: String,
        scheduledTime: String,
        notes: String?
    ): Result<Appointment>
    suspend fun getAppointmentDetails(id: String): Result<Appointment>
    suspend fun cancelAppointment(id: String): Result<String>
    suspend fun getUpcomingAppointment(): Result<Appointment?>
    suspend fun getAppointmentHistory(page: Int = 1): Result<List<Appointment>>
    suspend fun getAvailableServices(serviceCenterId: String): Result<List<Service>>
    suspend fun getAvailableTimeSlots(
        serviceCenterId: String,
        serviceTypeId: String,
        date: String
    ): Result<TimeSlots>
    suspend fun initiatePayment(appointmentId: String, bonusAmount: Double): Result<String>
    suspend fun getPaymentStatus(id: String): Result<PaymentStatusDto>
    suspend fun syncPaymentStatus(id: String): Result<SyncPaymentStatusResponseDto>
}
