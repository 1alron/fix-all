package io.alron.fixall.data.repository

import io.alron.fixall.data.remote.api.AdminApi
import io.alron.fixall.data.remote.dto.*
import io.alron.fixall.domain.model.*
import io.alron.fixall.domain.repository.AdminRepository
import io.alron.fixall.presentation.util.DateTimeUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
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

    override suspend fun getStatusStats(period: String, centerId: String?): Result<AdminStatusStats> {
        return try {
            val dto = api.getStatusStats(period, centerId)
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

    override suspend fun getAttendanceStats(period: String, centerId: String?): Result<AdminAttendanceStats> {
        return try {
            val dto = api.getAttendanceStats(period, centerId)
            Result.success(
                AdminAttendanceStats(
                    period = dto.period,
                    centers = dto.centers.map {
                        CenterAttendance(it.id, it.address, it.count)
                    },
                    daily = dto.daily?.map {
                        AdminDailyAttendance(it.date, it.label, it.count)
                    }
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBranchAttendanceStats(centerId: String, period: String): Result<BranchAttendanceStats> {
        return try {
            val dto = api.getBranchAttendanceStats(centerId, period)
            Result.success(
                BranchAttendanceStats(
                    centerId = dto.centerId,
                    period = dto.period,
                    labels = dto.labels,
                    data = dto.data
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getServicePopularity(period: String, centerId: String?): Result<AdminServicePopularity> {
        return try {
            val dto = api.getServicePopularity(period, centerId)
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

    override suspend fun getUniqueServiceNames(): Result<List<String>> {
        return try {
            val results = api.getUniqueServices()
            Result.success(results)
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

    override suspend fun getBranches(): Result<List<AdminBranch>> {
        return try {
            val results = api.getBranches()
            Result.success(results.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBranchDetail(id: String): Result<AdminBranch> {
        return try {
            val response = api.getBranchDetail(id)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createBranch(address: String, phone: String, openingHours: String): Result<AdminBranch> {
        return try {
            val response = api.createBranch(CreateUpdateBranchRequestDto(address, phone, openingHours))
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateBranch(id: String, address: String, phone: String, openingHours: String): Result<AdminBranch> {
        return try {
            val response = api.updateBranch(id, CreateUpdateBranchRequestDto(address, phone, openingHours))
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteBranch(id: String): Result<Unit> {
        return try {
            api.deleteBranch(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateBranchPhoto(id: String, photoFile: File): Result<String> {
        return try {
            val requestFile = photoFile.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("photo", photoFile.name, requestFile)
            val response = api.updateBranchPhoto(id, body)
            Result.success(response.photoUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBranchWorkingHours(id: String): Result<List<AdminWorkingHour>> {
        return try {
            val results = api.getBranchWorkingHours(id)
            Result.success(results.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setBranchWorkingHours(id: String, workingHour: AdminWorkingHour): Result<AdminWorkingHour> {
        return try {
            val response = api.setBranchWorkingHours(
                id,
                SetWorkingHoursRequestDto(
                    dayOfWeek = workingHour.dayOfWeek,
                    startTime = workingHour.startTime,
                    endTime = workingHour.endTime,
                    lunchStart = workingHour.lunchStart,
                    lunchEnd = workingHour.lunchEnd,
                    isWorking = workingHour.isWorking
                )
            )
            if (response.success) {
                Result.success(response.data?.toDomain() ?: workingHour)
            } else {
                Result.failure(Exception(response.message ?: "Failed to set working hours"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getClients(filters: Map<String, String>): Result<List<AdminClientListItemDto>> {
        return try {
            Result.success(api.getClients(filters))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getClientDetail(id: Int): Result<AdminClientDetail> {
        return try {
            val dto = api.getClientDetail(id)
            Result.success(AdminClientDetail(
                id = dto.id,
                username = dto.username,
                fullName = dto.fullName,
                email = dto.email,
                phone = dto.phone,
                address = dto.address,
                dateJoined = dto.dateJoined,
                isStaff = dto.isStaff,
                carsCount = dto.carsCount,
                appointmentsCount = dto.appointmentsCount,
                totalPaid = dto.totalPaid,
                cars = dto.cars.map { AdminClientCar(it.id, it.name, it.licensePlate, it.year) },
                recentAppointments = dto.recentAppointments.map { 
                    AdminClientAppointment(it.id, it.scheduledDate, it.scheduledTime, it.serviceName, it.centerAddress, it.status, it.statusDisplay)
                },
                topServices = dto.topServices.map { AdminClientStatItem(it.name, it.count) },
                topCenters = dto.topCenters.map { AdminClientStatItem(it.name, it.count) },
                weekdayCounts = dto.weekdayCounts,
                hourCounts = dto.hourCounts
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createClient(request: CreateClientRequestDto): Result<AdminClient> {
        return try {
            val dto = api.createClient(request)
            Result.success(AdminClient(
                id = dto.id,
                username = dto.username,
                fullName = dto.fullName,
                email = dto.email,
                phone = dto.phone,
                address = dto.address,
                carsCount = dto.carsCount,
                appointmentsCount = dto.appointmentsCount,
                activeAppointmentsCount = dto.activeAppointmentsCount,
                dateJoined = dto.dateJoined,
                isStaff = dto.isStaff
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateClient(id: Int, request: UpdateClientRequestDto): Result<Unit> {
        return try {
            api.updateClient(id, request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteClient(id: Int): Result<Unit> {
        return try {
            api.deleteClient(id)
            Result.success(Unit)
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

    private fun AdminBranchListItemDto.toDomain() = AdminBranch(
        id = id,
        address = address,
        phone = phone,
        openingHours = openingHours,
        photo = photo ?: photoUrl,
        servicesCount = servicesCount,
        workingHours = workingHours?.map { it.toDomain() } ?: emptyList()
    )

    private fun AdminWorkingHourDto.toDomain() = AdminWorkingHour(
        id = id,
        dayOfWeek = dayOfWeek,
        dayDisplay = dayDisplay,
        startTime = startTime,
        endTime = endTime,
        lunchStart = lunchStart,
        lunchEnd = lunchEnd,
        isWorking = isWorking
    )
}
