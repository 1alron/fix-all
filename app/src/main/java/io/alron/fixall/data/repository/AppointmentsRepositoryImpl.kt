package io.alron.fixall.data.repository

import io.alron.fixall.data.remote.api.AppointmentsApi
import io.alron.fixall.data.remote.dto.*
import io.alron.fixall.data.remote.mappers.toDomain
import io.alron.fixall.domain.model.Appointment
import io.alron.fixall.domain.model.Service
import io.alron.fixall.domain.model.TimeSlots
import io.alron.fixall.domain.repository.AppointmentsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class AppointmentsRepositoryImpl @Inject constructor(
    private val api: AppointmentsApi
) : AppointmentsRepository {

    private val _appointments = MutableSharedFlow<List<Appointment>>(replay = 1)
    override val appointments: Flow<List<Appointment>> = _appointments.onStart { getAppointments() }

    private val _upcomingAppointment = MutableSharedFlow<Appointment?>(replay = 1)
    override val upcomingAppointment: Flow<Appointment?> =
        _upcomingAppointment.onStart { getUpcomingAppointment() }

    override suspend fun getAppointments(): Result<List<Appointment>> {
        return try {
            val response = api.getAppointments()
            val domainList = response.map { it.toDomain() }
            _appointments.emit(domainList)
            Result.success(domainList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createAppointment(
        carId: String,
        serviceTypeId: String,
        serviceCenterId: String,
        scheduledDate: String,
        scheduledTime: String,
        notes: String?
    ): Result<Appointment> {
        return try {
            val request = CreateAppointmentRequestDto(
                carId = carId,
                serviceTypeId = serviceTypeId,
                serviceCenterId = serviceCenterId,
                scheduledDate = scheduledDate,
                scheduledTime = scheduledTime,
                notes = notes
            )
            val response = api.createAppointment(request)
            val domain = response.toDomain()
            getAppointments()
            getUpcomingAppointment()
            Result.success(domain)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAppointmentDetails(id: String): Result<Appointment> {
        return try {
            val response = api.getAppointmentDetails(id)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelAppointment(id: String): Result<String> {
        return try {
            val response = api.cancelAppointment(id)
            if (response.success == true) {
                getAppointments()
                getUpcomingAppointment()
                Result.success(response.message ?: "Запись отменена")
            } else {
                Result.failure(Exception(response.error ?: "Ошибка при отмене записи"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUpcomingAppointment(): Result<Appointment?> {
        return try {
            val response = api.getUpcomingAppointment()
            val domain = if (response.id != null) response.toDomain() else null
            _upcomingAppointment.emit(domain)
            Result.success(domain)
        } catch (e: Exception) {
            _upcomingAppointment.emit(null)
            Result.failure(e)
        }
    }

    override suspend fun getAppointmentHistory(page: Int): Result<List<Appointment>> {
        return try {
            val response = api.getAppointmentHistory(page)
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAvailableServices(serviceCenterId: String): Result<List<Service>> {
        return try {
            val response = api.getAvailableServices(serviceCenterId)
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAvailableTimeSlots(
        serviceCenterId: String,
        serviceTypeId: String,
        date: String
    ): Result<TimeSlots> {
        return try {
            val response = api.getAvailableTimeSlots(serviceCenterId, serviceTypeId, date)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun initiatePayment(appointmentId: String, bonusAmount: Double): Result<String> {
        return try {
            val response = api.initiatePayment(appointmentId, PaymentRequestDto(bonusAmount))
            if (response.paymentUrl != null) {
                Result.success(response.paymentUrl)
            } else {
                Result.failure(Exception("Payment URL not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPaymentStatus(id: String): Result<PaymentStatusDto> {
        return try {
            val response = api.getPaymentStatus(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncPaymentStatus(id: String): Result<SyncPaymentStatusResponseDto> {
        return try {
            val response = api.syncPaymentStatus(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
