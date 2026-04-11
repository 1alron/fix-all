package io.alron.fixall.data.remote.api

import io.alron.fixall.data.remote.dto.*
import retrofit2.http.*

interface AppointmentsApi {
    @GET("/api/appointments/")
    suspend fun getAppointments(): List<AppointmentDto>

    @POST("/api/appointments/")
    suspend fun createAppointment(@Body request: CreateAppointmentRequestDto): AppointmentDto

    @GET("/api/appointments/{id}/")
    suspend fun getAppointmentDetails(@Path("id") id: String): AppointmentDto

    @POST("/api/appointments/{id}/cancel/")
    suspend fun cancelAppointment(@Path("id") id: String): CancelAppointmentResponseDto

    @GET("/api/appointments/upcoming/")
    suspend fun getUpcomingAppointment(): AppointmentDto

    @GET("/api/appointments/history/")
    suspend fun getAppointmentHistory(@Query("page") page: Int = 1): List<AppointmentDto>

    @GET("/api/appointments/available_services/")
    suspend fun getAvailableServices(@Query("service_center_id") serviceCenterId: String): List<ServiceDto>

    @GET("/api/appointments/available_time_slots/")
    suspend fun getAvailableTimeSlots(
        @Query("service_center_id") serviceCenterId: String,
        @Query("service_type_id") serviceTypeId: String,
        @Query("date") date: String
    ): AvailableTimeSlotsDto
}
