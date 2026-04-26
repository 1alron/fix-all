package io.alron.fixall.data.repository

import io.alron.fixall.data.remote.api.AdminApi
import io.alron.fixall.domain.model.AdminAppointment
import io.alron.fixall.domain.model.AdminDashboardStats
import io.alron.fixall.domain.model.AdminStatusStats
import io.alron.fixall.domain.model.StatusInfo
import io.alron.fixall.domain.repository.AdminRepository
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
                            date = appointmentDto.date,
                            time = appointmentDto.time,
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
}
