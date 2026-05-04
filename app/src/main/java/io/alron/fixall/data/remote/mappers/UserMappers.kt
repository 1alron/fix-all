package io.alron.fixall.data.remote.mappers

import io.alron.fixall.data.remote.dto.*
import io.alron.fixall.domain.model.*

fun UserDto.toDomain() = User(
    id = id,
    username = username,
    email = email,
    firstName = firstName,
    lastName = lastName,
    fullName = fullName,
    isAdmin = isStaff,
    profile = profile.toDomain()
)

fun UserProfileDto.toDomain() = UserProfile(
    phone = phone,
    address = address,
    avatarUrl = avatarUrl
)

fun UserStatsDto.toDomain() = UserStats(
    totalAppointments = totalAppointments,
    completedAppointments = completedAppointments,
    cancelledAppointments = cancelledAppointments,
    totalSpent = totalSpent,
    averageCheck = averageCheck,
    firstVisit = firstVisit,
    lastVisit = lastVisit,
    visitsByMonth = visitsByMonth,
    topServices = topServices.map { it.toDomain() },
    topCenters = topCenters.map { it.toDomain() },
    byWeekday = byWeekday,
    byHour = byHour
)

fun StatItemDto.toDomain() = StatItem(
    name = name,
    count = count
)

fun LoyaltyInfoDto.toDomain() = LoyaltyInfo(
    status = status,
    statusDisplay = statusDisplay,
    bonusBalance = bonusBalance,
    totalSpent = totalSpent,
    nextStatus = nextStatus,
    nextStatusProgress = nextStatusProgress,
    personalDiscount = personalDiscount,
    statusDiscount = statusDiscount,
    totalDiscount = totalDiscount
)
