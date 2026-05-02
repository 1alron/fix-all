package io.alron.fixall.data.repository

import io.alron.fixall.data.remote.api.AdminApi
import io.alron.fixall.data.remote.dto.*
import io.alron.fixall.domain.model.*
import io.alron.fixall.domain.repository.AdminRepository
import io.alron.fixall.presentation.util.DateTimeUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepositoryImpl @Inject constructor(
    private val api: AdminApi
) : AdminRepository {
    override suspend fun getDashboardStats(): Result<AdminDashboardStats> {
        return try {
            val dto = api.getDashboardStats()
            Result.success(
                AdminDashboardStats(
                    totalAppointments = dto.totalAppointments,
                    activeServices = dto.activeServices,
                    totalClients = dto.totalClients,
                    totalCenters = dto.totalCenters,
                    upcoming = dto.upcoming.map { appointmentDto ->
                        AdminAppointment(
                            id = appointmentDto.id,
                            date = DateTimeUtils.formatDate(appointmentDto.date),
                            time = DateTimeUtils.formatTime(appointmentDto.time),
                            client = appointmentDto.client,
                            service = appointmentDto.service,
                            car = appointmentDto.car,
                            center = appointmentDto.center,
                            status = appointmentDto.status,
                            statusDisplay = appointmentDto.statusDisplay
                        )
                    }
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getStatusStats(period: String): Result<AdminStatusStats> {
        return try {
            val dto = api.getStatusStats(period)
            Result.success(
                AdminStatusStats(
                    period = dto.period,
                    statuses = dto.statuses.mapValues { StatusInfo(it.value.label, it.value.count) },
                    total = dto.total
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAttendanceStats(period: String): Result<AdminAttendanceStats> {
        return try {
            val dto = api.getAttendanceStats(period)
            Result.success(
                AdminAttendanceStats(
                    period = dto.period,
                    centers = dto.centers.map { 
                        CenterAttendance(it.id, it.address, it.count)
                    }
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getServicePopularity(period: String): Result<AdminServicePopularity> {
        return try {
            val dto = api.getServicePopularity(period)
            Result.success(
                AdminServicePopularity(
                    period = dto.period,
                    services = dto.services.map { 
                        ServicePopularityItem(it.name, it.count, it.percentage)
                    }
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAppointments(filters: Map<String, String>): Result<List<AdminAppointmentListItem>> {
        return try {
            val results = api.getAppointments(filters)
            Result.success(
                results.map {
                    AdminAppointmentListItem(
                        id = it.id,
                        scheduledDate = DateTimeUtils.formatDate(it.scheduledDate),
                        scheduledTime = DateTimeUtils.formatTime(it.scheduledTime),
                        carInfo = it.carInfo,
                        clientName = it.clientName,
                        clientEmail = it.clientEmail,
                        serviceName = it.serviceName,
                        centerAddress = it.centerAddress,
                        status = it.status,
                        statusDisplay = it.statusDisplay,
                        notes = it.notes
                    )
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAppointmentDetail(id: String): Result<AdminAppointmentDetail> {
        return try {
            val dto = api.getAppointmentDetail(id)
            Result.success(
                AdminAppointmentDetail(
                    id = dto.id,
                    scheduledDate = DateTimeUtils.formatDate(dto.scheduledDate),
                    scheduledTime = DateTimeUtils.formatTime(dto.scheduledTime),
                    endTime = dto.endTime,
                    carInfo = dto.carInfo,
                    clientName = dto.clientName,
                    clientEmail = dto.clientEmail,
                    clientPhone = dto.clientPhone,
                    serviceName = dto.serviceName,
                    serviceDuration = dto.serviceDuration,
                    centerAddress = dto.centerAddress,
                    status = dto.status,
                    statusDisplay = dto.statusDisplay,
                    notes = dto.notes,
                    vin = dto.vin,
                    totalPrice = dto.totalPrice,
                    isPaid = dto.isPaid,
                    paymentStatus = dto.paymentStatus,
                    paymentInfo = dto.paymentInfo?.let { 
                        AdminPaymentInfo(it.status, it.paidAt, it.amount)
                    },
                    createdAt = dto.createdAt,
                    updatedAt = dto.updatedAt
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun changeStatus(id: String, status: String): Result<Unit> {
        return try {
            api.changeStatus(id, ChangeStatusRequestDto(status))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addNote(id: String, note: String): Result<AddNoteResponse> {
        return try {
            val response = api.addNote(id, AddNoteRequestDto(note))
            Result.success(AddNoteResponse(response.success, response.notes))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getReviews(filters: Map<String, String>): Result<List<AdminReviewListItemDto>> {
        return try {
            val response = api.getReviews(filters)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun replyToReview(id: String, reply: String): Result<AdminReplyResponseDto> {
        return try {
            val response = api.replyToReview(id, AdminReplyRequestDto(reply))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteReview(id: String): Result<DeleteReviewResponseDto> {
        return try {
            val response = api.deleteReview(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getServices(filters: Map<String, String>): Result<List<AdminService>> {
        return try {
            val results = api.getServices(filters)
            Result.success(results.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getServiceDetail(id: String): Result<AdminService> {
        return try {
            val response = api.getServiceDetail(id)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createService(request: CreateUpdateServiceRequestDto): Result<AdminService> {
        return try {
            val response = api.createService(request)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateService(id: String, request: CreateUpdateServiceRequestDto): Result<AdminService> {
        return try {
            val response = api.updateService(id, request)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteService(id: String): Result<Unit> {
        return try {
            api.deleteService(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleServiceActive(id: String): Result<ToggleActiveResponseDto> {
        return try {
            val response = api.toggleServiceActive(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun AdminServiceItemDto.toDomain() = AdminService(
        id = id,
        name = name,
        description = description,
        duration = duration,
        price = price,
        serviceCenterId = serviceCenterId,
        centerAddress = centerAddress,
        isActive = isActive
    )
}
